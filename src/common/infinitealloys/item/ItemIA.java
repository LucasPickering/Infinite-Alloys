package infinitealloys.item;

import infinitealloys.References;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

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
