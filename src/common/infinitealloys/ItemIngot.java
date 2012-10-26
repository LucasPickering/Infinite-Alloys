package infinitealloys;

import java.util.List;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

public class ItemIngot extends ItemIA {

	public ItemIngot(int id, int texture) {
		super(id, texture);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < IAValues.metalCount; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_82790_a(ItemStack itemstack, int renderPass) {
		if(itemstack.getItemDamage() < 8)
			return IAValues.metalColors[itemstack.getItemDamage()];
		return 0xffffff;
	}
}
