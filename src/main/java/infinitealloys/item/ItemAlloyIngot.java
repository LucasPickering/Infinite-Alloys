package infinitealloys.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumMetal;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;

public final class ItemAlloyIngot extends Item {

  @Override
  public boolean getShareTag() {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  @SuppressWarnings("unchecked")
  public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
    // The each metal's content in the alloy. Each metal gets a number from 0 to Consts.ALLOY_RADIX to represent its content.
    int[] metalContent = new int[Consts.METAL_COUNT];
    // The total amount of metal "pieces" in this
    int totalMass = 0;
    int alloy;
    int alloyID = itemstack.getItemDamage() - 1;
    if (itemstack.hasTagCompound()) {
      alloy = itemstack.getTagCompound().getInteger("alloy");
    } else if (alloyID >= 0 && alloyID < Consts.VALID_ALLOY_COUNT) {
      alloy = EnumAlloy.getAlloyForID(alloyID);
    } else {
      return;
    }
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      totalMass += metalContent[i] = Funcs.intAtPos(alloy, Consts.ALLOY_RADIX, i);
    }
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      float percentage = Math.round((float) metalContent[i] / (float) totalMass * 1000F) / 10F;
      if (percentage != 0) {
        list.add(Funcs.formatLoc("%d%% %s", percentage,
                                 "%metal." + EnumMetal.values()[i].name + ".name"));
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
    return MachineHelper.getColorForAlloy(
        itemstack.hasTagCompound() ? itemstack.getTagCompound().getInteger("alloy")
                                   : EnumAlloy.getAlloyForID((itemstack.getItemDamage() - 1)));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
  }

  @Override
  public String getUnlocalizedName(ItemStack itemstack) {
    if (itemstack.getItemDamage() > 0 && itemstack.getItemDamage() <= Consts.VALID_ALLOY_COUNT) {
      return "item." + EnumAlloy.values()[itemstack.getItemDamage() - 1].name;
    }
    return super.getUnlocalizedName(itemstack);
  }
}
