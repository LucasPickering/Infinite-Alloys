package infinitealloys.item;

import net.minecraft.item.Item;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;

public final class IAItems {

  public static final Item machineComponent = new Item().setCreativeTab(InfiniteAlloys.tabIA);
  public static final Item upgradeComponent = new Item().setCreativeTab(InfiniteAlloys.tabIA);
  public static final Item ingot =
      new ItemIngot().setCreativeTab(InfiniteAlloys.tabIA).setHasSubtypes(true);
  public static final Item alloyIngot = new ItemAlloyIngot().setHasSubtypes(true);
  public static final Item internetWand =
      new ItemInternetWand().setCreativeTab(InfiniteAlloys.tabIA).setMaxStackSize(1);
  public static final ItemUpgrade[] upgrades = new ItemUpgrade[Consts.UPGRADE_TYPE_COUNT];
}
