package infinitealloys.inventory;

import infinitealloys.util.EnumMachine;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotMachine extends Slot {

	private final EnumMachine machine;
	private final int slotIndex;

	/** @param machine The type of machine that the slot is in
	 * @param index The number that refers to the slot in the array of slots that make up the container
	 * @see {@link infinitealloys.util.MachineHelper MachineHelper} for the ID constants */
	public SlotMachine(IInventory inventory, EnumMachine machine, int index, int x, int y) {
		super(inventory, index, x, y);
		this.machine = machine;
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		// MachineHelper contains a function with switches to return a boolean based on the type of machine and the index of the slot
		return machine.stackValidForSlot(slotIndex, itemstack);
	}
}
