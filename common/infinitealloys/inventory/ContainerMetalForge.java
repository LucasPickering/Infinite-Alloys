package infinitealloys.inventory;

import infinitealloys.tile.TEEMetalForge;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerMetalForge extends ContainerMachine {

	public TEEMetalForge inventory;

	public ContainerMetalForge(InventoryPlayer inventoryPlayer, TEEMetalForge tileEntity) {
		super(tileEntity, 21);
		inventory = tileEntity;

		addSlotToContainer(new SlotMetalForge(inventory, 0, 8, 52, inventory.getID()));
		addSlotToContainer(new SlotMetalForge(inventory, 1, 148, 52, inventory.getID()));
		for(int y = 0; y < 2; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new SlotMetalForge(inventory, y * 9 + x + 2, x * 18 + 8, y * 18 + 82, inventory.getID()));

		initSlots(inventoryPlayer, 8, 134, 148, 8);
	}
}
