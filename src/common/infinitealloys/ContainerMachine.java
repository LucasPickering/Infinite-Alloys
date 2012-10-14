package infinitealloys;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerMachine extends Container {

	public TileEntityMachine inventory;

	public ContainerMachine(TileEntityMachine tileEntity) {
		inventory = tileEntity;
	}

	public ContainerMachine(InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
		inventory = tileEntity;
		addSlotToContainer(new SlotUpgrade(inventory, 0, 128, 8));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 8 + x * 18, 134 + y * 18));
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 192));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public ItemStack transferStackInSlot(int slot) {
		ItemStack itemstack = null;
		Slot stackInSlot = (Slot)this.inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();
			if(slot > 0 && slot <= 27) {
				if(!this.mergeItemStack(stackInSlotCopy, 28, 37, false))
					return null;
			}
			else if(slot > 27 && !this.mergeItemStack(stackInSlotCopy, 1, 27, false))
				return null;
			if(stackInSlotCopy.stackSize == 0)
				stackInSlot.putStack((ItemStack)null);
			else
				stackInSlot.onSlotChanged();
			if(stackInSlotCopy.stackSize == itemstack.stackSize)
				return null;
			stackInSlot.onPickupFromSlot(stackInSlotCopy);
		}
		return itemstack;
	}
}
