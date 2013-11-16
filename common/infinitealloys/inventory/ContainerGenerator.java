package infinitealloys.inventory;

import infinitealloys.tile.TEEGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerGenerator extends ContainerMachine {

	public TEEGenerator inventory;

	public ContainerGenerator(InventoryPlayer inventoryPlayer, TEEGenerator tileEntity) {
		super(tileEntity);
		inventory = tileEntity;
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 3; x++)
				addSlotToContainer(new SlotMachine(inventory, inventory.getID(), x + y * 3, 13 + x * 18, 22 + y * 18));
		addSlotToContainer(new SlotUpgrade(inventory, 9, 185, 40));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 27 + x * 18, 94 + y * 18));
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventoryPlayer, x, 27 + x * 18, 152));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack itemstack = null;
		Slot stackInSlot = (Slot)inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();
			// The stack is coming from the main inventory
			if(slot > 9) {
				// If it's a fuel, put it in the fuel supply section
				if(TileEntityFurnace.getItemBurnTime(stackInSlotCopy) > 0) {
					if(!mergeItemStack(stackInSlotCopy, 0, 9, false))
						return null;
				}
				// If it's an upgrade, put it in the upgrade slot
				else if(inventory.isUpgradeValid(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 9, 10, false))
						return null;
				}
			}
			// If it's coming from the fuel supply section
			else {
				// Move it to the main inventory
				if(!mergeItemStack(stackInSlotCopy, 10, 46, false))
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
