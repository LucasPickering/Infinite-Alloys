package infinitealloys.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMetal;

public final class ItemIngot extends ItemIA {

  public ItemIngot() {
    super();
    setCreativeTab(InfiniteAlloys.tabIA);
    setHasSubtypes(true);
  }

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
