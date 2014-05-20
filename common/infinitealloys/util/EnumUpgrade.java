package infinitealloys.util;

public enum EnumUpgrade {

	SPEED1(1, "speed1", true, false), SPEED2(2, "speed2", false, true),
	EFFICIENCY1(4, "efficiency1", true, false), EFFICIENCY2(8, "efficiency2", false, true),
	CAPACITY1(16, "capacity1", true, false), CAPACITY2(32, "capacity2", false, true),
	RANGE1(64, "range1", true, false), RANGE2(128, "range2", false, true),
	WIRELESS(256, "wireless", false, false);

	private final int id;
	private final String name;
	private final boolean prereq;
	private final boolean needsPrereq;

	private EnumUpgrade(int id, String name, boolean prereq, boolean needsPrereq) {
		this.id = id;
		this.name = name;
		this.prereq = prereq;
		this.needsPrereq = needsPrereq;
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isPrereq() {
		return prereq;
	}

	public boolean needsPrereq() {
		return needsPrereq;
	}

	/** Get the upgrade that this upgrade needs as a prerequisite. If this upgrade doesn't need one, return null. */
	public EnumUpgrade getPrereqUpgrade() {
		if(needsPrereq)
			return EnumUpgrade.values()[ordinal() - 1];
		return null;
	}
}
