package infinitealloys.util;

import net.minecraft.util.IStringSerializable;

public enum EnumMetal implements IStringSerializable {

  COPPER("copper", 0xce7136),
  ZINC("zinc", 0x787d76),
  MAGNESIUM("magnesium", 0xd2cda3),
  TANTALUM("tantalum", 0xccc34f),
  HYDRONITE("hydronite", 0x141dce),
  ROGUITE("roguite", 0xae2305),
  VERDITE("verdite", 0x177c19),
  GROTANIUM("grotanium", 0x75009b);

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

  @Override
  public String getName() {
    return name;
  }

  public static EnumMetal byMetadata(int meta) {
    return values()[0 <= meta && meta < Consts.METAL_COUNT ? meta : 0];
  }
}