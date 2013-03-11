package infinitealloys.core;

import infinitealloys.block.Blocks;
import infinitealloys.util.FuncHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabIA extends CreativeTabs {

	public CreativeTabIA(int id, String name) {
		super(id, name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return new ItemStack(Blocks.machine, 1, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return FuncHelper.getLoc("itemGroup." + getTabLabel());
	}
}