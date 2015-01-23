package infinitealloys.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.MachineHelper;

public class ItemUpgradeAlloy extends ItemUpgrade {

  private IIcon upgradeIcon;

  public ItemUpgradeAlloy() {
    super("alloyUpgrade", Consts.ALLOY_UPG, Consts.VALID_ALLOY_COUNT);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister iconRegister) {
    background = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradebackground");
    upgradeIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradeingot");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIconFromDamageForRenderPass(int damage, int renderPass) {
    if (renderPass == 1) {
      return upgradeIcon;
    }
    return background;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
    if (renderPass == 1) {
      return MachineHelper.getAlloyColor(EnumAlloy.getAlloyForID(itemstack.getItemDamage()));
    }
    return 0xffffff;
  }
}
