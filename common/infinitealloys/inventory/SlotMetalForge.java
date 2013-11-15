package infinitealloys.inventory;

import infinitealloys.tile.TEMMetalForge;
import net.minecraft.inventory.IInventory;

public class SlotMetalForge extends SlotUpgradable {

	private int slotIndex;

	public SlotMetalForge(IInventory inventory, int index, int x, int y, int teuID) {
		super(inventory, teuID, index, x, y);
		slotIndex = index;
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		if(slotIndex == 0)
			((TEMMetalForge)inventory).presetSelection = -1;
	}
}