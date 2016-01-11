package infinitealloys.util;

import net.minecraft.util.IStringSerializable;

public enum EnumMetal implements IStringSerializable {

  COPPER("copper", 0xce7136, 1),
  ZINC("zinc", 0x787d76, 1),
  MAGNESIUM("magnesium", 0xd2cda3, 1),
  TANTALUM("tantalum", 0xccc34f, 1),
  HYDRONITE("hydronite", 0x141dce, 2),
  ROGUITE("roguite", 0xae2305, 2),
  VERDITE("verdite", 0x177c19, 2),
  GROTANIUM("grotanium", 0x75009b, 3);

  public final String name;
  public final int color;
  public final int harvestLevel;

  /**
   * Constructs a new EnumMetal object
   *
   * @param name         the name of the metal, in lower case
   * @param color        the color of the metal as an RGB hexcode
   * @param harvestLevel the level of pickaxe it takes to harvest the ore
   */
  EnumMetal(String name, int color, int harvestLevel) {
    this.name = name;
    this.color = color;
    this.harvestLevel = harvestLevel;
  }

  @Override
  public String getName() {
    return name;
  }

  public static EnumMetal byMetadata(int meta) {
    return values()[0 <= meta && meta < Consts.METAL_COUNT ? meta : 0];
  }
}