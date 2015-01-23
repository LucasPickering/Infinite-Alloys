package infinitealloys.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

import infinitealloys.item.ItemIA;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;

public class MachineHelper {

  /**
   * How many blocks are searched per tick. Used to limit lag on the x-ray.
   */
  public static final int SEARCH_PER_TICK = 2000;

  /**
   * The blocks that the x-ray can detect and their values
   */
  private static ArrayList<DetectableBlock> detectables = new ArrayList<DetectableBlock>();

  /**
   * A list of the players who still need network information for the machines to be synced. This
   * sync is done when they first activate a machine.
   */
  public static ArrayList<String> playersToSync = new ArrayList<String>();

  /**
   * Add a block to the list of blocks that can be detected by the x-ray
   *
   * @param block the item that corresponds to the block that will be detected
   * @param value the amount the block is worth, higher value requires more energy to detect
   */
  public static void addDetectable(Item block, int metadata, int value) {
    detectables.add(new DetectableBlock(block, metadata, value));
  }

  /**
   * Add a block or blocks to the list of blocks that can be detected by the x-ray with an ore
   * dictionary string
   *
   * @param dictName the ore dictionary string from which the block(s) is/are retrieved
   * @param value    the amount the block(s) is/are worth, higher value requires more energy to
   *                 detect
   */
  public static void addDictDetectable(String dictName, int value) {
    for (ItemStack itemstack : OreDictionary.getOres(dictName)) {
      addDetectable(itemstack.getItem(), itemstack.getItemDamage(), value);
    }
  }

  public static boolean isDetectable(ItemStack stack) {
    return getDetectableValue(stack.getItem(), stack.getItemDamage()) > 0;
  }

  /**
   * Get the detectable value of the given ItemStack
   *
   * @return value of the block if it is detectable, otherwise 0
   */
  public static int getDetectableValue(Item block, int metadata) {
    for (DetectableBlock detectable : detectables) {
      if (detectable.block == block && detectable.metadata == metadata) {
        return detectable.value;
      }
    }
    return 0;
  }

  public static int getIngotNum(ItemStack ingot) {
    if (ingot.getItem() == ItemIA.ingot && ingot.getItemDamage() < Consts.METAL_COUNT) {
      return ingot.getItemDamage();
    }
    return -1;
  }

  /**
   * Is the machine at x, y, z capable of connecting to an ESU or computer?
   */
  public static boolean isClient(TileEntity te) {
    return te instanceof TileEntityMachine
           && ((TileEntityMachine) te).hasUpgrade(Consts.WIRELESS, 1)
           || te instanceof TileEntityElectric;
  }

  /**
   * A block that the x-ray can detect and identify
   */
  private static class DetectableBlock {

    private final Item block;
    private final int metadata;
    private final int value;

    private DetectableBlock(Item block, int metadata, int value) {
      this.block = block;
      this.metadata = metadata;
      this.value = value;
    }
  }

  /**
   * Get the color of the given alloy
   *
   * @param alloy the alloy's ID
   * @return the color of the alloy, as determined by mixing the colors of its component metals
   */
  public static int getAlloyColor(int alloy) {
    int colorCount = 0;
    int redTot = 0, greenTot = 0, blueTot = 0;

    for (EnumMetal metal : EnumMetal.values()) {
      int metalAmt = Funcs.intAtPos(alloy, Consts.ALLOY_RADIX, metal.ordinal());
      colorCount += metalAmt;
      // Get each color's byte from the hexcode and add it to that color's running total
      redTot += (metal.color >> 16 & 255) * metalAmt;
      greenTot += (metal.color >> 8 & 255) * metalAmt;
      blueTot += (metal.color & 255) * metalAmt;
    }

    int redAvg = 0, greenAvg = 0, blueAvg = 0;
    if (colorCount != 0) {
      redAvg = redTot / colorCount;
      greenAvg = greenTot / colorCount;
      blueAvg = blueTot / colorCount;
    }

    return (redAvg << 16) + (greenAvg << 8) + blueAvg;
  }
}
