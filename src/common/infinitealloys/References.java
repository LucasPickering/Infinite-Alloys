package infinitealloys;

public class References {

	public static final String TEXTURE_PATH = "/infinitealloys/gfx/";
	public static final String LANG_PATH = "lang/";
	public static final String[] langFiles = { LANG_PATH + "en_US.xml" };
	public static final int metalCount = 8;
	public static final int machineCount = 5;
	public static final int upgradeCount = 11;
	public static final int validAlloyCount = 6;

	/** The min for each metal in each valid alloy */
	public static final int[] validAlloyMins = { 11, 1111, 11111, 111111, 11111111, 44444444 };

	/** The max for each metal in each valid alloy */
	public static final int[] validAlloyMaxes = { 55, 24477, 356688, 557799, 44446688, 99999999 };

	/** The radix of the number system used for the alloys */
	public static final int alloyRadix = 10;

	/** The colors used to colorize the ingots */
	public static int[] metalColors = new int[metalCount];

	/** The names of each metal that can be used to make alloys */
	public static final String[] metalNames = { "zinc", "magnesium", "scandium", "tantalum", "flamium", "vegetanium", "aquatilum", "swagtanium" };

	/** The names of each upgrade that can be used on machines */
	public static final String[] upgradeNames = { "speed1", "speed2", "efficiency1", "efficiency2", "capacity1", "capacity2", "range1", "range2", "wireless", "eleccapacity1", "eleccapacity2" };

	/** The amount of coords that the gps can hold */
	public static final int gpsMaxCoords = 10;
}
