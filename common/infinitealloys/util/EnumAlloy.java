package infinitealloys.util;

import infinitealloys.core.InfiniteAlloys;

public enum EnumAlloy {
	// KEEP IN MIND: RIGHTMOST DIGITS ARE THE LESSER METALS WHILE LEFTMOST DIGITS ARE THE FANTASTICAL METALS

	ALLOY0("alloy0", 11, 55),
	ALLOY1("alloy1", 1111, 4477),
	ALLOY2("alloy2", 11111, 556688),
	ALLOY3("alloy3", 111111, 557799),
	ALLOY4("alloy4", 11110000, 55550000),
	ALLOY5("alloy5", 44444444, 99999999);

	public final String name;
	public final int min;
	public final int max;

	private EnumAlloy(String name, int min, int max) {
		this.name = name;
		this.min = min;
		this.max = max;
	}

	/** Get the alloy value of the alloy with the given ID */
	public static int getAlloy(int id) {
		return InfiniteAlloys.instance.worldData.getValidAlloys()[id];
	}

	/** Get the alloy value of this alloy */
	public int getAlloy() {
		return InfiniteAlloys.instance.worldData.getValidAlloys()[ordinal()];
	}

	/** Get the ID of this alloy. The ID is this alloy's index in the enum. */
	public short getID() {
		return (short)ordinal();
	}
}
