package infinitealloys.util;

public final class Consts {

  public static final String MOD_ID = "infinitealloys";
  public static final String TEXTURE_PREFIX = MOD_ID + ":";

  public static final int METAL_COUNT = EnumMetal.values().length;
  public static final int MACHINE_COUNT = EnumMachine.values().length;
  public static final int UPGRADE_TYPE_COUNT = EnumUpgrade.values().length;
  public static final int VALID_ALLOY_COUNT = EnumAlloy.values().length;

  /**
   * The radix of the number system used for the alloys. The maximum value for a single metal in an
   * alloy is {@code ALLOY_RADIX} - 1.
   */
  public static final int ALLOY_RADIX = 10;

  /**
   * The amount of coords that the Internet Wand can hold
   */
  public static final int WAND_SIZE = 10;

  /**
   * GUI ID for the Wand GUI
   */
  public static final int WAND_GUI_ID = MACHINE_COUNT;

  /**
   * The amount of states that each mob can be in. Off, Attract, or Repel
   */
  public static final int PASTURE_MODES = 3;
}