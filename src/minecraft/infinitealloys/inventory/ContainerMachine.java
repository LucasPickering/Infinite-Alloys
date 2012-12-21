package infinitealloys.inventory;

import infinitealloys.Point;
import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TileEntityComputer;
import infinitealloys.tile.TileEntityMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;

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

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
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
			stackInSlot.onPickupFromSlot(player, stackInSlotCopy);
		}
		return itemstack;
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer player) {
		super.onCraftGuiClosed(player);
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			inventory.playersUsing.remove(player.username);
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
				if(stackInSlot != null && stackInSlot.itemID == itemstack.itemID && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == stackInSlot.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack, stackInSlot)) {
					int var9 = stackInSlot.stackSize + itemstack.stackSize;
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
					ItemStack itemstack2 = itemstack.copy();
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
