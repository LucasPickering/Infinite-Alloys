package infinitealloys;

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
		return slotIndex == 0 && TileEntityMachine.isBook(itemstack) || (slotIndex > 10 && ((TileEntityMetalForge)inventory).getIngotNum(itemstack) != -1);
	}
}