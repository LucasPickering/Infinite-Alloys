package infinitealloys.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMetal;

public final class ItemIngot extends ItemIA {

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      list.add(new ItemStack(item, 1, i));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
    if (itemstack.getItemDamage() == 1) {
      return 0xcbcec7;
    }
    if (itemstack.getItemDamage() < Consts.METAL_COUNT) {
      return EnumMetal.values()[itemstack.getItemDamage()].color;
    }
    return 0xffffff;
  }

  @Override
  public String getUnlocalizedName(ItemStack itemstack) {
    if (itemstack.getItemDamage() < Consts.METAL_COUNT) {
      return "item." + EnumMetal.values()[itemstack.getItemDamage()].name + "Ingot";
    }
    return super.getUnlocalizedName(itemstack);
  }
}
