package infinitealloys;

import java.util.List;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

public class ItemIngot extends ItemIA {

	public ItemIngot(int id, int texture) {
		super(id, texture);
		setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < IAValues.oreCount; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromDamage(int damage, int renderPass) {
		if(damage < 8)
			return IAValues.ingotColors[damage + 1];
		return 0xffffff;
	}
}
