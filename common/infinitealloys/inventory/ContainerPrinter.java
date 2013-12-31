package infinitealloys.inventory;

import infinitealloys.tile.TEEPrinter;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerPrinter extends ContainerMachine {

	public TEEPrinter inventory;

	public ContainerPrinter(InventoryPlayer inventoryPlayer, TEEPrinter tileEntity) {
		super(tileEntity, 4);
		inventory = tileEntity;

		addSlotToContainer(new SlotMachine(inventory, inventory.getID(), 0, 12, 44));
		addSlotToContainer(new SlotMachine(inventory, inventory.getID(), 1, 80, 44));
		addSlotToContainer(new SlotMachine(inventory, inventory.getID(), 2, 148, 44));

		initSlots(inventoryPlayer, 8, 66, 148, 6);
	}
}
