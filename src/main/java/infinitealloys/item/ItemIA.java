package infinitealloys.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import infinitealloys.util.Consts;

public abstract class ItemIA extends Item {

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
