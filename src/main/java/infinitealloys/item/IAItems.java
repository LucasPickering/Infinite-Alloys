package infinitealloys.item;

import net.minecraft.item.Item;

import infinitealloys.util.Consts;

public class IAItems {

  public static final Item multi = new ItemMulti();
  public static final Item ingot = new ItemIngot();
  public static final Item alloyIngot = new ItemAlloyIngot();
  public static final Item internetWand = new ItemInternetWand();
  public static final Item teleporter = new ItemTeleporter();
  public static final ItemUpgrade[] upgrades = new ItemUpgrade[Consts.UPGRADE_TYPE_COUNT];
}
