package infinitealloys.item;

import infinitealloys.util.Consts;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemIngot extends ItemIA {

	public ItemIngot(int id) {
		super(id, "ingot");
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		if(itemstack.getItemDamage() < Consts.METAL_COUNT)
			return Consts.metalColors[itemstack.getItemDamage()];
		return 0xffffff;
	}
}
