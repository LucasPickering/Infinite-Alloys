package infinitealloys.inventory;

import infinitealloys.tile.TileEntityMachine;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUpgrade extends Slot {

	public SlotUpgrade(TileEntityMachine tem, int index, int x, int y) {
		super(tem, index, x, y);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return ((TileEntityMachine)inventory).isUpgradeValid(itemstack);
	}
}
