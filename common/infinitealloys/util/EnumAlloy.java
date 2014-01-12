package infinitealloys.util;

public enum EnumAlloy {
	// KEEP IN MIND: RIGHTMOST DIGITS ARE THE LESSER METALS WHILE LEFTMOST DIGITS ARE THE FANTASTICAL METALS

	ALLOY0("alloy0", 11, 55),
	ALLOY1("alloy1", 1111, 4477),
	ALLOY2("alloy2", 11111, 556688),
	ALLOY3("alloy3", 111111, 557799),
	ALLOY4("alloy4", 11110000, 55550000),
	ALLOY5("alloy5", 44444444, 99999999);

	public final int id;
	public final String name;
	public final int min;
	public final int max;

	private EnumAlloy(String name, int min, int max) {
		this.id = 1 << ordinal(); // The ID of this alloy is 2 raised to its position in the anum, e.g. alloy 0 is 1 and alloy 4 is 16
		this.name = name;
		this.min = min;
		this.max = max;
	}
}
