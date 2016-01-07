package infinitealloys.util;

import net.minecraft.item.ItemStack;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.IAItems;
import infinitealloys.item.ItemUpgrade;
import infinitealloys.item.ItemUpgradeAlloy;

public enum EnumUpgrade {

  SPEED("upgradeSpeed", 3, ItemUpgrade.class),
  EFFICIENCY("upgradeEfficiency", 3, ItemUpgrade.class),
  CAPACITY("upgradeCapacity", 3, ItemUpgrade.class),
  RANGE("upgradeRange", 3, ItemUpgrade.class),
  WIRELESS("upgradeWireless", 1, ItemUpgrade.class),
  ALLOY("upgradeAlloy", Consts.VALID_ALLOY_COUNT, ItemUpgradeAlloy.class);

  public final String name;

  /**
   * The maximum amount of tiers that this upgrade can have. Tiers values start at 1, so an
   * upgrade's tier value must ALWAYS be less than or equal to {@code tiers}.
   */
  public final int tiers;

  private final Class<? extends ItemUpgrade> itemClass;

  EnumUpgrade(String name, int tiers, Class<? extends ItemUpgrade> itemClass) {
    this.name = name;
    this.tiers = tiers;
    this.itemClass = itemClass;
  }

  public ItemUpgrade getItem() {
    try {
      ItemUpgrade item = itemClass.getConstructor(EnumUpgrade.class).newInstance(this);
      item.setCreativeTab(InfiniteAlloys.tabIA).setHasSubtypes(true).setUnlocalizedName(name);
      return item;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Get an {@link net.minecraft.item.ItemStack} representing an upgrade item of this type, with the
   * given tier value.
   *
   * @param tier the tier value, starting at 1
   */
  public ItemStack getItemStackForTier(int tier) {
    return new ItemStack(IAItems.upgrades[ordinal()], 1, tier - 1);
  }
}
