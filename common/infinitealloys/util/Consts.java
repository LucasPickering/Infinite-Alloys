package infinitealloys.util;

public class Consts {

	public static final String TEXTURE_DOMAIN = "infinitealloys";
	public static final String TEXTURE_PREFIX = TEXTURE_DOMAIN + ":";
	public static final String LANG_PATH = "/assets/infinitealloys/lang/";
	public static final String[] langFiles = { "en_US.xml" };

	public static final int MULTI_ITEM_COUNT = 2;
	public static final int METAL_COUNT = 8;
	public static final int TEU_COUNT = 8;
	public static final int UPGRADE_COUNT = 9;
	public static final int VALID_ALLOY_COUNT = 6;

	public static final int TOP = 0;
	public static final int BOTTOM = 1;
	public static final int NORTH = 2;
	public static final int SOUTH = 3;
	public static final int WEST = 4;
	public static final int EAST = 5;

	/** The radix of the number system used for the alloys */
	public static final int ALLOY_RADIX = 10;
	/** The min for each metal in each valid alloy, the valid alloys will be between these and the maxes */
	public static final int[] VALID_ALLOY_MINS = { 11, 1111, 11111, 111111, 11111111, 44444444 };
	/** The max for each metal in each valid alloy, the valid alloys will be between these and the mins */
	public static final int[] VALID_ALLOY_MAXES = { 55, 24477, 356688, 557799, 44446688, 99999999 };

	/** The colors used to colorize the ingots */
	public static int[] metalColors = new int[METAL_COUNT];

	/** The names of each metal that can be used to make alloys */
	public static final String[] METAL_NAMES = { "zinc", "magnesium", "scandium", "tantalum", "roguite", "verdite", "hydronium", "swagtanium" };
	/** The names of each item that has damage values */
	public static final String[] MULTI_ITEM_NAMES = { "machinecomponent", "upgradecomponent" };
	/** The names of each upgrade that can be used on machines */
	public static final String[] UPGRADE_NAMES = { "speed1", "speed2", "efficiency1", "efficiency2", "capacity1", "capacity2", "range1", "range2", "wireless" };

	/** The amount of coords that the Internet Wand can hold */
	public static final int WAND_MAX_COORDS = 10;

	/** The amount of states that each mob can be in. Off, Attract, or Repel */
	public static final int PASTURE_MODES = 3;
	/** Amount of animals compatible with the pasture */
	public static final int PASTURE_ANIMALS = 4;
	/** Amount of monsters compatible with the pasture */
	public static final int PASTURE_MONSTERS = 4;
}