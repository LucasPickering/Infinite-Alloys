package infinitealloys.util;

public class Consts {

	public static final String TEXTURE_PATH = "/mods/infinitealloys/textures/";
	public static final String TEXTURE_PREFIX = "infinitealloys:";
	public static final String LANG_PATH = "lang/";
	public static final String[] langFiles = { LANG_PATH + "en_US.xml" };

	public static final int MULTI_ITEM_COUNT = 2;
	public static final int METAL_COUNT = 8;
	public static final int MACHINE_COUNT = 5;
	public static final int UPGRADE_COUNT = 11;
	public static final int VALID_ALLOY_COUNT = 6;

	public static final int TOP = 0;
	public static final int BOTTOM = 1;
	public static final int NORTH = 2;
	public static final int SOUTH = 3;
	public static final int WEST = 4;
	public static final int EAST = 5;

	/** The min for each metal in each valid alloy, the valid alloys will be between these and the maxes */
	public static final int[] VALID_ALLOY_MINS = { 11, 1111, 11111, 111111, 11111111, 44444444 };

	/** The max for each metal in each valid alloy, the valid alloys will be between these and the mins */
	public static final int[] VALID_ALLOY_MAXES = { 55, 24477, 356688, 557799, 44446688, 99999999 };

	/** The radix of the number system used for the alloys */
	public static final int ALLOY_RADIX = 10;

	/** The colors used to colorize the ingots */
	public static int[] metalColors = new int[METAL_COUNT];

	/** The names of each metal that can be used to make alloys */
	public static final String[] METAL_NAMES = { "zinc", "magnesium", "scandium", "tantalum", "flamium", "vegetanium", "aquatilum", "swagtanium" };

	/** The names of each machine */
	public static final String[] MACHINE_NAMES = { "computer", "metalforge", "analyzer", "printer", "xray" };

	/** The names of each multi item */
	public static final String[] MULTI_ITEM_NAMES = { "machinecomponent", "upgradecomponent" };

	/** The names of each upgrade that can be used on machines */
	public static final String[] UPGRADE_NAMES = { "speed1", "speed2", "efficiency1", "efficiency2", "capacity1", "capacity2", "range1", "range2", "wireless", "eleccapacity1", "eleccapacity2" };

	/** The amount of coords that the gps can hold */
	public static final int GPS_MAX_COORDS = 10;
}