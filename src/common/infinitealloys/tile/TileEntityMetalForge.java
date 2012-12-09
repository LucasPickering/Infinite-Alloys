package infinitealloys.tile;

import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityMetalForge extends TileEntityMachine {

	/** An array for the "stack sizes" of each ingot in the recipe setting */
	public byte[] recipeAmts = new byte[References.metalCount];
	/** recipeAmts from last tick, used to tell if the recipe has changed to reset progress */
	private byte[] lastRecipeAmts = new byte[References.metalCount];
	public byte presetSelection = -1;

	public TileEntityMetalForge(ForgeDirection facing) {
		this();
		front = facing;
	}

	public TileEntityMetalForge() {
		super(1);
		inventoryStacks = new ItemStack[21];
		ticksToProcess = 12800;
	}

	@Override
	public String getInvName() {
		return "Metal Forge";
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {
		if(side == ForgeDirection.UP || side == ForgeDirection.DOWN)
			return 2;
		return 3;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		if(side == ForgeDirection.UP || side == ForgeDirection.DOWN)
			return 1;
		return 18;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		recipeAmts = tagCompound.getByteArray("RecipeAmts");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setByteArray("RecipeAmts", recipeAmts);
	}

	public void handlePacketData(byte[] recipeAmts) {
		this.recipeAmts = recipeAmts;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		boolean invChanged = false;
		joulesUsedPerTick *= getIngotsInRecipe();
		if(!Arrays.equals(lastRecipeAmts, recipeAmts))
			processProgress = 0;
		lastRecipeAmts = Arrays.copyOf(recipeAmts, recipeAmts.length);
		if(shouldBurn()) {
			processProgress += (float)(getInventoryStackLimit() - getIngotsInRecipe() + 1);
			joules -= joulesUsedPerTick;
			if(processProgress >= ticksToProcess) {
				processProgress = 0;
				byte[] ingotsToRemove = Arrays.copyOf(recipeAmts, recipeAmts.length);
				for(int slot : getSlotsWithIngot()) {
					int ingotNum = getIngotNum(inventoryStacks[slot]);
					int ingots = ingotsToRemove[ingotNum];
					ingotsToRemove[ingotNum] -= Math.min(ingotsToRemove[ingotNum], inventoryStacks[slot].stackSize);
					decrStackSize(slot, Math.min(ingots, inventoryStacks[slot].stackSize));
				}
				ItemStack ingotResult = getIngotResult();
				if(inventoryStacks[2] == null)
					inventoryStacks[2] = ingotResult;
				else if(inventoryStacks[2].getTagCompound().getInteger("alloy") == ingotResult.getTagCompound().getInteger("alloy"))
					inventoryStacks[2].stackSize += ingotResult.stackSize;
				ItemStack result = getIngotResult();
				if(inventoryStacks[2] == null)
					inventoryStacks[2] = result;
				else if(inventoryStacks[2].getTagCompound().getInteger("alloy") == result.getTagCompound().getInteger("alloy"))
					inventoryStacks[2].stackSize += result.stackSize;
				invChanged = true;
			}
		}
		if(invChanged)
			onInventoryChanged();
	}

	private boolean shouldBurn() {
		int typesInRecipe = 0;
		ArrayList<Boolean> sufficientIngots = new ArrayList<Boolean>();
		for(int amt : recipeAmts)
			if(amt > 0)
				typesInRecipe++;
		for(int i = 0; i < getIngotAmts().length; i++)
			sufficientIngots.add(getIngotAmts()[i] >= recipeAmts[i]);
		if(sufficientIngots.contains(false))
			processProgress = 0;
		return typesInRecipe > 1 && !sufficientIngots.contains(false) && joules >= joulesUsedPerTick && (inventoryStacks[2] == null || (inventoryStacks[2].isItemEqual(getIngotResult()) && getInventoryStackLimit() - inventoryStacks[2].stackSize >= 1));
	}

	public int getIngotNum(ItemStack ingot) {
		if(ingot.itemID == InfiniteAlloys.ingot.shiftedIndex && ingot.getItemDamage() < References.metalCount)
			return ingot.getItemDamage();
		return -1;
	}

	/** Return the resulting ingot for the smelted ingots
	 * 
	 * @return The resulting ingot. */
	private ItemStack getIngotResult() {
		int alloy = 0;
		for(int i = 0; i < recipeAmts.length; i++)
			alloy += Math.pow(References.alloyRadix, i) * recipeAmts[i];
		ItemStack result = new ItemStack(InfiniteAlloys.alloyIngot);
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setInteger("alloy", alloy);
		result.setTagCompound(tagCompound);
		result.setItemDamage(getDamageForAlloy(alloy));
		return result;
	}

	public int getDamageForAlloy(int alloy) {
		int[] validAlloys = InfiniteAlloys.instance.worldData.getValidAlloys();
		for(int i = 0; i < validAlloys.length; i++)
			if(alloy == validAlloys[i])
				return i + 1;
		return -1;
	}

	private ArrayList<Integer> getSlotsWithIngot() {
		ArrayList<Integer> slots = new ArrayList<Integer>();
		for(int i = 3; i < 21; i++)
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

	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.SPEED2))
			ticksToProcess = 6400;
		else if(hasUpgrade(TEHelper.SPEED1))
			ticksToProcess = 9600;
		else
			ticksToProcess = 12800;

		if(hasUpgrade(TEHelper.EFFICIENCY2))
			joulesUsedPerTick = 180D;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			joulesUsedPerTick = 270D;
		else
			joulesUsedPerTick = 360D;

		if(hasUpgrade(TEHelper.CAPACITY2))
			stackLimit = 48;
		else if(hasUpgrade(TEHelper.CAPACITY1))
			stackLimit = 64;
		else
			stackLimit = 32;

		canNetwork = hasUpgrade(TEHelper.WIRELESS);

		if(hasUpgrade(TEHelper.ELECCAPACITY2))
			maxJoules = 1000000D;
		else if(hasUpgrade(TEHelper.ELECCAPACITY1))
			maxJoules = 750000D;
		else
			maxJoules = 500000D;

	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(TEHelper.SPEED1);
		validUpgrades.add(TEHelper.SPEED2);
		validUpgrades.add(TEHelper.EFFICIENCY1);
		validUpgrades.add(TEHelper.EFFICIENCY2);
		validUpgrades.add(TEHelper.CAPACITY1);
		validUpgrades.add(TEHelper.CAPACITY2);
		validUpgrades.add(TEHelper.WIRELESS);
		validUpgrades.add(TEHelper.ELECCAPACITY1);
		validUpgrades.add(TEHelper.ELECCAPACITY2);
	}
}
