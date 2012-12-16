package infinitealloys.inventory;

import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TileEntityMetalForge;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotMetalForge extends Slot {

	private int slotIndex;

	public SlotMetalForge(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return slotIndex == 0 && TEHelper.isAlloyBook(itemstack) || (slotIndex > 2 && ((TileEntityMetalForge)inventory).getIngotNum(itemstack) != -1);
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		if(slotIndex == 0)
			((TileEntityMetalForge)inventory).presetSelection = -1;
	}
}