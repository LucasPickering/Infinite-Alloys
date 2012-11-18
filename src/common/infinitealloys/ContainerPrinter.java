package infinitealloys;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerPrinter extends ContainerMachine {

	public TileEntityPrinter inventory;

	public ContainerPrinter(InventoryPlayer inventoryPlayer, TileEntityPrinter tileEntity) {
		super(tileEntity);
		inventory = tileEntity;
		addSlotToContainer(new SlotPrinter(inventory, 0, 12, 26));
		addSlotToContainer(new SlotPrinter(inventory, 1, 80, 26));
		addSlotToContainer(new SlotPrinter(inventory, 2, 148, 26));
		addSlotToContainer(new SlotUpgrade(inventory, 3, 148, 6));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 8 + x * 18, 66 + y * 18));
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 124));
	}

	@Override
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
				if(stackInSlotCopy.itemID == InfiniteAlloys.alloyIngot.shiftedIndex) {
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
	}
}
