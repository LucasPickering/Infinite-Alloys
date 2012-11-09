package infinitealloys;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerXray extends ContainerMachine {

	public TileEntityXray inventory;

	public ContainerXray(InventoryPlayer inventoryPlayer, TileEntityXray tileEntity) {
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
	public ItemStack func_82846_b(EntityPlayer player, int slot) {
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
				if(inventory.isDetectable(stackInSlotCopy)) {
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
			stackInSlot.func_82870_a(player, stackInSlotCopy);
		}
		return itemstack;
	}
}
