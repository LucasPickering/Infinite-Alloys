package infinitealloys.client.gui;

import infinitealloys.tile.TEMEnergyStorage;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GuiEnergyStorage extends GuiMachine {

	public TEMEnergyStorage tees;
	private java.awt.Point energyMeter = new java.awt.Point(20, 27);

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

		// Draw the energy meter
		int meterHeight = tees.getCurrentRKScaled(ENERGY_METER.height);
		drawTexturedModalRect(energyMeter.x, energyMeter.y + ENERGY_METER.height - meterHeight, ENERGY_METER.x, ENERGY_METER.y, meterHeight, ENERGY_METER.height);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
