package infinitealloys;

import java.util.ArrayList;
import java.util.Arrays;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityMetalForge extends TileEntityMachine {

	/**
	 * The amount of ticks that the fuel in the slot will burn for
	 */
	public int currentFuelBurnTime;
	public int heatLeft;

	/**
	 * The multiplier for the fuel burn time
	 */
	public float fuelBonus = 1F;

	/**
	 * Ticks it takes to finish smelting one ingot
	 */
	public int ticksToFinish = 12800;

	/**
	 * The smelting progress
	 */
	public int smeltProgress;

	/**
	 * An array for the "stack sizes" of each ingot in the recipe setting
	 */
	public byte[] recipeAmts = new byte[References.metalCount];

	public TileEntityMetalForge(int facing) {
		this();
		orientation = (byte)facing;
	}

	public TileEntityMetalForge() {
		super(9);
		inventoryStacks = new ItemStack[29];
		orientation = 2;
	}

	@Override
	public String getInvName() {
		return "Metal Forge";
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		currentFuelBurnTime = tagCompound.getShort("CurrentFuelBurnTime");
		heatLeft = tagCompound.getShort("HeatLeft");
		smeltProgress = tagCompound.getShort("SmeltProgress");
		for(int i = 0; i < recipeAmts.length; i++)
			recipeAmts[i] = tagCompound.getByte("Recipe Amount " + i);
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setShort("CurrentFuelBurnTime", (short)currentFuelBurnTime);
		tagCompound.setShort("HeatLeft", (short)heatLeft);
		tagCompound.setShort("SmeltProgress", (short)smeltProgress);
		for(int i = 0; i < recipeAmts.length; i++)
			tagCompound.setByte("Recipe Amount " + i, recipeAmts[i]);
	}

	public void handlePacketDataFromServer(int currentFuelBurnTime, int heatLeft, int smeltProgress, byte[] recipeAmts) {
		this.currentFuelBurnTime = currentFuelBurnTime;
		this.heatLeft = heatLeft;
		this.smeltProgress = smeltProgress;
		this.recipeAmts = recipeAmts;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		updateUpgrades();
		boolean invChanged = false;
		if(heatLeft < getIngotsInRecipe()) {
			currentFuelBurnTime = 0;
			if(inventoryStacks[0] != null)
				currentFuelBurnTime = (int)(TileEntityFurnace.getItemBurnTime(inventoryStacks[0]) * fuelBonus);
			if(shouldBurn()) {
				heatLeft = currentFuelBurnTime;
				invChanged = true;
				if(--inventoryStacks[0].stackSize <= 0)
					inventoryStacks[0] = null;
			}
		}
		if(shouldBurn()) {
			smeltProgress += getInventoryStackLimit() - getIngotsInRecipe() + 1;
			heatLeft -= getIngotsInRecipe();
			if(smeltProgress >= ticksToFinish) {
				smeltProgress = 0;
				smeltItem();
				invChanged = true;
			}
		}
		else
			smeltProgress = 0;
		BlockMachine.updateBlockState(worldObj, xCoord, yCoord, zCoord);
		if(invChanged)
			onInventoryChanged();
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {
		if(side == ForgeDirection.DOWN) return 1;
		if(side == ForgeDirection.UP) return 0;
		return 2;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		return 1;
	}

	@Override
	public boolean isUpgradeValid(ItemStack upgrade) {
		return super.isUpgradeValid(upgrade);
	}

	private boolean shouldBurn() {
		int typesInRecipe = 0;
		ArrayList<Boolean> sufficientIngots = new ArrayList<Boolean>();
		for(int amt : recipeAmts)
			if(amt > 0)
				typesInRecipe++;
		for(int i = 0; i < getIngotAmts().length; i++)
			sufficientIngots.add(getIngotAmts()[i] >= recipeAmts[i]);
		return typesInRecipe > 1 && !sufficientIngots.contains(false) && (heatLeft >= getIngotsInRecipe() || currentFuelBurnTime > 0) && (inventoryStacks[10] == null || (inventoryStacks[10].isItemEqual(getIngotResult()) && getInventoryStackLimit() - inventoryStacks[10].stackSize >= getIngotsInRecipe()));
	}

	/**
	 * Updates the settings based on the speed, capacity, and efficiency
	 * upgrades.
	 */
	protected void updateUpgrades() {
		canNetwork = (upgrades & 256) == 256;
	}

	private void smeltItem() {
		byte[] ingotsToRemove = Arrays.copyOf(recipeAmts, recipeAmts.length);
		for(int slot : getSlotsWithIngot()) {
			int ingotNum = getIngotNum(inventoryStacks[slot]);
			int ingots = ingotsToRemove[ingotNum];
			ingotsToRemove[ingotNum] -= Math.min(ingotsToRemove[ingotNum], inventoryStacks[slot].stackSize);
			decrStackSize(slot, Math.min(ingots, inventoryStacks[slot].stackSize));
		}
		ItemStack ingotResult = getIngotResult();
		if(inventoryStacks[10] == null)
			inventoryStacks[10] = ingotResult;
		else if(inventoryStacks[10].getTagCompound().getInteger("alloy") == ingotResult.getTagCompound().getInteger("alloy"))
			inventoryStacks[10].stackSize += ingotResult.stackSize;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Get a scaled cook progress, used for the gui progress bar
	 * @param i Scale
	 * @return Scaled progress
	 */
	public int getCookProgressScaled(int i) {
		return smeltProgress * i / ticksToFinish;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Get a scaled burn time, used for the gui flames
	 * @param i Scale
	 * @return Scaled burn time
	 */
	public int getBurnTimeRemainingScaled(int i) {
		return heatLeft * i / (currentFuelBurnTime == 0 ? 0 : currentFuelBurnTime);
	}

	public int getIngotNum(ItemStack ingot) {
		if(ingot.itemID == InfiniteAlloys.ingot.shiftedIndex && ingot.getItemDamage() < References.metalCount)
			return ingot.getItemDamage();
		return -1;
	}

	/**
	 * Return the resulting ingot for the smelted ingots
	 * 
	 * @return The resulting ingot.
	 */
	private ItemStack getIngotResult() {
		int alloy = 0;
		for(int i = 0; i < recipeAmts.length; i++)
			alloy += Math.pow(References.alloyRadix, i) * recipeAmts[i];
		ItemStack result = new ItemStack(InfiniteAlloys.alloyIngot, getIngotsInRecipe());
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setInteger("alloy", alloy);
		result.setTagCompound(tagCompound);
		return result;
	}

	private ArrayList<Integer> getSlotsWithIngot() {
		ArrayList<Integer> slots = new ArrayList<Integer>();
		for(int i = 11; i < 29; i++)
			if(inventoryStacks[i] != null)
				slots.add(i);
		return slots;
	}

	private int[] getIngotAmts() {
		int[] amts = new int[References.metalCount];
		for(int slot : getSlotsWithIngot())
			amts[getIngotNum(inventoryStacks[slot])] += inventoryStacks[slot].stackSize;
		return amts;
	}

	private int getIngotsInRecipe() {
		int ingots = 0;
		for(int amt : recipeAmts)
			ingots += amt;
		return ingots;
	}
}
