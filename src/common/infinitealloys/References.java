package infinitealloys;

public class References {

	public static final String TEXTURE_PATH = "/infinitealloys/gfx/";

	/**
	 * Amount of metals usable in creating alloys
	 */
	public static final int metalCount = 8;

	/**
	 * Amount of machines added by the mod
	 */
	public static final int machineCount = 3;

	/**
	 * Amount of upgrades that can be made and used on machines
	 */
	public static final int upgradeCount = 9;

	/**
	 * Amount of "good" alloys, that can actually be used
	 */
	public static final int validAlloyCount = 6;

	/**
	 * Alloys that are actually usable
	 */
	public static int[] validAlloys = new int[validAlloyCount];

	/**
	 * Starting points for randomly genned alloy values
	 */
	public static final int[] validAlloyTargets = { 0 };

	/**
	 * The radix of the number system used for the alloys
	 */
	public static final int alloyRadix = 9;

	/**
	 * The colors used to colorize the ingots
	 */
	public static int[] metalColors = new int[metalCount];

	/**
	 * The names of each metal that can be used to make alloys
	 */
	public static final String[] metalNames = { "Copper", "Zinc", "Magnesium", "Tantalum", "Flamium", "Vegetanium", "Aquatilum", "Swagtanium" };

	/**
	 * The names of each upgrade that can be used on machines
	 */
	public static final String[] upgradeNames = { "Speed I", "Speed II", "Efficiency I", "Efficiency II", "Capacity I", "Capacity II", "Range I", "Range II", "Wireless Networking" };

	/**
	 * The amount of coords that the gps can hold
	 */
	public static final int gpsMaxCoords = 10;
}
