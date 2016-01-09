package infinitealloys.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedList;
import java.util.List;

import infinitealloys.item.IAItems;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.MachineHelper;
import io.netty.buffer.ByteBuf;

public final class TEEMetalForge extends TileEntityElectric {

  /**
   * The ID of the alloy that is currently set as the recipe
   */
  public byte recipeAlloyID = -1;

  /**
   * True if the alloy recipe has been changed by the client, used to reset progress
   */
  private boolean recipeChanged;

  public TEEMetalForge() {
    super(20);
    baseRKPerTick = -18;
  }

  @Override
  public EnumMachine getEnumMachine() {
    return EnumMachine.METAL_FORGE;
  }

  @Override
  public void update() {
    super.update();
    if (recipeAlloyID < 0 && hasUpgrade(EnumUpgrade.ALLOY, 1)) {
      recipeAlloyID = 0;
    }
    recipeChanged = false;
  }

  @Override
  public boolean shouldProcess() {
    return (inventoryStacks[0] == null
            || inventoryStacks[0].isItemEqual(getIngotResult())
               && inventoryStacks[0].stackSize < getInventoryStackLimit()) && hasSufficientIngots();
  }

  @Override
  protected boolean shouldResetProgress() {
    return !hasSufficientIngots() || recipeChanged;
  }

  @Override
  protected void onFinishProcess() {
    int[] ingotsToRemove = new int[Consts.METAL_COUNT];
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      ingotsToRemove[i] = EnumAlloy.getMetalAmt(recipeAlloyID, i);
    }
    for (int slot : getSlotsWithIngot()) {
      int ingotNum = MachineHelper.getIngotNum(inventoryStacks[slot]);
      int ingots = ingotsToRemove[ingotNum];
      ingotsToRemove[ingotNum] -=
          Math.min(ingotsToRemove[ingotNum], inventoryStacks[slot].stackSize);
      decrStackSize(slot, Math.min(ingots, inventoryStacks[slot].stackSize));
    }
    final ItemStack result = getIngotResult();

    if (inventoryStacks[0] == null) {
      inventoryStacks[0] = result; // If there are no alloys in the output slot, add this one
    } else {
      inventoryStacks[0].stackSize += result.stackSize; // Otherwise, increment the stack size
    }
  }

  @Override
  public int getRKChange() {
    return (int) (baseRKPerTick * rkPerTickMult / processSpeedMult * getIngotsInRecipe());
  }

  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    recipeAlloyID = tagCompound.getByte("recipeAlloyID");
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    tagCompound.setByte("recipeAlloyID", recipeAlloyID);
  }

  @Override
  public void readToClientData(ByteBuf bytes) {
    super.readToClientData(bytes);
    byte recipeAlloyID = bytes.readByte();
    if (recipeAlloyID != this.recipeAlloyID) {
      recipeChanged = true;
    }
    this.recipeAlloyID = recipeAlloyID;
  }

  @Override
  public void writeToClientData(ByteBuf bytes) {
    super.writeToClientData(bytes);
    bytes.writeByte(recipeAlloyID);
  }

  @Override
  public void readToServerData(ByteBuf bytes) {
    super.readToServerData(bytes);
    byte recipeAlloyID = bytes.readByte();
    if (recipeAlloyID != this.recipeAlloyID) {
      recipeChanged = true;
    }
    this.recipeAlloyID = recipeAlloyID;
  }

  @Override
  public void writeToServerData(ByteBuf bytes) {
    super.writeToServerData(bytes);
    bytes.writeByte(recipeAlloyID);
  }

  /**
   * Get the alloy ingot that will result from the current recipe
   *
   * @return The resulting ingot.
   */
  private ItemStack getIngotResult() {
    NBTTagCompound tagCompound = new NBTTagCompound();
    tagCompound.setInteger("alloy", EnumAlloy.getAlloyForID(recipeAlloyID));
    ItemStack result = new ItemStack(IAItems.alloyIngot, 1, recipeAlloyID + 1);
    result.setTagCompound(tagCompound);
    return result;
  }

  /**
   * Does the inventory of the forge contain enough ingots to fulfill the current recipe?
   */
  private boolean hasSufficientIngots() {
    if (recipeAlloyID == -1) {
      return false;
    }
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      if (getAvailableIngots()[i] < EnumAlloy.getMetalAmt(recipeAlloyID, i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Get a list of the metal slots that contain an ingot
   */
  private List<Integer> getSlotsWithIngot() {
    final List<Integer> slots = new LinkedList<>();
    for (int i = 1; i < 19; i++) {
      if (inventoryStacks[i] != null) {
        slots.add(i);
      }
    }
    return slots;
  }

  /**
   * Get the amount of ingots of each metal in the inventory that are available for use
   */
  private int[] getAvailableIngots() {
    final int[] amts = new int[Consts.METAL_COUNT];
    for (int slot : getSlotsWithIngot()) {
      amts[MachineHelper.getIngotNum(inventoryStacks[slot])] += inventoryStacks[slot].stackSize;
    }
    return amts;
  }

  /**
   * Get the total quantity of ingots in the recipe
   */
  private int getIngotsInRecipe() {
    int ingots = 0;
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      ingots += EnumAlloy.getMetalAmt(recipeAlloyID, i);
    }
    return ingots;
  }

  @Override
  protected void updateUpgrades() {
    float[] speedUpgradeValues = {1F, 1.33F, 1.67F, 2F};
    processSpeedMult = speedUpgradeValues[getUpgradeTier(EnumUpgrade.SPEED)];

    float[] efficiencyUpgradeValues = {1F, 0.83F, 0.67F, 0.5F};
    rkPerTickMult = efficiencyUpgradeValues[getUpgradeTier(EnumUpgrade.EFFICIENCY)];

    int[] capacityUpgradeValues = {32, 42, 52, 64};
    stackLimit = capacityUpgradeValues[getUpgradeTier(EnumUpgrade.CAPACITY)];
  }

  @Override
  protected void populateValidUpgrades() {
    addValidUpgradeType(EnumUpgrade.SPEED);
    addValidUpgradeType(EnumUpgrade.EFFICIENCY);
    addValidUpgradeType(EnumUpgrade.CAPACITY);
    addValidUpgradeType(EnumUpgrade.WIRELESS);
    addValidUpgradeType(EnumUpgrade.ALLOY);
  }
}
