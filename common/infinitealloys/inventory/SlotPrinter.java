package infinitealloys.inventory;

import infinitealloys.tile.TEHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPrinter extends Slot {

	private int slotIndex;

	public SlotPrinter(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return TEHelper.stackValidForSlot(TEHelper.PRINTER, slotIndex, itemstack);
	}
}
