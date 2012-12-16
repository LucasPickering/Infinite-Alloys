package infinitealloys.inventory;

import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TileEntityMetalForge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMetalForge extends ContainerMachine {

	public TileEntityMetalForge inventory;

	public ContainerMetalForge(InventoryPlayer inventoryPlayer, TileEntityMetalForge tileEntity) {
		super(tileEntity);
		inventory = tileEntity;
		addSlotToContainer(new SlotMetalForge(inventory, 0, 8, 52));
		addSlotToContainer(new SlotUpgrade(inventory, 1, 148, 8));
		addSlotToContainer(new SlotMetalForge(inventory, 2, 148, 52));
		for(int y = 0; y < 2; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new SlotMetalForge(inventory, y * 9 + x + 3, x * 18 + 8, y * 18 + 82));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 8 + x * 18, 134 + y * 18));
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 192));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack itemstack = null;
		Slot stackInSlot = (Slot)this.inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();
			if(slot >= 2 && slot <= 20 || slot == 0) {
				if(!mergeItemStack(stackInSlotCopy, 21, 57, false))
					return null;
				stackInSlot.onSlotChange(stackInSlotCopy, itemstack);
			}
			else if(slot > 20) {
				if(TEHelper.isBook(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 0, 1, false))
						return null;
				}
				else if(inventory.isUpgradeValid(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 1, 2, false))
						return null;
				}
				else if(inventory.getIngotNum(stackInSlotCopy) != -1) {
					if(!mergeItemStack(stackInSlotCopy, 3, 20, false))
						return null;
				}
				else if(slot > 20 && slot < 48) {
					if(!mergeItemStack(stackInSlotCopy, 48, 57, false))
						return null;
				}
				else if(slot >= 48) {
					if(!mergeItemStack(stackInSlotCopy, 21, 48, false))
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
	}
}
