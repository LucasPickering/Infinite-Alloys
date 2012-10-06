package infinitealloys;

import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class ItemBlockIA extends ItemBlock {

	public ItemBlockIA(int i) {
		super(i);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "IA Block " + itemstack.itemID + "x" + itemstack.getItemDamage();
	}
}
