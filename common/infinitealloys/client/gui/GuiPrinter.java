package infinitealloys.client.gui;

import infinitealloys.tile.TEMPrinter;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiPrinter extends GuiMachine {

	public GuiPrinter(InventoryPlayer inventoryPlayer, TEMPrinter tileEntity) {
		super(176, 148, inventoryPlayer, tileEntity);
		progressBar.setLocation(31, 14);
	}
}
