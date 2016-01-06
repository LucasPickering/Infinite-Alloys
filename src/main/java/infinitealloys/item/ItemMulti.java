package infinitealloys.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;

public final class ItemMulti extends ItemIA {

  private final IIcon[] icons = new IIcon[Consts.MULTI_ITEM_NAMES.length];

  public ItemMulti() {
    super();
    setCreativeTab(InfiniteAlloys.tabIA);
    setHasSubtypes(true);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister iconRegister) {
    for (int i = 0; i < Consts.MULTI_ITEM_NAMES.length; i++) {
      icons[i] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + Consts.MULTI_ITEM_NAMES[i]);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIconFromDamage(int damage) {
    return icons[damage];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
    for (int i = 0; i < Consts.MULTI_ITEM_NAMES.length; i++) {
      list.add(new ItemStack(item, 1, i));
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack itemstack) {
    if (itemstack.getItemDamage() < Consts.MULTI_ITEM_NAMES.length) {
      return "item." + Consts.MULTI_ITEM_NAMES[itemstack.getItemDamage()];
    }
    return super.getUnlocalizedName(itemstack);
  }
}
