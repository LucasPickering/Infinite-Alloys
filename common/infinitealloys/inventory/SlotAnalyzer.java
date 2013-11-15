package infinitealloys.inventory;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotAnalyzer extends SlotMachine {

	private int slotIndex;

	public SlotAnalyzer(IInventory inventory, int index, int x, int y, int temID) {
		super(inventory, temID, index, x, y);
		slotIndex = index;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
		int damage = stack.getItemDamage();
		if(slotIndex == 1 && damage > 0 && damage <= Consts.VALID_ALLOY_COUNT)
			player.addStat(InfiniteAlloys.achievements[damage], 1);
	}
}
