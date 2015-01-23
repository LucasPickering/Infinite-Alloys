package infinitealloys.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;

public class ItemIA extends Item {

  public static Item multi;
  public static Item ingot;
  public static Item alloyIngot;
  public static Item internetWand;
  public static final ItemUpgrade[] upgrades = new ItemUpgrade[Consts.UPGRADE_TYPE_COUNT];

  private String textureName;

  public ItemIA() {
    super();
    setCreativeTab(InfiniteAlloys.tabIA);
  }

  public ItemIA(String textureName) {
    this();
    this.textureName = textureName;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + textureName);
  }
}
