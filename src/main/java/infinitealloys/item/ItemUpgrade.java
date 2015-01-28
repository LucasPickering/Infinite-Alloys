package infinitealloys.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumMetal;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;

public class ItemUpgrade extends ItemIA {

  protected IIcon background;
  private final IIcon[] upgradeIcons;

  public final EnumUpgrade upgradeType;

  public ItemUpgrade(EnumUpgrade upgradeType) {
    super();
    this.upgradeType = upgradeType;
    setUnlocalizedName(upgradeType.name);
    upgradeIcons = new IIcon[upgradeType.tiers];
    setHasSubtypes(true);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister iconRegister) {
    background = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradeBackground");
    for (int i = 0; i < upgradeType.tiers; i++) {
      upgradeIcons[i] =
          iconRegister.registerIcon(Consts.TEXTURE_PREFIX + upgradeType.name + (i + 1));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIconFromDamageForRenderPass(int damage, int renderPass) {
    if (renderPass == 1) {
      return upgradeIcons[damage];
    }
    return background;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean requiresMultipleRenderPasses() {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
    for (int i = 0; i < upgradeType.tiers; i++) {
      list.add(new ItemStack(item, 1, i));
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack itemstack) {
    if (itemstack.getItemDamage() < upgradeType.tiers) {
      return "item." + upgradeType.name + (itemstack.getItemDamage() + 1);
    }
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
    if (upgradeType == EnumUpgrade.ALLOY && renderPass == 1) {
      int colorCount = 0;
      int redTot = 0, greenTot = 0, blueTot = 0;
      int alloy = EnumAlloy.getAlloyForID(itemstack.getItemDamage());

      for (int i = 0; i < Consts.METAL_COUNT; i++) {
        int ingotColor = EnumMetal.values()[i].color;
        int alloyAmt = Funcs.intAtPos(alloy, Consts.ALLOY_RADIX, i);
        colorCount += alloyAmt;
        redTot +=
            (ingotColor >> 16 & 255) * alloyAmt; // Get the red byte from the ingot's hex color code
        greenTot +=
            (ingotColor >> 8 & 255)
            * alloyAmt; // Get the green byte from the ingot's hex color code
        blueTot +=
            (ingotColor & 255) * alloyAmt; // Get the blue byte from the ingot's hex color code
      }

      int redAvg = 0, greenAvg = 0, blueAvg = 0;
      if (colorCount != 0) {
        redAvg = redTot / colorCount;
        greenAvg = greenTot / colorCount;
        blueAvg = blueTot / colorCount;
      }
      return (redAvg << 16) + (greenAvg << 8) + blueAvg;
    }
    return 0xffffff;
  }
}
