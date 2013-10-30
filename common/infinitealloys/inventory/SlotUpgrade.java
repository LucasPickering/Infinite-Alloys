package infinitealloys.inventory;

import infinitealloys.tile.TileEntityMachine;
import infinitealloys.tile.TileEntityUpgradable;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUpgrade extends Slot {

	public SlotUpgrade(TileEntityUpgradable teu, int index, int x, int y) {
		super(teu, index, x, y);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return ((TileEntityMachine)inventory).isUpgradeValid(itemstack);
	}
}
