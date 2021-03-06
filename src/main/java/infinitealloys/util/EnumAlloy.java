package infinitealloys.util;

import infinitealloys.core.InfiniteAlloys;

public enum EnumAlloy {

  // KEEP IN MIND: RIGHTMOST DIGITS ARE THE LESSER METALS
  ALLOY0("alloy0", 11, 55),
  ALLOY1("alloy1", 1111, 4477),
  ALLOY2("alloy2", 11111, 556688),
  ALLOY3("alloy3", 111111, 557799),
  ALLOY4("alloy4", 11110000, 55550000),
  ALLOY5("alloy5", 44444444, 99999999);

  /**
   * Get the alloy value (which lists its metal composition) of the alloy with the given ID. This
   * function is safe in that if you give it a bad alloy ID (outside the range [0, Amount of
   * alloys)), it will just return 0
   */
  public static int getAlloyForID(int id) {
    if (id < 0 || id >= values().length) {
      return 0;
    }
    return InfiniteAlloys.instance.getValidAlloys()[id];
  }

  /**
   * Get the ID of the of the alloy with the given alloy value (which lists its metal composition)
   */
  public static int getIDForAlloy(int alloy) {
    for (int i = 0; i < Consts.VALID_ALLOY_COUNT; i++) {
      if (alloy == getAlloyForID(i)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Get the amount of a certain metal in the alloy with the given ID
   */
  public static int getMetalAmt(int alloyID, int metalID) {
    return Funcs.intAtPos(getAlloyForID(alloyID), Consts.ALLOY_RADIX, metalID);
  }

  public final String name;
  public final int min;
  public final int max;

  EnumAlloy(String name, int min, int max) {
    this.name = name;
    this.min = min;
    this.max = max;
  }

  /**
   * Get the alloy value of this alloy
   */
  public int getAlloy() {
    return InfiniteAlloys.instance.getValidAlloys()[ordinal()];
  }

}
