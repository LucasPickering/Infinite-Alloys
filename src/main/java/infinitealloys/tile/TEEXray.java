package infinitealloys.tile;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point3;
import io.netty.buffer.ByteBuf;

public class TEEXray extends TileEntityElectric {

  /**
   * A list of the detected blocks, x and z are relative to the machine, y is absolute
   */
  private final ArrayList<Point3> detectedBlocks = new ArrayList<>();
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
   * Was {@link #search} still running when it terminated?
   */
  private boolean searchingGround;

  /**
   * Should the process bar start to tick? Once it starts, this does NOT need to be true for it to
   * continue;
   */
  private boolean shouldStartProcess;

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
    if (!worldObj.isRemote && (searchingGround || inventoryStacks[0] != null
                                                  && detectedBlocks.isEmpty())) {
      search();
    }
  }

  @Override
  public boolean shouldProcess() {
    if (shouldStartProcess) {
      shouldStartProcess = false;
      return true;
    }
    return getProcessProgress() > 0;
  }

  @Override
  protected boolean shouldResetProgress() {
    return inventoryStacks[0] == null;
  }

  /**
   * Perform a search for the target block type. This checks {@link MachineHelper#SEARCH_PER_TICK}
   * blocks in a tick, then saves its place and picks up where it left off next tick. This should
   * ONLY be called on the server.
   *
   * @throws RuntimeException if called on the client
   */
  private void search() {
    if (worldObj.isRemote) {
      throw new RuntimeException("cannot search on client");
    }

    searchingGround = true;

    // Convenience variables for the data pertaining to the target block that is being searched for
    Block targetBlock = Block.getBlockFromItem(inventoryStacks[0].getItem());
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
          if (targetBlock == worldObj.getBlock(xCoord + x, y, zCoord + z)
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
        // If we've searched all the z values, reset the z position.
        lastSearch.z = -range;
      }
      // If we've searched all the x values, reset the x position.
      lastSearch.x = -range;
    }

    lastSearch.y = 0; // If we've searched all the y values, reset the y position.
    searchingGround = false; // The search is done. Stop running the function.
    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); // Mark the block so it will be synced
  }

  /**
   * Start the processing, i.e. make the progress bar start ticking.
   */
  @SideOnly(Side.CLIENT)
  public void startProcess() {
    shouldStartProcess = true;
    syncToServer();
  }

  public Point3[] getDetectedBlocks() {
    return detectedBlocks.toArray(new Point3[detectedBlocks.size()]);
  }

  @Override
  public void onInventoryChanged() {
    detectedBlocks.clear();
  }

  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    for (int i = 0; tagCompound.hasKey("detectedBlock" + i); i++) {
      int[] detectedBlock = tagCompound.getIntArray("detectedBlock" + i);
      detectedBlocks.add(new Point3(detectedBlock[0], detectedBlock[1], detectedBlock[2]));
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    for (int i = 0; i < detectedBlocks.size(); i++) {
      Point3 detectedBlock = detectedBlocks.get(i);
      tagCompound.setIntArray("detectedBlock" + i,
                              new int[]{detectedBlock.x, detectedBlock.y, detectedBlock.z});
    }
  }

  @Override
  public void readToClientData(ByteBuf bytes) {
    super.readToClientData(bytes);
    detectedBlocks.clear();
    int detectedBlocksSize = bytes.readInt();
    for (int i = 0; i < detectedBlocksSize; i++) {
      detectedBlocks.add(Point3.readFromByteBuf(bytes));
    }
    refreshGUI = true;
  }

  @Override
  public void writeToClientData(ByteBuf bytes) {
    super.writeToClientData(bytes);
    bytes.writeInt(detectedBlocks.size());
    for (Point3 detectedBlock : detectedBlocks) {
      detectedBlock.writeToByteBuf(bytes);
    }
  }

  @Override
  public void readToServerData(ByteBuf bytes) {
    super.readToServerData(bytes);
    // A sync packet is only sent to the server when the Search button is clicked,
    // so processing should always begin when the packet is received.
    shouldStartProcess = true;
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
