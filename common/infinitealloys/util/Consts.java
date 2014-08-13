package infinitealloys.util;

public class Consts {

	public static final String MOD_ID = "infinitealloys";
	public static final String TEXTURE_PREFIX = MOD_ID + ":";

	public static final int MULTI_ITEM_COUNT = 2;
	public static final int METAL_COUNT = 8;
	public static final int MACHINE_COUNT = EnumMachine.values().length;
	public static final int UPGRADE_TYPE_COUNT = 6;
	public static final int VALID_ALLOY_COUNT = EnumAlloy.values().length;

	public static final int SPEED = 0;
	public static final int EFFICIENCY = 1;
	public static final int CAPACITY = 2;
	public static final int RANGE = 3;
	public static final int WIRELESS = 4;
	public static final int ALLOY_UPG = 5;

	public static final int BOTTOM = 0;
	public static final int TOP = 1;
	public static final int NORTH = 2;
	public static final int SOUTH = 3;
	public static final int WEST = 4;
	public static final int EAST = 5;

	/** The radix of the number system used for the alloys */
	public static final int ALLOY_RADIX = 10;

	/** The colors used to colorize the ingots */
	public static int[] metalColors = new int[METAL_COUNT];

	/** The names of each metal that can be used to make alloys */
	public static final String[] METAL_NAMES = { "Copper", "Aluminium", "Zinc", "Magnesium", "Tantalum", "Hydronium", "Roguite", "Verdite" };
	/** The names of each item that has damage values */
	public static final String[] MULTI_ITEM_NAMES = { "MachineComponent", "UpgradeComponent" };

	/** The amount of coords that the Internet Wand can hold */
	public static final int WAND_SIZE = 10;
	/** The GUI id for the Wand GUI */
	public static final int WAND_GUI_ID = MACHINE_COUNT;

	/** The amount of states that each mob can be in. Off, Attract, or Repel */
	public static final int PASTURE_MODES = 3;
	/** Amount of animals compatible with the pasture */
	public static final int PASTURE_ANIMALS = 4;
	/** Amount of monsters compatible with the pasture */
	public static final int PASTURE_MONSTERS = 4;
}