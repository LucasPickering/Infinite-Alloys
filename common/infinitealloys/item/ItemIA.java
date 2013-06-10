package infinitealloys.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemIA extends Item {

	public ItemIA(int id) {
		super(id);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return "IAitem" + itemstack.itemID + "@" + itemstack.getItemDamage();
	}
}
