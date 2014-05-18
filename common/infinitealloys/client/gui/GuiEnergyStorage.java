package infinitealloys.client.gui;

import infinitealloys.core.NetworkManager;
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
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glDisable(GL11.GL_LIGHTING);
		Funcs.bindTexture(extras);

		// Draw the energy amount
		drawString(fontRenderer, tees.getCurrentRK() + "/" + tees.getMaxRK() + " RK", 70, 26, 0xffffff);

		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected ColoredLine[] getNetworkStatuses() {
		boolean hosting = NetworkManager.getHost(tees.getEnergyNetworkID()).equals(tees.coords());
		String status;

		if(hosting) {
			int clients = NetworkManager.getSize(tees.getEnergyNetworkID());

			// A string that says this TE is hosting a network and how many clients are connected
			status = Funcs.getLoc("machine.network.hosting") + " " + clients + " " +
					(clients == 1 ? Funcs.getLoc("machine.network.client") : Funcs.getLoc("machine.network.clients")); // A switch between "Client" and
		}
		else 
			status = Funcs.getLoc("machine.network.hostedby") + " " + NetworkManager.getHost(tee.getEnergyNetworkID());

		return new ColoredLine[] { new ColoredLine(Funcs.getLoc("machine.network.energy") + ": " + status, 0x00ff00) };
	}
}
