package infinitealloys.item;

import net.minecraft.item.Item;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;

public final class IAItems {

  public static final Item machineComponent = new Item().setCreativeTab(InfiniteAlloys.creativeTab);
  public static final Item upgradeComponent = new Item().setCreativeTab(InfiniteAlloys.creativeTab);
  public static final Item ingot =
      new ItemIngot().setCreativeTab(InfiniteAlloys.creativeTab).setHasSubtypes(true).setMaxDamage(0);
  public static final Item alloyIngot = new ItemAlloyIngot().setHasSubtypes(true);
  public static final Item internetWand =
      new ItemInternetWand().setCreativeTab(InfiniteAlloys.creativeTab).setMaxStackSize(1);
  public static final ItemUpgrade[] upgrades = new ItemUpgrade[Consts.UPGRADE_TYPE_COUNT];
}
