package infinitealloys;

import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SlotUpgrade extends Slot {

	public SlotUpgrade(TileEntityMachine tem, int index, int x, int y) {
		super(tem, index, x, y);
	}

	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return ((TileEntityMachine)inventory).isUpgradeValid(itemstack);
	}
}
