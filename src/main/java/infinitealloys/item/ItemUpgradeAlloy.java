package infinitealloys.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.MachineHelper;

public final class ItemUpgradeAlloy extends ItemUpgrade {

  public ItemUpgradeAlloy(EnumUpgrade upgradeType) {
    super(upgradeType);
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
