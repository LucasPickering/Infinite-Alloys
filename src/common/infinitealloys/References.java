package infinitealloys;

public class References {

	public static String TEXTURE_PATH = "/infinitealloys/gfx/";

	/**
	 * Amount of metals usable in creating alloys
	 */
	public static int metalCount = 8;

	/**
	 * Amount of machines added by the mod
	 */
	public static int machineCount = 3;

	/**
	 * Amount of upgrades that can be made and used on machines
	 */
	public static int upgradeCount = 9;

	/**
	 * Alloys that are actually usable
	 */
	public static int[] validAlloyIngots = { 0, 1, 2, 3, 4, 5 };

	/**
	 * The radix of the number system used for the alloys
	 */
	public static int alloyRadix = 9;

	/**
	 * The colors used to colorize the ingots
	 */
	public static int[] metalColors = new int[metalCount];

	/**
	 * The names of each metal that can be used to make alloys
	 */
	public static String[] metalNames = { "Copper", "Zinc", "Magnesium", "Tantalum", "Flamium", "Vegetanium", "Aquatilum", "Swagtanium" };

	/**
	 * The names of each upgrade that can be used on machines
	 */
	public static String[] upgradeNames = { "Speed I", "Speed II", "Efficiency I", "Efficiency II", "Capacity I", "Capacity II", "Range I", "Range II", "Wireless Networking" };
}
