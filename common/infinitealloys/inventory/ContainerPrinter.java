package infinitealloys.inventory;

import infinitealloys.item.Items;
import infinitealloys.tile.TEEPrinter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPrinter extends ContainerMachine {

	public TEEPrinter inventory;

	public ContainerPrinter(InventoryPlayer inventoryPlayer, TEEPrinter tileEntity) {
		super( tileEntity, 4);
		inventory = tileEntity;

		addSlotToContainer(new SlotMachine(inventory, inventory.getID(), 0, 12, 44));
		addSlotToContainer(new SlotMachine(inventory, inventory.getID(), 1, 80, 44));
		addSlotToContainer(new SlotMachine(inventory, inventory.getID(), 2, 148, 44));
		
		initSlots(inventoryPlayer,8,66,148,6);
	}

	/*@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack itemstack = null;
		Slot stackInSlot = (Slot)inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();
			if(slot < 3) {
				if(!mergeItemStack(stackInSlotCopy, 4, 39, false))
					return null;
			}
			if(slot > 3) {
				if(stackInSlotCopy.itemID == Items.alloyIngot.itemID) {
					if(!mergeItemStack(stackInSlotCopy, 0, 1, false))
						return null;
				}
				else if(inventory.isUpgradeValid(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 3, 4, false))
						return null;
				}
				else if(slot > 3 && slot < 30) {
					if(!mergeItemStack(stackInSlotCopy, 30, 39, false))
						return null;
				}
				else if(slot >= 30)
					if(!mergeItemStack(stackInSlotCopy, 3, 30, false))
						return null;
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
