package infinitealloys.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.block.IABlocks;
import infinitealloys.util.Funcs;

public class CreativeTabIA extends CreativeTabs {

  public CreativeTabIA(int id, String name) {
    super(id, name);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ItemStack getIconItemStack() {
    return new ItemStack(IABlocks.machine, 1, 1);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getTranslatedTabLabel() {
    return Funcs.getLoc("itemGroup." + getTabLabel());
  }

  @Override
  public Item getTabIconItem() {
    return getIconItemStack().getItem();
  }
}