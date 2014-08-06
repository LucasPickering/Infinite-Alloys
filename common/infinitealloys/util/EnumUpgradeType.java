package infinitealloys.util;

public enum EnumUpgradeType {

	SPEED("speed", 3, true), EFFICIENCY("efficiency", 3, true), CAPACITY("capacity", 3, true), RANGE("range", 3, true), WIRELESS("wireless", 1, false), ALLOY("alloy", Consts.VALID_ALLOY_COUNT, false);

	/** Get the ID of the upgrade type that corresponds with the given upgrade total tier, e.g. Speed I and II both return 0, Capacity I and II both return 2 */
	public static EnumUpgradeType getType(int totalTier) {
		int i = 0; // A variable to record how many upgrade tiers we've checked
		for(EnumUpgradeType upgradeType : values()) { // For each upgrade
			if(totalTier > i + upgradeType.tiers) { // If the item's damage is higher than the amount of tiers we've checked
				i += upgradeType.tiers; // Add the tiers to i
				continue;
			}
			return upgradeType; // We've found the type of upgrade that this item fits into
		}
		return null;
	}

	/** Get the ID of the upgrade tier that corresponds with the given upgrade total tier, e.g. Speed I returns 1 and Efficiency I returns 4 */
	public static int getTier(int totalTier) {
		int i = 0; // A variable to record how many upgrade tiers we've checked
		for(EnumUpgradeType upgradeType : values()) { // For each upgrade
			if(totalTier > i + upgradeType.tiers) { // If the item's damage is higher than the amount of tiers we've checked
				i += upgradeType.tiers; // Add the tiers to i
				continue;
			}
			return totalTier - i; // We've found the type of upgrade that this item fits into
		}
		return -1;
	}

	public static int getTotalTiers() {
		int i = 0; // A variable to record the tiers
		for(EnumUpgradeType upgradeType : values())
			i += upgradeType.tiers; // For each upgrade, add its tiers to the to total
		return i;
	}

	private final String name;
	private final int tiers;
	private final boolean requiresPrevious;

	/** @param name An unlocalized name for this upgrade
	 * @param tiers The amount of levels of upgrades in this type, e.g. Speed has 3 tiers, Speed I, Speed II, and Speed III
	 * @param requiresPrevious Whether or not the tiers of upgrade in this type require the previous tier in order to be applied */
	private EnumUpgradeType(String name, int tiers, boolean requiresPrevious) {
		this.name = name;
		this.tiers = tiers;
		this.requiresPrevious = requiresPrevious;
	}

	public String getName() {
		return name;
	}

	public int getTiers() {
		return tiers;
	}

	public boolean getRequiresPrevious() {
		return requiresPrevious;
	}

	/** Get the damage of an upgrade item that would correspond to this type/tier */
	public int getItemDamage(int tier) {
		int i = 0;
		// Count the total tiers in types before this
		for(int j = 0; j < ordinal(); j++)
			i += values()[j].tiers;
		return i + tier - 1; // Add the tier of this upgrade, then subtract one so that the first tier of the first type is DV 0
	}
}
