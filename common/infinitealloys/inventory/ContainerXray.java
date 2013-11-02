package infinitealloys.inventory;

import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TEMXray;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerXray extends ContainerUpgradable {

	public TEMXray inventory;

	public ContainerXray(InventoryPlayer inventoryPlayer, TEMXray tileEntity) {
		super(tileEntity);
		inventory = tileEntity;
		addSlotToContainer(new SlotXray(inventory, 0, 32, 6));
		addSlotToContainer(new SlotUpgrade(inventory, 1, 168, 6));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 18 + x * 18, 156 + y * 18));
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventoryPlayer, x, 18 + x * 18, 214));
	}

	@Override
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
				if(TEHelper.isDetectable(stackInSlotCopy)) {
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
	}
}
