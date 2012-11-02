package infinitealloys;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.TileEntityFurnace;

public class ContainerAnalyzer extends ContainerMachine {

	public TileEntityAnalyzer inventory;

	public ContainerAnalyzer(InventoryPlayer inventoryPlayer, TileEntityAnalyzer tileEntity) {
		super(tileEntity);
		inventory = tileEntity;
		addSlotToContainer(new SlotAnalyzer(inventory, 0, 12, 35));
		addSlotToContainer(new SlotAnalyzer(inventory, 1, 148, 35));
		addSlotToContainer(new SlotUpgrade(inventory, 2, 152, 8));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 142));
	}

	@Override
	public ItemStack func_82846_b(EntityPlayer player, int slot) {
		ItemStack itemstack = null;
		Slot stackInSlot = (Slot)this.inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();
			if(slot<3) {
				if(!mergeItemStack(stackInSlotCopy, 3, 39, false))
					return null;
			}
			if(slot > 3) {
				if(stackInSlotCopy.itemID == InfiniteAlloys.alloyIngot.shiftedIndex) {
					if(!mergeItemStack(stackInSlotCopy, 0, 1, false))
						return null;
				}
				else if(stackInSlotCopy.itemID == InfiniteAlloys.upgrade.shiftedIndex && inventory.isUpgradeValid(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 1, 2, false))
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
			stackInSlot.func_82870_a(player, stackInSlotCopy);
		}
		return itemstack;
	}
}
