package infinitealloys.inventory;

import infinitealloys.tile.TEEXray;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerXray extends ContainerMachine {

	public TEEXray inventory;

	public ContainerXray(InventoryPlayer inventoryPlayer, TEEXray tileEntity) {
		super(tileEntity, 2);
		inventory = tileEntity;

		addSlotToContainer(new SlotMachine(inventory, inventory.getID(), 0, 32, 6));

		initSlots(inventoryPlayer, 18, 156, 168, 6);
	}
}
