package infinitealloys.inventory;

import infinitealloys.util.MachineHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotMachine extends Slot {

	private final int temID;
	private final int slotIndex;

	/** @param temID The ID number for the type of machine that this slot is in
	 * @param index The number that refers to the slot in the array of slots that make up the container
	 * @see {@link infinitealloys.util.MachineHelper MachineHelper} for the ID constants */
	public SlotMachine(IInventory inventory, int temID, int index, int x, int y) {
		super(inventory, index, x, y);
		this.temID = temID;
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		// MachineHelper contains a function with switches to return a boolean based on the type of machine and the index of the slot
		return MachineHelper.stackValidForSlot(temID, slotIndex, itemstack);
	}
}
