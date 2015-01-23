package infinitealloys.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

import infinitealloys.item.ItemIA;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.MachineHelper;

public class TEEMetalForge extends TileEntityElectric {

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
  public void updateEntity() {
    super.updateEntity();
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
      int ingotsToTakeFromSlot = Math.min(ingotsToRemove[ingotNum],
                                          inventoryStacks[slot].stackSize);
      ingotsToRemove[ingotNum] -= ingotsToTakeFromSlot;
      decrStackSize(slot, ingotsToTakeFromSlot);
    }
    ItemStack result = getIngotResult();

    if (inventoryStacks[0] == null) {
      inventoryStacks[0] = result; // If there are no alloys in the output slot, add this one
    } else {
      inventoryStacks[0].stackSize += result.stackSize; // Otherwise, increment the stack size
    }
  }

  @Override
  public int getRKChange() {
    return (int) (baseRKPerTick * rkPerTickMult / processTimeMult * getIngotsInRecipe());
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
  public Object[] getSyncDataToClient() {
    return ArrayUtils.addAll(super.getSyncDataToClient(), recipeAlloyID);
  }

  @Override
  public Object[] getSyncDataToServer() {
    return new Object[]{recipeAlloyID};
  }

  @Override
  public void handlePacketDataFromServer(byte recipeAlloyID) {
    if (recipeAlloyID != this.recipeAlloyID) {
      recipeChanged = true;
    }
    this.recipeAlloyID = recipeAlloyID;
  }

  public void handlePacketDataFromClient(byte recipeAlloyID) {
    if (recipeAlloyID != this.recipeAlloyID) {
      recipeChanged = true;
    }
    this.recipeAlloyID = recipeAlloyID;
  }

  /**
   * Return the resulting ingot for the smelted ingots
   *
   * @return The resulting ingot.
   */
  private ItemStack getIngotResult() {
    final ItemStack result = new ItemStack(ItemIA.alloyIngot);
    final NBTTagCompound tagCompound = new NBTTagCompound();
    tagCompound.setInteger("alloy", EnumAlloy.getAlloyForID(recipeAlloyID));
    result.setTagCompound(tagCompound);
    result.setItemDamage(recipeAlloyID + 1);
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
  private ArrayList<Integer> getSlotsWithIngot() {
    final ArrayList<Integer> slots = new ArrayList<Integer>();
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
    for (final int slot : getSlotsWithIngot()) {
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
    float[] speedUpgradeValues = {1F, 0.83F, 0.67F, 0.5F};
    processTimeMult = speedUpgradeValues[getUpgradeTier(Consts.SPEED)];

    float[] efficiencyUpgradeValues = {1F, 0.83F, 0.67F, 0.5F};
    rkPerTickMult = efficiencyUpgradeValues[getUpgradeTier(Consts.EFFICIENCY)];

    int[] capacityUpgradeValues = {32, 42, 52, 64};
    stackLimit = capacityUpgradeValues[getUpgradeTier(Consts.CAPACITY)];
  }

  @Override
  protected void populateValidUpgrades() {
    validUpgradeTypes.add(ItemIA.upgrades[Consts.SPEED]);
    validUpgradeTypes.add(ItemIA.upgrades[Consts.EFFICIENCY]);
    validUpgradeTypes.add(ItemIA.upgrades[Consts.CAPACITY]);
    validUpgradeTypes.add(ItemIA.upgrades[Consts.WIRELESS]);
    validUpgradeTypes.add(ItemIA.upgrades[Consts.ALLOY_UPG]);
  }
}
