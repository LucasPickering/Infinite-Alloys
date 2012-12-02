package infinitealloys.client;

import infinitealloys.ContainerXray;
import infinitealloys.TileEntityXray;
import net.minecraft.src.InventoryPlayer;

public class GuiXray extends GuiMachine {

	private TileEntityXray tex;

	public GuiXray(InventoryPlayer inventoryPlayer, TileEntityXray tileEntity) {
		super(176, 238, tileEntity, new ContainerXray(inventoryPlayer, tileEntity), "xray");
		tex = tileEntity;
	}
}
