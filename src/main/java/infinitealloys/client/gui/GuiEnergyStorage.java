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
		progressBar.setLocation(70, 57);
		networkIcon = new java.awt.Point(31, 4);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
		GL11.glDisable(GL11.GL_LIGHTING);
		Funcs.bindTexture(extras);

		// Draw the energy amount
		drawString(fontRendererObj, Funcs.abbreviateNum(tees.getCurrentRK()) + "/" + Funcs.abbreviateNum(tees.getMaxRK()) + " RK", topLeft.x + 70, topLeft.y + 26, 0xffffff);

		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected ColoredLine[] getNetworkStatuses() {
		String status;
		int color = 0x00ff00;

		if(tees.isHostingNetwork()) {
			color = 0x0060ff;
			int clients = tees.getNetworkSize();

			// A string that says this TE is hosting a network and how many clients are connected
			status = Funcs.getLoc("machine.network.hosting") + " " + clients + " " +
					(clients == 1 ? Funcs.getLoc("machine.network.client") : Funcs.getLoc("machine.network.clients")); // A switch between "Client" and "Clients"
		}
		else
			status = Funcs.getLoc("machine.network.hostedby") + " " + tees.energyHost;

		return new ColoredLine[] { new ColoredLine(Funcs.getLoc("machine.network.energy") + ": " + status, color) };
	}
}
