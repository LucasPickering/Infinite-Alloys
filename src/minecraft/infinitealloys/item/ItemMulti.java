package infinitealloys.item;

import infinitealloys.core.References;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMulti extends ItemIA {

	public ItemMulti(int id, int texture) {
		super(id, texture);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconFromDamage(int damage) {
		return iconIndex + damage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < References.MULTI_ITEM_COUNT; i++)
			list.add(new ItemStack(id, 1, i));
	}
}
