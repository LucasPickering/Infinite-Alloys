package infinitealloys.inventory;

import infinitealloys.tile.TEEAnalyzer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAnalyzer extends ContainerMachine {

	public TEEAnalyzer inventory;

	public ContainerAnalyzer(InventoryPlayer inventoryPlayer, TEEAnalyzer tileEntity) {
		super(tileEntity, 9);
		inventory = tileEntity;

		for(int x = 0; x < 8; x++)
			addSlotToContainer(new SlotMachine(inventory, inventory.getID(), x, 17 + x * 18, 58));
		addSlotToContainer(new SlotMachine(inventory, inventory.getID(), 8, 125, 8));

		initSlots(inventoryPlayer, 8, 84, 151, 8);
	}
}
