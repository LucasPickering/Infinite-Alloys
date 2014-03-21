package infinitealloys.client.gui;

import infinitealloys.tile.TileEntityElectric;
import infinitealloys.util.Funcs;
import infinitealloys.util.NetworkManager;
import infinitealloys.util.Point;
import java.text.DecimalFormat;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public abstract class GuiElectric extends GuiMachine {

	/** Coordinates of the progress bar texture, changes by machine but still otherwise */
	protected java.awt.Point progressBar = new java.awt.Point();

	/** Coordinates of the energy icon, that indicates power network status */
	protected java.awt.Point energyIcon = new java.awt.Point();

	protected TileEntityElectric tee;

	public GuiElectric(int xSize, int ySize, InventoryPlayer inventoryPlayer, TileEntityElectric tileEntity) {
		super(xSize, ySize, inventoryPlayer, tileEntity);
		tee = tileEntity;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		super.drawScreen(mouseX, mouseY, partialTick);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// Draw the progress info if the mouse is over the progress bar
		if(tee.ticksToProcess > 0)
			if(Funcs.mouseInZone(mouseX, mouseY, topLeft.x + progressBar.x, topLeft.y + progressBar.y, PROGRESS_BAR.width, PROGRESS_BAR.height))
				// Draw the progress as a percentage, rounded to the nearest tenth
				drawTextBox(mouseX, mouseY, new ColoredLine(new DecimalFormat("0.0").format(tee.getProcessProgressScaled(100F)) + "%", 0xffffff));

		// Draw the power network info if the mouse is over the energy icon
		if(Funcs.mouseInZone(mouseX, mouseY, topLeft.x + energyIcon.x, topLeft.y + energyIcon.y, ENERGY_ICON_ON.width, ENERGY_ICON_ON.height)) {
			if(tee.getEnergyNetworkID() != -1) {
				final int rkChange = tee.shouldProcess() ? tee.getRKChange() : 0;// The rate of change of RK
				final Point esu = NetworkManager.getHost(tee.getEnergyNetworkID()); // The coordinates of the energy storage unit

				// If the ESU for this machine is this machine, display SELF, otherwise display the coords of the ESU
				final String line1 = Funcs.getLoc("machine.connected.true") + (esu.equals(tee.xCoord, tee.yCoord, tee.zCoord) ? Funcs.getLoc("machine.connected.self") : esu);

				// If the rk change is positive, add '+', then display the rate of change of RK
				final String line2 = (rkChange > 0 ? "+" : "") + rkChange + " RK/t";

				// Draw all the information, with colors for the change based on pos/neg
				drawTextBox(mouseX, mouseY, new ColoredLine(line1, 0x00ff00), new ColoredLine(line2, rkChange < 0 ? 0xff0000 : rkChange > 0 ? 0x00ff00 : 0xffffff));
			}
			else
				drawTextBox(mouseX, mouseY, new ColoredLine(Funcs.getLoc("machine.connected.false"), 0xff0000));
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
		GL11.glPushMatrix();
		GL11.glTranslatef(topLeft.x, topLeft.y, 0);
		Funcs.bindTexture(extras);

		// Draw the progress bar overlay
		if(tee.ticksToProcess > 0)
			drawTexturedModalRect(progressBar.x, progressBar.y, PROGRESS_BAR.x, PROGRESS_BAR.y, (int)tee.getProcessProgressScaled(PROGRESS_BAR.width),
					PROGRESS_BAR.height);

		// Draw the energy icon overlay
		if(tee.getEnergyNetworkID() != -1)
			Funcs.drawTexturedModalRect(this, energyIcon.x, energyIcon.y, ENERGY_ICON_ON);
		else
			Funcs.drawTexturedModalRect(this, energyIcon.x, energyIcon.y, ENERGY_ICON_OFF);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
