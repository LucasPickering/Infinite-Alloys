package infinitealloys.inventory;

import infinitealloys.tile.TEEXray;
import infinitealloys.util.MachineHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerXray extends ContainerMachine {

	public TEEXray inventory;

	public ContainerXray(InventoryPlayer inventoryPlayer, TEEXray tileEntity) {
		super( tileEntity, 2);
		inventory = tileEntity;

		addSlotToContainer(new SlotMachine(inventory, inventory.getID(), 0, 32, 6));
		
		initSlots(inventoryPlayer, 18, 156, 168, 6);
	}

	/*@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack itemstack = null;
		Slot stackInSlot = (Slot)inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();
			if(slot < 2) {
				if(!mergeItemStack(stackInSlotCopy, 2, 37, false))
					return null;
			}
			else {
				if(MachineHelper.isDetectable(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 0, 1, false))
						return null;
				}
				else if(inventory.isUpgradeValid(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 1, 2, false))
						return null;
				}
			}
			if(stackInSlotCopy.stackSize == 0)
				stackInSlot.putStack((ItemStack)null);
			else
				stackInSlot.onSlotChanged();
			if(stackInSlotCopy.stackSize == itemstack.stackSize)
				return null;
			stackInSlot.onPickupFromSlot(player, stackInSlotCopy);
		}
		return itemstack;
	}*/
}
