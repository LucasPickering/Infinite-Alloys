package infinitealloys.client.gui;

import infinitealloys.tile.TEMEnergyStorage;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GuiEnergyStorage extends GuiMachine {

	public TEMEnergyStorage tees;
	private final java.awt.Point energyMeter = new java.awt.Point(19, 42);

	public GuiEnergyStorage(InventoryPlayer inventoryPlayer, TEMEnergyStorage tileEntity) {
		super(176, 166, inventoryPlayer, tileEntity);
		tees = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(extras);

		// Draw the energy meter. It uses the same texture as the progress bar.
		drawTexturedModalRect(energyMeter.x, energyMeter.y, PROGRESS_BAR.x, PROGRESS_BAR.y, tees.getCurrentRKScaled(PROGRESS_BAR.width), PROGRESS_BAR.height);

		// Draw the energy amount
		drawString(fontRenderer, tees.getCurrentRK() + "/" + tees.getMaxRK() + " RK", 12, 12, 0xffffff);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
