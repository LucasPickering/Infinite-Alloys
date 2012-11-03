package infinitealloys;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

public class CreativeTabIA extends CreativeTabs {

	public CreativeTabIA(int id, String name) {
		super(id, name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return new ItemStack(InfiniteAlloys.machine, 1, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return InfiniteAlloys.getStringLocalization("itemGroup." + getTabLabel());
	}
}
