package infinitealloys.inventory;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.Items;
import infinitealloys.util.References;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotAnalyzer extends Slot {

	private int slotIndex;

	public SlotAnalyzer(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return (slotIndex == 0 && itemstack.itemID == Items.alloyIngot.itemID) || (slotIndex == 2 && itemstack.itemID == Items.alloyBook.itemID);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
		int damage = stack.getItemDamage();
		if(slotIndex == 1 && damage > 0 && damage <= References.VALID_ALLOY_COUNT)
			player.addStat(InfiniteAlloys.instance.achievements[damage], 1);
	}
}
