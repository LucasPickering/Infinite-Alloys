package infinitealloys.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;

public class ItemIA extends Item {

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + getIconString());
  }

  @Override
  public Item setUnlocalizedName(String unlocalizedName) {
    super.setUnlocalizedName(unlocalizedName);
    setTextureName(unlocalizedName);
    return this;
  }
}
