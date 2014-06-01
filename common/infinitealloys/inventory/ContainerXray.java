package infinitealloys.inventory;

import infinitealloys.tile.TileEntityMachine;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerXray extends ContainerMachine {

	public ContainerXray(InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
		super(tileEntity, 2);

		addSlotToContainer(new SlotMachine(inventory, inventory.getEnumMachine(), 0, 32, 6));

		initSlots(inventoryPlayer, 18, 158, 168, 6);
	}
}
