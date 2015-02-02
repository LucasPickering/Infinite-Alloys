package infinitealloys.tile;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point3;
import io.netty.buffer.ByteBuf;

public class TEEXray extends TileEntityElectric {

  /**
   * A list of the detected blocks, x and z are relative to the machine, y is absolute
   */
  public final ArrayList<Point3> detectedBlocks = new ArrayList<>();
  public int range;

  /**
   * Client-only, the index of the button selected by the user, or -1 if no buttons are selected.
   */
  public int selectedButton = -1;

  /**
   * Client-only, set to true when a sync packet comes in to refresh the GUI
   */
  public boolean refreshGUI;

  /**
   * The last point that was checked for the target block in the previous iteration of {@link
   * #search}. The x and z coords are relative to the x-ray block; the y coord is absolute
   */
  private Point3 lastSearch;

  /**
   * Should searching continue, or is it complete. Set this to true to begin a search.
   */
  public boolean shouldSearch;

  public TEEXray() {
    super(2);
    stackLimit = 1;
    ticksToProcess = 240; // TODO: Change this back to 2400
    baseRKPerTick = -360;
  }

  @Override
  public EnumMachine getEnumMachine() {
    return EnumMachine.XRAY;
  }

  @Override
  public void updateEntity() {
    super.updateEntity();

    if (inventoryStacks[0] == null) {
      shouldSearch = false;
    } else if (!worldObj.isRemote && shouldSearch) {
      search();
    }
  }

  @Override
  public boolean shouldProcess() {
    return shouldSearch || getProcessProgress() > 0;
  }

  @Override
  protected boolean shouldResetProgress() {
    return inventoryStacks[0] == null;
  }

  /**
   * Called when processProgress reaches ticksToProgress
   */
  @Override
  protected void onFinishProcess() {
    if (worldObj.isRemote) {
      shouldSearch = false;
    }
  }

  /**
   * Perform a search for the target block. This checks {@link infinitealloys.util.MachineHelper#SEARCH_PER_TICK
   * a set amount of} blocks in a tick, then saves its place and picks up where it left off next
   * tick. This eliminates stutter during searches.
   */
  private void search() {
    // Convenience variables for the data pertaining to the target block that is being searched for
    Item targetBlock = inventoryStacks[0].getItem();
    int targetMetadata = inventoryStacks[0].getItemDamage();

    // The amount of blocks that have been iterated over this tick. When this reaches TEHelper.SEARCH_PER_TICK, the loops break
    int blocksSearched = 0;

    // (-range, 0, -range) is the start point for the search. When lastSearch == (-range, 0, -range), this is the first tick of the search. The block list
    // is cleared to be repopulated in this search.
    if (lastSearch.equals(-range, 0, -range)) {
      detectedBlocks.clear();
    }

    // Iterate over each block that is within the given range horizontally. Note that it searches all blocks below x-ray within that horizontal range, which
    // is why the y loop comes first and why it looks a bit different from the x and z loops.
    for (int y = lastSearch.y; y <= yCoord; y++) {
      for (int x = lastSearch.x; x <= range; x++) {
        for (int z = lastSearch.z; z <= range; z++) {

          // If the block at the given coords (which have been converted to absolute coordinates) is of the target block's type, add it to the
          // list of blocks that have been found.
          if (targetBlock == Item.getItemFromBlock(worldObj.getBlock(xCoord + x, y, zCoord + z))
              && targetMetadata == worldObj.getBlockMetadata(xCoord + x, y, zCoord + z)) {
            detectedBlocks.add(new Point3(x, y, z));
          }

          // If the amounts of blocks search this tick has reached the limit, save our place and end the function. The search will be
          // continued next tick.
          if (++blocksSearched >= MachineHelper.SEARCH_PER_TICK) {
            lastSearch.set(x, y, z);
            return;
          }
        }
        // If we've search all the z values, reset the z position.
        lastSearch.z = -range;
      }
      // If we've search all the x values, reset the x position.
      lastSearch.x = -range;
    }

    lastSearch.y = 0; // If we've search all the y values, reset the y position.
    shouldSearch =
        false; // The search is done. Stop running the function until another search is initiated.
    worldObj.markBlockForUpdate(xCoord, yCoord,
                                zCoord); // Mark the block so that the search info will be synced to clients
  }

  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    // True if there were blocks before to be restored, false if it was empty
    shouldSearch = tagCompound.getBoolean("shouldSearch");
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    // True if there are blocks on the GUI, false if there are no blocks
    tagCompound.setBoolean("shouldSearch", detectedBlocks.size() > 0);
  }

  @Override
  public Object[] getSyncDataToClient() {
    return ArrayUtils.addAll(super.getSyncDataToClient(),
                             detectedBlocks.size(), detectedBlocks.toArray());
  }

  @Override
  public Object[] getSyncDataToServer() {
    return new Object[]{shouldSearch};
  }

  @Override
  public void readServerToClientData(ByteBuf bytes) {
    super.readServerToClientData(bytes);
    detectedBlocks.clear();
    int detectedBlocksSize = bytes.readInt();
    for (int i = 0; i < detectedBlocksSize; i++) {
      detectedBlocks.add(new Point3(bytes.readInt(), bytes.readInt(), bytes.readInt()));
    }
    refreshGUI = true;
  }

  @Override
  public void readClientToServerData(ByteBuf bytes) {
    super.readClientToServerData(bytes);
    shouldSearch = bytes.readBoolean();
  }

  @Override
  protected void updateUpgrades() {
    float[] speedUpgradeValues = {1F, 0.83F, 0.67F, 0.5F};
    processTimeMult = speedUpgradeValues[getUpgradeTier(EnumUpgrade.SPEED)];

    float[] efficiencyUpgradeValues = {1F, 1.33F, 1.67F, 2F};
    rkPerTickMult = efficiencyUpgradeValues[getUpgradeTier(EnumUpgrade.EFFICIENCY)];

    int[] rangeUpgradeValues = {4, 6, 8, 10};
    range = rangeUpgradeValues[getUpgradeTier(EnumUpgrade.RANGE)];

    if (lastSearch == null) {
      lastSearch = new Point3(-range, 0, -range);
    } else {
      lastSearch.set(-range, 0, -range);
    }
  }

  @Override
  protected void populateValidUpgrades() {
    addValidUpgradeType(EnumUpgrade.SPEED);
    addValidUpgradeType(EnumUpgrade.EFFICIENCY);
    addValidUpgradeType(EnumUpgrade.RANGE);
    addValidUpgradeType(EnumUpgrade.WIRELESS);
  }
}
