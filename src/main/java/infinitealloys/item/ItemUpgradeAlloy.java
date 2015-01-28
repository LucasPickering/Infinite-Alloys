package infinitealloys.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.MachineHelper;

public class ItemUpgradeAlloy extends ItemUpgrade {

  private IIcon ingotIcon;

  public ItemUpgradeAlloy(EnumUpgrade upgradeType) {
    super(upgradeType);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister iconRegister) {
    backgroundIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradebackground");
    ingotIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradeingot");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIconFromDamageForRenderPass(int damage, int renderPass) {
    if (renderPass == 1) {
      return ingotIcon;
    }
    return backgroundIcon;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
    if (renderPass == 1) {
      return MachineHelper.getColorForAlloy(EnumAlloy.getAlloyForID(itemstack.getItemDamage()));
    }
    return 0xffffff;
  }
}
