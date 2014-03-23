package infinitealloys.client.gui;

import infinitealloys.tile.TEEEnergyStorage;
import infinitealloys.util.Funcs;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GuiEnergyStorage extends GuiElectric {

	public TEEEnergyStorage tees;

	public GuiEnergyStorage(InventoryPlayer inventoryPlayer, TEEEnergyStorage tileEntity) {
		super(214, 176, inventoryPlayer, tileEntity);
		tees = tileEntity;
		progressBar.setLocation(70, 39);
		energyIcon.setLocation(31, 4);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glDisable(GL11.GL_LIGHTING);
		Funcs.bindTexture(extras);

		// Draw the energy amount
		drawString(fontRenderer, tees.getCurrentRK() + "/" + tees.getMaxRK() + " RK", 70, 28, 0xffffff);

		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
