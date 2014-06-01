package infinitealloys.inventory;

import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAnalyzer extends ContainerMachine {

	public ContainerAnalyzer(InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
		super(tileEntity, 9);

		for(int x = 0; x < Consts.METAL_COUNT; x++)
			addSlotToContainer(new SlotMachine(inventory, inventory.getEnumMachine(), x, 26 + x * 18, 58));

		initSlots(inventoryPlayer, 8, 84, 151, 8);
	}
}
