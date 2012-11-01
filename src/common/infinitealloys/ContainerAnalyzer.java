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
		addSlotToContainer(new SlotAnalyzer(inventory, 0, 56, 17));
		addSlotToContainer(new SlotAnalyzer(inventory, 1, 56, 53));
		addSlotToContainer(new SlotAnalyzer(inventory, 2, 116, 35));
		addSlotToContainer(new SlotUpgrade(inventory, 3, 152, 8));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 142));
	}

	@Override
	public ItemStack func_82846_b(EntityPlayer player, int slot) {
		super.func_82846_b(player, slot);
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
				else if(slot > 28 && slot <= 56) {
					if(!this.mergeItemStack(stackInSlotCopy, 57, 66, false))
						return null;
				}
				else if(slot > 56 && !mergeItemStack(stackInSlotCopy, 29, 55, false))
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
