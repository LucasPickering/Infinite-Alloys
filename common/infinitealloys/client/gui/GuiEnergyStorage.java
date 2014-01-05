package infinitealloys.client.gui;

import infinitealloys.tile.TEMEnergyStorage;
import infinitealloys.util.Funcs;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GuiEnergyStorage extends GuiElectric {

	public TEMEnergyStorage tees;

	public GuiEnergyStorage(InventoryPlayer inventoryPlayer, TEMEnergyStorage tileEntity) {
		super(214, 176, inventoryPlayer, tileEntity);
		tees = tileEntity;
		progressBar.setLocation(70, 39);
		energyIcon.setLocation(31, 4);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Funcs.bindTexture(extras);

		// Draw the energy amount
		drawString(fontRenderer, tees.getCurrentRK() + "/" + tees.getMaxRK() + " RK", 70, 28, 0xffffff);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
