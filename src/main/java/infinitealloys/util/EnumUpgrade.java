package infinitealloys.util;

import net.minecraft.item.ItemStack;

import infinitealloys.item.IAItems;

public enum EnumUpgrade {

  SPEED("upgradeSpeed", 3), EFFICIENCY("upgradeEfficiency", 3),
  CAPACITY("upgradeCapacity", 3), RANGE("upgradeRange", 3),
  WIRELESS("upgradeWireless", 1), ALLOY("upgradeAlloy", 6);

  public final String name;

  /**
   * The maximum amount of tiers that this upgrade can have. Tiers values start at 1, so an
   * upgrade's tier value must ALWAYS be less than or equal to {@code tiers}.
   */
  public final int tiers;

  private EnumUpgrade(String name, int tiers) {
    this.name = name;
    this.tiers = tiers;
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
