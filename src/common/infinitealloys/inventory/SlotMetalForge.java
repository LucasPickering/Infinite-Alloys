package infinitealloys.inventory;

import infinitealloys.tile.TileEntityMachine;
import infinitealloys.tile.TileEntityMetalForge;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SlotMetalForge extends Slot {

	private int slotIndex;

	public SlotMetalForge(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return slotIndex == 0 && TileEntityMachine.isAlloyBook(itemstack) || (slotIndex > 2 && ((TileEntityMetalForge)inventory).getIngotNum(itemstack) != -1);
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		if(slotIndex == 0)
			((TileEntityMetalForge)inventory).presetSelection = -1;
	}
}