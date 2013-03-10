package infinitealloys.item;

import infinitealloys.core.References;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemIA extends Item {

	public ItemIA(int id, int texture) {
		super(id);
		setIconIndex(texture);
	}

	@Override
	public String getTextureFile() {
		return References.TEXTURE_PATH + "sprites.png";
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "IAitem" + itemstack.itemID + "@" + itemstack.getItemDamage();
	}
}
