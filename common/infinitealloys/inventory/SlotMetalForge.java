package infinitealloys.inventory;

import infinitealloys.tile.TEEMetalForge;
import net.minecraft.inventory.IInventory;

public class SlotMetalForge extends SlotMachine {

	private final int slotIndex;

	public SlotMetalForge(IInventory inventory, int index, int x, int y, int temID) {
		super(inventory, temID, index, x, y);
		slotIndex = index;
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		if(slotIndex == 0)
			((TEEMetalForge)inventory).presetSelection = -1;
	}
}