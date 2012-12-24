package infinitealloys.inventory;

import infinitealloys.tile.TEHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotXray extends Slot {

	public SlotXray(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return TEHelper.isDetectable(itemstack);
	}
}
