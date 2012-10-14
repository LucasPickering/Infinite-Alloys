package infinitealloys;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.TileEntityFurnace;

public class SlotMetalForge extends Slot {

	private int slotIndex;

	public SlotMetalForge(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public int getSlotStackLimit() {
		if(slotIndex >= 1 && slotIndex <= 9)
			return 8;
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		int ingotNum = ((TileEntityMetalForge)inventory).getIngotNum(itemstack);
		return slotIndex == 0 && TileEntityFurnace.isItemFuel(itemstack) || (slotIndex > 11 && ingotNum != -1);
	}
}