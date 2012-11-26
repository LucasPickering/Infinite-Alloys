package infinitealloys;

import infinitealloys.handlers.PacketHandler;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityMetalForge extends TileEntityMachine {

	/** An array for the "stack sizes" of each ingot in the recipe setting */
	public byte[] recipeAmts = new byte[References.metalCount];
	public ArrayList<Integer> recipePresets = new ArrayList<Integer>();
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
		joulesUsedPerTick *= (double)getIngotsInRecipe();
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

	public void updatePresets() {
		if(inventoryStacks[0] != null) {
			for(int alloy : inventoryStacks[0].getTagCompound().getIntArray("savedAlloys"))
				for(int validAlloy : InfiniteAlloys.instance.worldData.getValidAlloys())
					if(alloy == validAlloy)
						recipePresets.add(alloy);
		}
		else
			recipePresets.clear();
		presetSelection = -1;
	}

	protected void updateUpgrades() {
		if(hasUpgrade(SPEED2))
			ticksToProcess = 6400;
		else if(hasUpgrade(SPEED1))
			ticksToProcess = 9600;
		else
			ticksToProcess = 12800;

		if(hasUpgrade(EFFICIENCY2))
			joulesUsedPerTick = 180D;
		else if(hasUpgrade(EFFICIENCY1))
			joulesUsedPerTick = 270D;
		else
			joulesUsedPerTick = 360D;

		if(hasUpgrade(CAPACITY2))
			stackLimit = 48;
		else if(hasUpgrade(CAPACITY1))
			stackLimit = 64;
		else
			stackLimit = 32;

		canNetwork = hasUpgrade(WIRELESS);

		if(hasUpgrade(ELECCAPACITY2))
			maxJoules = 1000000D;
		else if(hasUpgrade(ELECCAPACITY1))
			maxJoules = 750000D;
		else
			maxJoules = 500000D;

	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(SPEED1);
		validUpgrades.add(SPEED2);
		validUpgrades.add(EFFICIENCY1);
		validUpgrades.add(EFFICIENCY2);
		validUpgrades.add(CAPACITY1);
		validUpgrades.add(CAPACITY2);
		validUpgrades.add(WIRELESS);
		validUpgrades.add(ELECCAPACITY1);
		validUpgrades.add(ELECCAPACITY2);
	}
}
