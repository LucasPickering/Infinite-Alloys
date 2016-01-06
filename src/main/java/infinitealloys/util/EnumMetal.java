package infinitealloys.util;

public enum EnumMetal {

  COPPER("copper", 0xCE7136),
  ALUMINIUM("aluminium", 0xCBCEC7), // Yes it has to be spelled that way
  ZINC("zinc", 0x787D76),
  MAGNESIUM("magnesium", 0xD2CDA3),
  TANTALUM("tantalum", 0xCCC34F),
  HYDRONIUM("hydronium", 0x141DCE),
  ROGUITE("roguite", 0xAE2305),
  VERDITE("verdite", 0x177C19);

  public final String name;
  public final int color;

  /**
   * Constructs a new EnumMetal object
   *
   * @param name  the name of the metal, in lower case
   * @param color the color of the metal as an RGB hexcode
   */
  EnumMetal(String name, int color) {
    this.name = name;
    this.color = color;
  }

  public static EnumMetal byMetadata(int meta) {
    return values()[0 <= meta && meta < Consts.METAL_COUNT ? meta : 0];
  }
}