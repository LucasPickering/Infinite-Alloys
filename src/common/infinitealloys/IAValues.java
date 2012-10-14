package infinitealloys;

public class IAValues {

	public static String TEXTURE_PATH = "/infinitealloys/gfx/";
	public static String BLOCKS_PNG = TEXTURE_PATH + "blocks.png";
	public static String ITEMS_PNG = TEXTURE_PATH + "items.png";
	/**
	 * Amount of ores added by the mod
	 */
	public static int oreCount = 8;

	/**
	 * Amount of metals usable in creating alloys
	 */
	public static int metalCount = 9;

	/**
	 * Amount of machines added by the mod
	 */
	public static int machineCount = 3;

	/**
	 * Amount of upgrades that can be made and used on machines
	 */
	public static int upgradeCount = 8;

	/**
	 * Alloys that are actually usable
	 */
	public static int[] validAlloyIngots = { 0, 1, 2, 3, 4, 5 };

	/**
	 * The colors used to colorize the ingots
	 */
	public static int[] metalColors = new int[metalCount];

	/**
	 * The names of each metal that can be used to make alloys
	 */
	public static String[] metalNames = { "Iron", "Copper", "Zinc", "Magnesium", "Tantalum", "Flamium", "Vegetanium", "Aquatilum", "Swagtanium" };

	/**
	 * The names of each upgrade that can be used on machines
	 */
	public static String[] upgradeNames = { "Speed I", "Speed II", "Efficiency I", "Efficiency II", "Capacity I", "Capacity II", "Range I", "Range II" };
}
