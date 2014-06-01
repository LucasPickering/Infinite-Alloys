package infinitealloys.inventory;

import infinitealloys.tile.TileEntityMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerMachine extends Container {

	public TileEntityMachine inventory;
	/** The index of the first slot in this container for the regular inventory */
	private int inventoryStart = 1;

	/** Used for machines that have their own Container class. {@link #initSlots} needs to be run later */
	public ContainerMachine(TileEntityMachine tileEntity, int inventoryStart) {
		inventory = tileEntity;
		this.inventoryStart = inventoryStart;
	}

	/** Used for machines without their own Container class. {@link #initSlots} doesn't need to be run later */
	public ContainerMachine(InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity, int invX, int invY, int upgX, int upgY) {
		inventory = tileEntity;
		initSlots(inventoryPlayer, invX, invY, upgX, upgY);
	}

	/** Given the coordinates for the player's inventory and the upgrade slot, add these slots to the container */
	protected void initSlots(InventoryPlayer inventoryPlayer, int invX, int invY, int upgX, int upgY) {
		addSlotToContainer(new SlotUpgrade(inventory, inventory.upgradeSlotIndex, upgX, upgY)); // Add the upgrade slot

		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventoryPlayer, x, invX + x * 18, invY + 58)); // Add the hotbar

		// Add the main inventory
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(inventoryPlayer, 9 + x + y * 9, invX + x * 18, invY + y * 18));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack itemstack = null;
		final Slot stackInSlot = (Slot)inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			final ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();

			// If an item was clicked in one of the container's slots
			if(slot < inventoryStart) {
				if(!mergeItemStack(stackInSlotCopy, inventoryStart, inventoryStart + 36, false))
					return null;
			}

			// If an item was clicked in the main inventory or the hotbar
			else {
				boolean moved = false;

				// If the item can go in one of the container's slots, put it there
				for(int i = 0; i < inventoryStart; i++) {
					if(inventory.isItemValidForSlot(i, stackInSlotCopy)) {
						moved = true;
						if(!mergeItemStack(stackInSlotCopy, i, i + 1, false) && !inventory.isItemValidForSlot(i + 1, stackInSlotCopy))
							break;
					}
				}

				if(!moved) {
					// Otherwise, if it's in the hotbar, move it to the main inventory
					if(slot < inventoryStart + 9) {
						if(!mergeItemStack(stackInSlotCopy, inventoryStart + 9, inventoryStart + 36, false))
							return null;
					}

					// Otherwise, if it's in the main inventory, move it to the hotbar
					else if(slot >= inventoryStart + 9) {
						if(!mergeItemStack(stackInSlotCopy, inventoryStart, inventoryStart + 9, false))
							return null;
					}
				}
			}

			if(stackInSlotCopy.stackSize == 0)
				stackInSlot.putStack(null);
			else
				stackInSlot.onSlotChanged();
			if(stackInSlotCopy.stackSize == itemstack.stackSize)
				return null;
			stackInSlot.onPickupFromSlot(player, stackInSlotCopy);
		}
		return itemstack;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if(!player.worldObj.isRemote)
			inventory.playersUsing.remove(player.getDisplayName());
	}

	@Override
	protected boolean mergeItemStack(ItemStack itemstack, int slotStart, int slotEnd, boolean backwards) {
		boolean var5 = false;
		int currentSlot = slotStart;
		if(backwards)
			currentSlot = slotEnd - 1;
		Slot slot;
		ItemStack stackInSlot;
		int maxStackSize;
		if(itemstack.isStackable()) {
			while(itemstack.stackSize > 0 && (!backwards && currentSlot < slotEnd || backwards && currentSlot >= slotStart)) {
				slot = (Slot)inventorySlots.get(currentSlot);
				maxStackSize = Math.min(itemstack.getMaxStackSize(), slot.getSlotStackLimit());
				stackInSlot = slot.getStack();
				if(stackInSlot != null && stackInSlot.isItemEqual(itemstack)
						&& ItemStack.areItemStackTagsEqual(itemstack, stackInSlot)) {
					final int var9 = stackInSlot.stackSize + itemstack.stackSize;
					if(var9 <= maxStackSize) {
						itemstack.stackSize = 0;
						stackInSlot.stackSize = var9;
						slot.onSlotChanged();
						var5 = true;
					}
					else if(stackInSlot.stackSize < maxStackSize) {
						itemstack.stackSize -= maxStackSize - stackInSlot.stackSize;
						stackInSlot.stackSize = maxStackSize;
						slot.onSlotChanged();
						var5 = true;
					}
				}
				if(backwards)
					currentSlot--;
				else
					currentSlot++;
			}
		}
		if(itemstack.stackSize > 0) {
			if(backwards)
				currentSlot = slotEnd - 1;
			else
				currentSlot = slotStart;
			while(!backwards && currentSlot < slotEnd || backwards && currentSlot >= slotStart) {
				slot = (Slot)inventorySlots.get(currentSlot);
				maxStackSize = Math.min(itemstack.getMaxStackSize(), slot.getSlotStackLimit());
				stackInSlot = slot.getStack();
				if(stackInSlot == null) {
					final ItemStack itemstack2 = itemstack.copy();
					itemstack2.stackSize = Math.min(itemstack2.stackSize, maxStackSize);
					slot.putStack(itemstack2);
					slot.onSlotChanged();
					itemstack.stackSize -= itemstack2.stackSize;
					var5 = true;
					break;
				}
				if(backwards)
					currentSlot--;
				else
					currentSlot++;
			}
		}
		return var5;
	}
}
