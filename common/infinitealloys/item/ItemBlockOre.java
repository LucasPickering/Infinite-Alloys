package infinitealloys.item;

import infinitealloys.util.Consts;
import infinitealloys.util.MachineHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockOre extends ItemBlock {

	public ItemBlockOre(int id) {
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if(itemstack.getItemDamage() < Consts.METAL_COUNT)
			return "tile.IA" + Consts.METAL_NAMES[itemstack.getItemDamage()] + "ore";
		return super.getUnlocalizedName(itemstack);
	}
}
