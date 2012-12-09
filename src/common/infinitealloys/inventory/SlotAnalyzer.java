package infinitealloys.inventory;

import infinitealloys.InfiniteAlloys;
import infinitealloys.item.Items;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SlotAnalyzer extends Slot {

	private int slotIndex;

	public SlotAnalyzer(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return (slotIndex == 0 && itemstack.itemID == Items.alloyIngot.shiftedIndex) || (slotIndex == 2 && itemstack.itemID == Items.alloyBook.shiftedIndex);
	}
}
