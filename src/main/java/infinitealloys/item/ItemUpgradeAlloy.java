package infinitealloys.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.MachineHelper;

public final class ItemUpgradeAlloy extends ItemUpgrade {

  private IIcon ingotIcon;

  public ItemUpgradeAlloy(EnumUpgrade upgradeType) {
    super(upgradeType);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradebackground");
    ingotIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradeingot");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIconFromDamageForRenderPass(int damage, int renderPass) {
    if (renderPass == 1) {
      return ingotIcon;
    }
    return itemIcon;
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
