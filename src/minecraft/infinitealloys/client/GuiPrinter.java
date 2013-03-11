package infinitealloys.client;

import infinitealloys.inventory.ContainerPrinter;
import infinitealloys.tile.TileEntityPrinter;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiPrinter extends GuiMachine {

	public GuiPrinter(InventoryPlayer inventoryPlayer, TileEntityPrinter tileEntity) {
		super(176, 148, tileEntity, new ContainerPrinter(inventoryPlayer, tileEntity), "printer");
		progressBar.setLocation(31, 14);
	}
}
