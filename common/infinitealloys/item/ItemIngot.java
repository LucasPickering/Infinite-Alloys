package infinitealloys.item;

import infinitealloys.util.Consts;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemIngot extends ItemIA {

	public ItemIngot() {
		super("ingot");
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		if(itemstack.getItemDamage() < Consts.METAL_COUNT)
			return Consts.metalColors[itemstack.getItemDamage()];
		return 0xffffff;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if(itemstack.getItemDamage() < Consts.METAL_COUNT)
			return "item.IA" + Consts.METAL_NAMES[itemstack.getItemDamage()] + "ingot";
		return super.getUnlocalizedName(itemstack);
	}
}
