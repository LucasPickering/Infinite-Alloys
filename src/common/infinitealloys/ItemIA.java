package infinitealloys;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class ItemIA extends Item {

	public ItemIA(int id, int texture) {
		super(id);
		setIconIndex(texture);
	}

	@Override
	public String getTextureFile() {
		return IAValues.ITEMS_PNG;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "iaItem" + itemstack.itemID + "@" + itemstack.getItemDamage();
	}
}
