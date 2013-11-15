package infinitealloys.inventory;

import infinitealloys.tile.TEHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUpgradable extends Slot {

	private int teuID;
	private int slotIndex;

	/** @param teuID The ID number for the type of TileEntityUpgradable that this slot is in (see {@link infinitealloys.tile.TEHelper TEHelper})
	 * @param index The number that refers to the slot in the array of slots that make up the container */
	public SlotUpgradable(IInventory inventory, int teuID, int index, int x, int y) {
		super(inventory, index, x, y);
		this.teuID = teuID;
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		// TEHelper contains a function with switches to return a boolean based on the type of TEU and the index of the slot
		return TEHelper.stackValidForSlot(teuID, slotIndex, itemstack);
	}
}
