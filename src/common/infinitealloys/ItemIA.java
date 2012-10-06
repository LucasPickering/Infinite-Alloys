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
		return "/infinitealloys/gfx/items.png";
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "IA Item " + itemstack.itemID;
	}
}
