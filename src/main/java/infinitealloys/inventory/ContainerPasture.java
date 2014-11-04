package infinitealloys.inventory;

import infinitealloys.tile.TileEntityMachine;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerPasture extends ContainerMachine {

	public ContainerPasture(InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
		super(inventoryPlayer, tileEntity, 13, 94, 141, 44);
	}
}
