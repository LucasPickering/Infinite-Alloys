package infinitealloys;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICrafting;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerMetalForge extends Container {

	public TileEntityMetalForge inventory;
	private int lastSmeltProgress;
	private int lastBurnTime;
	private int lastFuelBurnTime;

	public ContainerMetalForge(InventoryPlayer inventoryPlayer, TileEntityMetalForge tileEntity) {
		inventory = tileEntity;
		addSlotToContainer(new SlotMetalForge(inventory, 0, 16, 35));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 3; x++)
				addSlotToContainer(new SlotMetalForge(inventory, y * 3 + x + 1, x * 18 + 44, y * 18 + 16));
		addSlotToContainer(new SlotMetalForge(inventory, 10, 140, 34));
		for(int y = 0; y < 2; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new SlotMetalForge(inventory, y * 9 + x + 11, x * 18 + 8, y * 18 + 82));
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
	public void updateCraftingResults() {
		super.updateCraftingResults();
		for(int i = 0; i < crafters.size(); i++) {
			ICrafting crafting = (ICrafting)crafters.get(i);
			if(lastSmeltProgress != inventory.smeltProgress)
				crafting.updateCraftingInventoryInfo(this, 0, inventory.heatLeft);
			if(lastBurnTime != inventory.heatLeft)
				crafting.updateCraftingInventoryInfo(this, 1, inventory.heatLeft);
			if(lastFuelBurnTime != inventory.currentFuelBurnTime)
				crafting.updateCraftingInventoryInfo(this, 2, inventory.currentFuelBurnTime);
		}
		lastSmeltProgress = inventory.smeltProgress;
		lastBurnTime = inventory.heatLeft;
		lastFuelBurnTime = inventory.currentFuelBurnTime;
	}

	@Override
	public ItemStack transferStackInSlot(int slot) {
		ItemStack itemstack = null;
		Slot stackInSlot = (Slot)this.inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();
			if(slot == 2) {
				if(!mergeItemStack(stackInSlotCopy, 3, 39, true))
					return null;
				stackInSlot.onSlotChange(stackInSlotCopy, itemstack);
			}
			else if(slot != 1 && slot != 0) {
				if(stackInSlotCopy.itemID == InfiniteAlloys.ingot.shiftedIndex && stackInSlotCopy.getItemDamage() < 5) {
					if(!mergeItemStack(stackInSlotCopy, 0, 1, false))
						return null;
				}
				else if(slot >= 3 && slot < 30) {
					if(!mergeItemStack(stackInSlotCopy, 30, 39, false))
						return null;
				}
				else if(slot >= 30 && slot < 39 && !this.mergeItemStack(stackInSlotCopy, 3, 30, false))
					return null;
			}
			else if(!mergeItemStack(stackInSlotCopy, 3, 39, false))
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
