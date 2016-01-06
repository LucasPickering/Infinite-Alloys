package infinitealloys.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumUpgrade;

public class ItemUpgrade extends Item {

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
    itemIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradebackground");
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
    return itemIcon;
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
}
