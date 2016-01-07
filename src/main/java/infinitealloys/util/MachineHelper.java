package infinitealloys.util;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashSet;

import infinitealloys.item.IAItems;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;

public final class MachineHelper {

  /**
   * How many block are searched per tick. Used to limit lag on the x-ray.
   */
  public static final int SEARCH_PER_TICK = 2000;

  /**
   * A set of the block that the x-ray can detect. As a {@link java.util.Set}, this will not take
   * duplicates of each block.
   */
  private static HashSet<DetectableBlock> detectableBlocks = new HashSet<>();

  /**
   * A list of the players who still need network information for the machines to be synced. This sync
   * is done when they first activate a machine.
   */
  public static ArrayList<String> playersToSync = new ArrayList<>();

  /**
   * Add a block or block to the set of block that can be detected by the x-ray via the block(s)'s
   * {@link net.minecraftforge.oredict.OreDictionary} identifier String.
   *
   * @param dictName the ore dictionary string from which the block(s) is/are retrieved
   * @param color    the color of the outline that will be used for this block
   * @param value    the amount the block is worth, higher value requires more energy to detect
   */
  public static void addDictDetectable(String dictName, int color, int value) {
    for (ItemStack itemstack : OreDictionary.getOres(dictName)) {
      addDetectable(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage(), color,
                    value);
    }
  }

  /**
   * Add a block to the list of block that can be detected by the x-ray
   *
   * @param block    the block type of the detectable block
   * @param metadata the metadata of the detectable block
   * @param color    the color of the outline that will be used for this block
   * @param value    the amount the block is worth, higher value requires more energy to detect
   */
  public static void addDetectable(Block block, int metadata, int color, int value) {
    detectableBlocks.add(new DetectableBlock(block, metadata, color, value));
  }

  public static boolean isDetectable(ItemStack stack) {
    return getDetectableValue(Block.getBlockFromItem(stack.getItem()), stack.getItemDamage()) > 0;
  }

  /**
   * Get the color of the detectable block with the given information.
   *
   * @param block    the block type of the detectable block
   * @param metadata the metdata of the detectable block
   * @return color of the block if it is detectable, otherwise 0
   */
  public static int getDetectableColor(Block block, int metadata) {
    for (DetectableBlock detectable : detectableBlocks) {
      if (detectable.block == block && detectable.metadata == metadata) {
        return detectable.color;
      }
    }
    return 0;
  }

  /**
   * Get the value of the detectable block with the given information.
   *
   * @param block    the block type of the detectable block
   * @param metadata the metadata of the detectable block
   * @return value of the block if it is detectable, otherwise 0
   */
  public static int getDetectableValue(Block block, int metadata) {
    for (DetectableBlock detectable : detectableBlocks) {
      if (detectable.block == block && detectable.metadata == metadata) {
        return detectable.value;
      }
    }
    return 0;
  }

  public static int getIngotNum(ItemStack ingot) {
    if (ingot.getItem() == IAItems.ingot && ingot.getItemDamage() < Consts.METAL_COUNT) {
      return ingot.getItemDamage();
    }
    return -1;
  }

  /**
   * Is the machine at x, y, z capable of connecting to an ESU or computer?
   */
  public static boolean isClient(TileEntity te) {
    return te instanceof TileEntityMachine
           && ((TileEntityMachine) te).hasUpgrade(EnumUpgrade.WIRELESS, 1)
           || te instanceof TileEntityElectric;
  }

  /**
   * Get the color of an alloy based on its composition
   *
   * @param alloy the alloy
   * @return a color for the alloy
   */
  public static int getColorForAlloy(int alloy) {
    int colorCount = 0;
    int redTot = 0, greenTot = 0, blueTot = 0;

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      int ingotColor = EnumMetal.values()[i].color;
      int alloyAmt = Funcs.intAtPos(alloy, Consts.ALLOY_RADIX, i);
      colorCount += alloyAmt;

      redTot += (ingotColor >> 16 & 255) * alloyAmt; // Get the red byte
      greenTot += (ingotColor >> 8 & 255) * alloyAmt; // Get the green byte
      blueTot += (ingotColor & 255) * alloyAmt; // Get the blue byte
    }

    int redAvg = 0, greenAvg = 0, blueAvg = 0;
    if (colorCount != 0) {
      redAvg = redTot / colorCount;
      greenAvg = greenTot / colorCount;
      blueAvg = blueTot / colorCount;
    }
    return (redAvg << 16) + (greenAvg << 8) + blueAvg;
  }

  /**
   * A block that the x-ray can detect and identify
   */
  private static class DetectableBlock {

    private final Block block;
    private final int metadata;
    private final int color;
    private final int value;

    /**
     * Constructs a new {@code DetectableBlock} object.
     *
     * @param block    the block type of the detectable block
     * @param metadata the metadata of the detectable block
     * @param color    the color of the outline that will highlight this block
     * @param value    the value of this block, valuable block require more energy to discover
     */
    private DetectableBlock(Block block, int metadata, int color, int value) {
      this.block = block;
      this.metadata = metadata;
      this.color = color;
      this.value = value;
    }

    /**
     * {@inheritDoc} Two {@code DetectableBlocks} are considered equal if their {@code block} and
     * {@code metadata} are equal, regardless of each {@code DetectableBlock's} {@code color} and
     * {@code value}.
     */
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof DetectableBlock)) {
        return false;
      }

      DetectableBlock that = (DetectableBlock) o;
      return block == that.block && metadata == that.metadata;
    }

    @Override
    public int hashCode() {
      int result = block.hashCode();
      result = 31 * result + metadata;
      return result;
    }
  }
}
