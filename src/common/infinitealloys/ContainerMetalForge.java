package infinitealloys;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.TileEntityFurnace;

public class ContainerMetalForge extends ContainerMachine {

	public TileEntityMetalForge inventory;

	public ContainerMetalForge(InventoryPlayer inventoryPlayer, TileEntityMetalForge tileEntity) {
		super(tileEntity);
		inventory = tileEntity;
		addSlotToContainer(new SlotMetalForge(inventory, 0, 8, 35));
		for(int y = 0; y < 2; y++)
			for(int x = 0; x < 4; x++)
				addSlotToContainer(new SlotMetalForge(inventory, y * 2 + x + 1, x * 18 + 34, y * 18 + 26));
		addSlotToContainer(new SlotUpgrade(inventory, 9, 148, 8));
		addSlotToContainer(new SlotMetalForge(inventory, 10, 148, 34));
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
	public ItemStack func_82846_b(EntityPlayer player, int slot) {
		ItemStack itemstack = null;
		Slot stackInSlot = (Slot)this.inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();
			if(slot >= 10 && slot <= 28 || slot == 0) {
				if(!mergeItemStack(stackInSlotCopy, 29, 65, false))
					return null;
				stackInSlot.onSlotChange(stackInSlotCopy, itemstack);
			}
			else if(slot > 28) {
				if(TileEntityFurnace.isItemFuel(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 0, 1, false))
						return null;
				}
				else if(inventory.isUpgradeValid(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 9, 10, false))
						return null;
				}
				else if(inventory.getIngotNum(stackInSlotCopy) != -1) {
					if(!mergeItemStack(stackInSlotCopy, 11, 28, false))
						return null;
				}
				else if(slot > 28 && slot < 56) {
					if(!mergeItemStack(stackInSlotCopy, 56, 65, false))
						return null;
				}
				else if(slot >= 56) {
					if(!mergeItemStack(stackInSlotCopy, 29, 56, false))
						return null;
				}
			}
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
	public ItemStack slotClick(int slot, int mouseButton, int i, EntityPlayer player) {
		if((mouseButton == 0 || mouseButton == 1) && slot >= 1 && slot <= 8) {
			inventory.smeltProgress = 0;
			if(mouseButton == 0)
				inventory.recipeAmts[slot - 1] = (byte)Math.min(inventory.recipeAmts[slot - 1] + 1, References.alloyRadix - 1);
			else if(mouseButton == 1)
				inventory.recipeAmts[slot - 1] = (byte)Math.max(inventory.recipeAmts[slot - 1] - 1, 0);
			return null;
		}
		return super.slotClick(slot, mouseButton, i, player);
	}
}
