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
		addSlotToContainer(new SlotUpgrade(inventory, 0, 140, 43));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	/**
	 * transferStackInSlot
	 */
	@Override
	public ItemStack func_82846_b(EntityPlayer player, int slot) {
		ItemStack itemstack = null;
		Slot stackInSlot = (Slot)inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();
			if(slot > 0) {
				if(inventory.isUpgradeValid(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 0, 1, false))
						return null;
				}
				else if(slot <= 27) {
					if(!mergeItemStack(stackInSlotCopy, 28, 37, false))
						return null;
				}
			}
			else if(slot > 27 && !mergeItemStack(stackInSlotCopy, 1, 27, false))
				return null;
			if(stackInSlotCopy.stackSize == 0)
				stackInSlot.putStack((ItemStack)null);
			else
				stackInSlot.onSlotChanged();
			if(stackInSlotCopy.stackSize == itemstack.stackSize)
				return null;
			stackInSlot.func_82870_a(player, stackInSlotCopy);
		}
		return itemstack;
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer player) {
		super.onCraftGuiClosed(player);
		TileEntityMachine.controllers.remove(player.username);
	}
}
