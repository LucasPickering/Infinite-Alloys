package infinitealloys;

import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SlotPrinter extends Slot {

	private int slotIndex;

	public SlotPrinter(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		boolean isBook = itemstack.itemID == InfiniteAlloys.alloyIngot.shiftedIndex || itemstack.itemID == Item.writableBook.shiftedIndex || itemstack.itemID == Item.writtenBook.shiftedIndex && itemstack.hasTagCompound();
		return (slotIndex == 0 && isBook) || (slotIndex == 1 && itemstack.itemID == Item.book.shiftedIndex);
	}
}
