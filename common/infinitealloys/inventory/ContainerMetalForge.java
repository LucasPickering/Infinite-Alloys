package infinitealloys.inventory;

import infinitealloys.tile.TEEMetalForge;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerMetalForge extends ContainerMachine {

	public TEEMetalForge inventory;

	public ContainerMetalForge(InventoryPlayer inventoryPlayer, TEEMetalForge tileEntity) {
		super(tileEntity, 20);
		inventory = tileEntity;

		addSlotToContainer(new SlotMachine(inventory, inventory.getID(), 0, 148, 52));
		for(int y = 0; y < 2; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new SlotMachine(inventory, inventory.getID(), y * 9 + x + 1, x * 18 + 8, y * 18 + 82));

		initSlots(inventoryPlayer, 8, 134, 148, 8);
	}
}
