package infinitealloys;

import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.TileEntityFurnace;

public class SlotPrinter extends Slot {

	private int slotIndex;

	public SlotPrinter(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return (slotIndex == 0 && itemstack.itemID == InfiniteAlloys.alloyIngot.shiftedIndex) || (slotIndex == 1 && itemstack.itemID == Item.book.shiftedIndex);
	}
}
