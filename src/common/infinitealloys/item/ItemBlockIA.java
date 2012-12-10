package infinitealloys.item;

import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

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
	public String getItemNameIS(ItemStack itemstack) {
		return "IAblock" + itemstack.itemID + "@" + itemstack.getItemDamage();
	}
}
