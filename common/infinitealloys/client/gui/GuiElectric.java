package infinitealloys.client.gui;

import infinitealloys.tile.TileEntityElectric;
import infinitealloys.util.Funcs;
import java.text.DecimalFormat;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public abstract class GuiElectric extends GuiMachine {

	/** Coordinates of the progress bar texture, changes by machine but still otherwise */
	protected java.awt.Point progressBar = new java.awt.Point();

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

		// Draw the progress and energy info if the mouse is over the progress bar
		if(tee.ticksToProcess > 0 && Funcs.mouseInZone(mouseX, mouseY, topLeft.x + progressBar.x, topLeft.y + progressBar.y, PROGRESS_BAR.width, PROGRESS_BAR.height)) {
			int rkChange = tee.shouldProcess() ? tee.getRKChange() : 0;

			// The current process progress displayed as a percent
			String line1 = new DecimalFormat("0.0").format(tee.getProcessProgressScaled(100F)) + "%";

			// If the rk change is positive, add '+', then display the rate of change of RK
			String line2 = (rkChange > 0 ? "+" : "") + rkChange + " RK/t";

			// Draw all the information, with colors for the change based on pos/neg
			drawTextBox(mouseX, mouseY, new ColoredLine(line1, 0xffffff), new ColoredLine(line2, rkChange < 0 ? 0xff0000 : rkChange > 0 ? 0x00ff00 : 0xffffff));
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
			drawTexturedModalRect(progressBar.x, progressBar.y, PROGRESS_BAR.x, PROGRESS_BAR.y, (int)tee.getProcessProgressScaled(PROGRESS_BAR.width), PROGRESS_BAR.height);

		GL11.glPopMatrix();
	}

	@Override
	protected ColoredLine[] getNetworkStatuses() {
		int color;
		String status;

		if(tee.getEnergyHost() == null) {
			color = 0xff0000;
			status = Funcs.getLoc("machine.network.noconnection");
		}
		else {
			color = 0x00ff00;
			status = Funcs.getLoc("machine.network.hostedby") + " " + tee.getEnergyHost();
		}

		return new ColoredLine[] { new ColoredLine(Funcs.getLoc("machine.network.energy") + ": " + status, color) };
	}
}
