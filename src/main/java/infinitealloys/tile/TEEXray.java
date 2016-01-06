package infinitealloys.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import io.netty.buffer.ByteBuf;

public final class TEEXray extends TileEntityElectric {

  /**
   * A list of the detected blocks, x and z are relative to the machine, y is absolute
   */
  private final ArrayList<BlockPos> detectedBlocks = new ArrayList<>();
  public int range;

  /**
   * Client-only, the index of the button selected by the user, or -1 if no buttons are selected.
   */
  public int selectedButton = -1;

  /**
   * The last point that was checked for the target block in the previous iteration of {@link
   * #search}. The x and z coords are relative to the x-ray block; the y coord is absolute
   */
  private BlockPos lastSearch;

  /**
   * Should the block search run? Should only be modified server-side, as it has no effect
   * client-side.
   */
  private boolean shouldSearch;

  /**
   * Should the process bar progress?
   */
  private boolean shouldProcess;

  /**
   * Should the GUI display the
   */
  private boolean revealBlocks;

  public TEEXray() {
    super(2);
    stackLimit = 1;
    ticksToProcess = 2400;
    baseRKPerTick = -360;
  }

  @Override
  public EnumMachine getEnumMachine() {
    return EnumMachine.XRAY;
  }

  @Override
  public void update() {
    super.update();
    if (inventoryStacks[0] == null) {
      shouldSearch = false;
    } else if (!worldObj.isRemote && shouldSearch) {
      search();
    }
  }

  @Override
  public boolean shouldProcess() {
    return shouldProcess;
  }

  @Override
  protected void onFinishProcess() {
    shouldProcess = false;
    revealBlocks = true;
  }

  /**
   * Perform a search for the target block type. This checks {@link MachineHelper#SEARCH_PER_TICK}
   * blocks in a tick, then saves its place and picks up where it left off next tick. This should ONLY
   * be called on the server.
   *
   * @throws IllegalStateException if called on the client
   */
  private void search() {
    if (worldObj.isRemote) {
      throw new IllegalStateException("cannot search on client");
    }

    Block targetBlock = Block.getBlockFromItem(inventoryStacks[0].getItem());
    int targetMetadata = inventoryStacks[0].getItemDamage();

    // The amount of blocks that have been iterated over this tick.
    // When this reaches MachineHelper.SEARCH_PER_TICK, the loops break.
    int blocksSearched = 0;

    // If this is first tick of the search, clear detectedBlocks.
    if (lastSearch.equals(new BlockPos(-range, 0, -range))) {
      detectedBlocks.clear();
    }

    // Iterate over each block that is within the given range horizontally. Note that it searches
    // all blocks below x-ray within that horizontal range, which is why the y loop comes first and
    // why it looks a bit different from the x and z loops.
    for (int y = lastSearch.getY(); y <= pos.getY(); y++) {
      for (int x = lastSearch.getX(); x <= range; x++) {
        for (int z = lastSearch.getZ(); z <= range; z++) {
          BlockPos searchingPos = new BlockPos(pos.getX() + x, y, pos.getZ() + z);
          IBlockState blockState = worldObj.getBlockState(searchingPos);

          // If the block at the given coords (which have been converted to absolute coordinates) is
          // of the target block's type, add it to the list of blocks that have been found.
          if (targetBlock == blockState.getBlock()
              && targetMetadata == targetBlock.getMetaFromState(blockState)) {
            detectedBlocks.add(new BlockPos(x, y, z));
          }

          // If the amounts of blocks search this tick has reached the limit, save our place and end
          // the function. The search will be continued next tick.
          if (++blocksSearched >= MachineHelper.SEARCH_PER_TICK) {
            lastSearch = searchingPos;
            return;
          }
        }
        lastSearch = new BlockPos(lastSearch.getX(), lastSearch.getY(), -range);
      }
      lastSearch = new BlockPos(-range, lastSearch.getY(), lastSearch.getZ());
    }
    lastSearch = new BlockPos(lastSearch.getX(), 0, lastSearch.getZ());
    shouldSearch = false;
    worldObj.markBlockForUpdate(pos); // Mark this block so it will be synced
  }

