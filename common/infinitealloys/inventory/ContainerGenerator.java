package infinitealloys.inventory;

import infinitealloys.tile.TEEGenerator;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerGenerator extends ContainerMachine {

	public TEEGenerator inventory;

	public ContainerGenerator(InventoryPlayer inventoryPlayer, TEEGenerator tileEntity) {
		super(tileEntity, 10);
		inventory = tileEntity;

		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 3; x++)
				addSlotToContainer(new SlotMachine(inventory, inventory.getID(), x + y * 3, 13 + x * 18, 22 + y * 18));

		initSlots(inventoryPlayer, 27, 94, 185, 40);
	}
}
