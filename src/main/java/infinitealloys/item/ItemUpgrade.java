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

public abstract class ItemUpgrade extends ItemIA {

  protected IIcon background;
  private final IIcon[] upgradeIcons;

  public final String name;
  public final int upgradeType;
  public final int tiers;

  public ItemUpgrade(String name, int upgradeType, int tiers) {
    super();
    this.name = name;
    this.upgradeType = upgradeType;
    this.tiers = tiers;
    upgradeIcons = new IIcon[tiers];
    setHasSubtypes(true);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister iconRegister) {
    background = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradebackground");
    for (int i = 0; i < tiers; i++) {
      upgradeIcons[i] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + name + (i + 1));
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
    for (int i = 0; i < tiers; i++) {
      list.add(new ItemStack(item, 1, i));
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack itemstack) {
    if (itemstack.getItemDamage() < tiers) {
      return "item." + name + (itemstack.getItemDamage() + 1);
    }
    return super.getUnlocalizedName(itemstack);
  }
}
