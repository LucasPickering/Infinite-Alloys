package infinitealloys.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumMetal;

public final class ItemIngot extends Item {

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    if (stack.getItemDamage() < Consts.METAL_COUNT) {
      return "item." + EnumMetal.values()[stack.getItemDamage()].name + "Ingot";
    }
    return super.getUnlocalizedName(stack);
  }

  @Override
  @SideOnly(Side.CLIENT)
  @SuppressWarnings("unchecked")
  public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      list.add(new ItemStack(item, 1, i));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack stack, int renderPass) {
    if (stack.getItemDamage() == 1) {
      return 0xcbcec7;
    }
    if (stack.getItemDamage() < Consts.METAL_COUNT) {
      return EnumMetal.values()[stack.getItemDamage()].color;
    }
    return 0xffffff;
  }
}
