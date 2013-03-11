package infinitealloys.util;

public class References {

	public static final String TEXTURE_PATH = "/infinitealloys/gfx/";
	public static final String LANG_PATH = "lang/";
	public static final String[] langFiles = { LANG_PATH + "en_US.xml" };

	public static final int MULTI_ITEM_COUNT = 2;
	public static final int METAL_COUNT = 8;
	public static final int MACHINE_COUNT = 5;
	public static final int UPGRADE_COUNT = 11;
	public static final int VALID_ALLOY_COUNT = 6;

	// These are the int values for block faces, NOT correct right now
	// TODO: Make this correct
	public static final int TOP = 0;
	public static final int BOTTOM = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;
	public static final int NORTH = 4;
	public static final int SOUTH = 5;

	/** The min for each metal in each valid alloy, the valid alloys will be between these and the maxes */
	public static final int[] validAlloyMins = { 11, 1111, 11111, 111111, 11111111, 44444444 };

	/** The max for each metal in each valid alloy, the valid alloys will be between these and the mins */
	public static final int[] validAlloyMaxes = { 55, 24477, 356688, 557799, 44446688, 99999999 };

	/** The radix of the number system used for the alloys */
	public static final int alloyRadix = 10;

	/** The colors used to colorize the ingots */
	public static int[] metalColors = new int[METAL_COUNT];

	/** The names of each metal that can be used to make alloys */
	public static final String[] metalNames = { "zinc", "magnesium", "scandium", "tantalum", "flamium", "vegetanium", "aquatilum", "swagtanium" };

	/** The names of each upgrade that can be used on machines */
	public static final String[] upgradeNames = { "speed1", "speed2", "efficiency1", "efficiency2", "capacity1", "capacity2", "range1", "range2", "wireless", "eleccapacity1", "eleccapacity2" };

	/** The amount of coords that the gps can hold */
	public static final int gpsMaxCoords = 10;
}