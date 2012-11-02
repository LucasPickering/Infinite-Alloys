package infinitealloys;

import net.minecraft.client.Minecraft;

public class References {

	public static final String TEXTURE_PATH = "/infinitealloys/gfx/";
	public static final String OBJ_PATH = "./InfiniteAlloys/obj/";
	public static final String LANG_PATH = "./InfiniteAlloys/lang/";
	public static final String[] langFiles = { LANG_PATH + "en_US.xml" };

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
	 * The min for each metal in each valid alloy
	 */
	public static final int[] validAlloyMins = { 00000011, 00001111, 00011111, 00111111, 11111111, 44444444 };

	/**
	 * The max for each metal in each valid alloy
	 */
	public static final int[] validAlloyMaxes = { 00000055, 00024477, 356688, 557799, 44446688, 99999999 };

	/**
	 * The radix of the number system used for the alloys
	 */
	public static final int alloyRadix = 10;

	/**
	 * The colors used to colorize the ingots
	 */
	public static int[] metalColors = new int[metalCount];

	/**
	 * The names of each metal that can be used to make alloys
	 */
	public static final String[] metalNames = { "copper", "zinc", "magnesium", "tantalum", "flamium", "vegetanium", "aquatilum", "swagtanium" };

	/**
	 * The names of each upgrade that can be used on machines
	 */
	public static final String[] upgradeNames = { "Speed I", "Speed II", "Efficiency I", "Efficiency II", "Capacity I", "Capacity II", "Range I", "Range II", "Wireless Networking" };

	/**
	 * The amount of coords that the gps can hold
	 */
	public static final int gpsMaxCoords = 10;
}
