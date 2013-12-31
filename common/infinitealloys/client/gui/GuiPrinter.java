package infinitealloys.client.gui;

import infinitealloys.tile.TEEPrinter;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiPrinter extends GuiElectric {

	public GuiPrinter(InventoryPlayer inventoryPlayer, TEEPrinter tileEntity) {
		super(176, 148, inventoryPlayer, tileEntity);
		progressBar.setLocation(31, 14);
		energyIcon.setLocation(83, 35);
	}
}
