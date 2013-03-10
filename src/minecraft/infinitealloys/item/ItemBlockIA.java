package infinitealloys.item;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockIA extends ItemBlock {

	public ItemBlockIA(int id) {
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return "IAblock" + itemstack.itemID + "@" + itemstack.getItemDamage();
	}
}