  public BlockPos[] getDetectedBlocks() {
    return detectedBlocks.toArray(new BlockPos[detectedBlocks.size()]);
  }

  public boolean shouldSearch() {
    return shouldSearch;
  }

  public boolean shouldRevealBlocks() {
    return revealBlocks;
  }

  /**
   * Start the processing, i.e. make the progress bar start ticking. If this is client-side, also tell
   * the server to start processing.
   */
  public void startProcess() {
    shouldProcess = true;
    revealBlocks = false;
    if (worldObj.isRemote) {
      syncToServer();
    }
  }

  @Override
  public void onInventoryChanged(int slotIndex) {
    if (slotIndex == 0) {
      detectedBlocks.clear();
      shouldSearch = true;
      shouldProcess = false;
      revealBlocks = false;
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    shouldSearch = tagCompound.getBoolean("shouldSearch");
    shouldProcess = tagCompound.getBoolean("shouldProcess");
    revealBlocks = tagCompound.getBoolean("revealBlocks");
    for (int i = 0; tagCompound.hasKey("detectedBlock" + i); i++) {
      int[] detectedBlock = tagCompound.getIntArray("detectedBlock" + i);
      detectedBlocks.add(new BlockPos(detectedBlock[0], detectedBlock[1], detectedBlock[2]));
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    tagCompound.setBoolean("shouldSearch", shouldSearch);
    tagCompound.setBoolean("shouldProcess", shouldProcess);
    tagCompound.setBoolean("revealBlocks", revealBlocks);
    for (int i = 0; i < detectedBlocks.size(); i++) {
      BlockPos detectedBlock = detectedBlocks.get(i);
      tagCompound.setIntArray("detectedBlock" + i, new int[]{detectedBlock.getX(),
                                                             detectedBlock.getY(),
                                                             detectedBlock.getZ()});
    }
  }

  @Override
  public void readToClientData(ByteBuf bytes) {
    super.readToClientData(bytes);
    shouldProcess = bytes.readBoolean();
    revealBlocks = bytes.readBoolean();
    detectedBlocks.clear();
    int detectedBlocksSize = bytes.readInt();
    for (int i = 0; i < detectedBlocksSize; i++) {
      detectedBlocks.add(Funcs.readBlockPosFromByteBuf(bytes));
    }
  }

  @Override
  public void writeToClientData(ByteBuf bytes) {
    super.writeToClientData(bytes);
    bytes.writeBoolean(shouldProcess);
    bytes.writeBoolean(revealBlocks);
    bytes.writeInt(detectedBlocks.size());
    for (BlockPos detectedBlock : detectedBlocks) {
      Funcs.writeBlockPosToByteBuf(bytes, detectedBlock);
    }
  }

  @Override
  public void readToServerData(ByteBuf bytes) {
    super.readToServerData(bytes);
    // A sync packet is only sent to the server when the Search button is clicked,
    // so processing should always begin when the packet is received.
    shouldProcess = true;
    revealBlocks = false;
  }

  @Override
  protected void updateUpgrades() {
    float[] speedUpgradeValues = {1F, 1.33F, 1.67F, 2F};
    processSpeedMult = speedUpgradeValues[getUpgradeTier(EnumUpgrade.SPEED)];

    float[] efficiencyUpgradeValues = {1F, 0.83F, 0.67F, 0.5F};
    rkPerTickMult = efficiencyUpgradeValues[getUpgradeTier(EnumUpgrade.EFFICIENCY)];

    int[] rangeUpgradeValues = {4, 6, 8, 10};
    range = rangeUpgradeValues[getUpgradeTier(EnumUpgrade.RANGE)];

    lastSearch = new BlockPos(-range, 0, -range);
  }

  @Override
  protected void populateValidUpgrades() {
    addValidUpgradeType(EnumUpgrade.SPEED);
    addValidUpgradeType(EnumUpgrade.EFFICIENCY);
    addValidUpgradeType(EnumUpgrade.RANGE);
    addValidUpgradeType(EnumUpgrade.WIRELESS);
  }
}
