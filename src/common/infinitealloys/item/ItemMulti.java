package infinitealloys.item;

import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import java.util.List;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

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
		for(int i = 0; i < References.multiItemCount; i++)
			list.add(new ItemStack(id, 1, i));
	}
}
