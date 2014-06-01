package infinitealloys.inventory;

import infinitealloys.tile.TileEntityMachine;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerMetalForge extends ContainerMachine {

	public ContainerMetalForge(InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
		super(tileEntity, 20);

		addSlotToContainer(new SlotMachine(inventory, inventory.getEnumMachine(), 0, 148, 52));
		for(int y = 0; y < 2; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new SlotMachine(inventory, inventory.getEnumMachine(), y * 9 + x + 1, x * 18 + 8, y * 18 + 82));

		initSlots(inventoryPlayer, 8, 134, 148, 8);
	}
}
