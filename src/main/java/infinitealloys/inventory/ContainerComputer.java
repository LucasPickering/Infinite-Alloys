package infinitealloys.inventory;

import infinitealloys.tile.TileEntityMachine;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerComputer extends ContainerMachine {

	public ContainerComputer(InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
		super(inventoryPlayer, tileEntity, 8, 84, 140, 43);
	}
}
