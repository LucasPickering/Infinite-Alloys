package infinitealloys.inventory;

import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.util.Consts;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAnalyzer extends ContainerMachine {

	public TEEAnalyzer inventory;

	public ContainerAnalyzer(InventoryPlayer inventoryPlayer, TEEAnalyzer tileEntity) {
		super(tileEntity, 9);
		inventory = tileEntity;

		for(int x = 0; x < Consts.METAL_COUNT; x++)
			addSlotToContainer(new SlotMachine(inventory, inventory.getID(), x, 26 + x * 18, 58));

		initSlots(inventoryPlayer, 8, 84, 151, 8);
	}
}
