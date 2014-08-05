package infinitealloys.util;

public enum EnumUpgrade {

	SPEED1("speed1", true, false), SPEED2("speed2", false, true),
	EFFICIENCY1("efficiency1", true, false), EFFICIENCY2("efficiency2", false, true),
	CAPACITY1("capacity1", true, false), CAPACITY2("capacity2", false, true),
	RANGE1("range1", true, false), RANGE2("range2", false, true),
	WIRELESS("wireless", false, false);

	private final String name;

	/** Whether or not this upgrade has another upgrade after it in the sequence, e.g. this is true for Speed I but not for Speed II */
	private final boolean hasFollowing;

	/** Whether or not this upgrade has another upgrade before it in the sequence that it needs to be added, e.g. this is true for Speed II but not for Speed I */
	private final boolean hasPreceding;

	private EnumUpgrade(String name, boolean hasFollowing, boolean hasPreceding) {
		this.name = name;
		this.hasFollowing = hasFollowing;
		this.hasPreceding = hasPreceding;
	}

	public String getName() {
		return name;
	}

	public boolean hasFollowing() {
		return hasFollowing;
	}

	public boolean hasPreceding() {
		return hasPreceding;
	}

	/** Get the upgrade that precedes this one in the sequence (i.e. the prerequisite upgrade), e.g. for Speed I this will return Speed II
	 * 
	 * @return The following upgrade if it exists, otherwise null */
	public EnumUpgrade getPrecedingUpgrade() {
		if(hasPreceding)
			return EnumUpgrade.values()[ordinal() - 1];
		return null;
	}

	/** Get the upgrade that follows this one in the sequence, e.g. for Speed I this will return Speed II
	 * 
	 * @return The following upgrade if it exists, otherwise null */
	public EnumUpgrade getFollowingUpgrade() {
		if(hasFollowing)
			return EnumUpgrade.values()[ordinal() + 1];
		return null;
	}
}
