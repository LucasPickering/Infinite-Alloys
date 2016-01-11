package infinitealloys.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import infinitealloys.util.EnumMachine;
import infinitealloys.util.Funcs;

public final class CreativeTabIA extends CreativeTabs {

  public CreativeTabIA(int id, String name) {
    super(id, name);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ItemStack getIconItemStack() {
    return EnumMachine.METAL_FORGE.getItemStack();
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