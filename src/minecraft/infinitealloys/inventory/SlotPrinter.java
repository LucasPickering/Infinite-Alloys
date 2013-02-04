package infinitealloys.inventory;

import infinitealloys.item.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotPrinter extends Slot {

	private int slotIndex;

	public SlotPrinter(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		boolean isBook = itemstack.itemID == Items.alloyIngot.itemID || itemstack.itemID == Item.writableBook.itemID || itemstack.itemID == Item.writtenBook.itemID && itemstack.hasTagCompound();
		return (slotIndex == 0 && isBook) || (slotIndex == 1 && itemstack.itemID == Item.book.itemID);
	}
}
