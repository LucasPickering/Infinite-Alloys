package infinitealloys.item;

import infinitealloys.References;
import java.util.List;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class ItemIngot extends ItemIA {

	public ItemIngot(int id, int texture) {
		super(id, texture);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < References.metalCount; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		if(itemstack.getItemDamage() < 8)
			return References.metalColors[itemstack.getItemDamage()];
		return 0xffffff;
	}
}
