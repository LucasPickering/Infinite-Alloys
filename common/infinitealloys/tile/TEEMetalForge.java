package infinitealloys.tile;

import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;

public class TEEMetalForge extends TileEntityElectric {

	/** An array for the "stack sizes" of each ingot in the recipe setting */
	public byte[] recipeAmts = new byte[Consts.METAL_COUNT];
	/** recipeAmts from last tick, used to tell if the recipe has changed to reset progress */
	private byte[] lastRecipeAmts = new byte[Consts.METAL_COUNT];
	public byte presetSelection = -1;

	public TEEMetalForge(byte front) {
		this();
		this.front = front;
	}

	public TEEMetalForge() {
		super(20);
		baseRKPerTick = -18;
	}

	@Override
	public int getID() {
		return MachineHelper.METAL_FORGE;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		lastRecipeAmts = Arrays.copyOf(recipeAmts, recipeAmts.length);
	}

	@Override
	public boolean shouldProcess() {
		int typesInRecipe = 0;
		for(final int amt : recipeAmts)
			if(amt > 0)
				typesInRecipe++;
		return (inventoryStacks[0] == null || inventoryStacks[0].isItemEqual(getIngotResult()) && inventoryStacks[0].stackSize < getInventoryStackLimit())
				&& typesInRecipe > 1 && hasSufficientIngots();
	}

	@Override
	protected boolean shouldResetProgress() {
		return !hasSufficientIngots() || !Arrays.equals(lastRecipeAmts, recipeAmts);
	}

	@Override
	protected void finishProcess() {
		final byte[] ingotsToRemove = Arrays.copyOf(recipeAmts, recipeAmts.length);
		for(final int slot : getSlotsWithIngot()) {
			final int ingotNum = MachineHelper.getIngotNum(inventoryStacks[slot]);
			final int ingots = ingotsToRemove[ingotNum];
			ingotsToRemove[ingotNum] -= Math.min(ingotsToRemove[ingotNum], inventoryStacks[slot].stackSize);
			decrStackSize(slot, Math.min(ingots, inventoryStacks[slot].stackSize));
		}
		final ItemStack result = getIngotResult();
		if(inventoryStacks[0] == null)
			inventoryStacks[0] = result;
		else if(inventoryStacks[0].getTagCompound().getInteger("alloy") == result.getTagCompound().getInteger("alloy"))
			inventoryStacks[0].stackSize += result.stackSize;
	}

	@Override
	public int getRKChange() {
		return (int)(baseRKPerTick * rkPerTickMult / processTimeMult * getIngotsInRecipe());
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		lastRecipeAmts = recipeAmts = tagCompound.getByteArray("RecipeAmts");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setByteArray("RecipeAmts", recipeAmts);
	}

	@Override
	public Object[] getSyncDataToClient() {
		return ArrayUtils.addAll(super.getSyncDataToClient(), recipeAmts);
	}

	@Override
	public Object[] getSyncDataToServer() {
		return new Object[] { recipeAmts };
	}

	public void handlePacketDataFromClient(byte[] recipeAmts) {
		this.recipeAmts = recipeAmts;
	}

	/** Return the resulting ingot for the smelted ingots
	 * 
	 * @return The resulting ingot. */
	private ItemStack getIngotResult() {
		int alloy = 0;
		for(int i = 0; i < recipeAmts.length; i++)
			alloy += Math.pow(Consts.ALLOY_RADIX, i) * recipeAmts[i];
		alloy = Funcs.reduceAlloy(alloy);
		final ItemStack result = new ItemStack(Items.alloyIngot);
		final NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setInteger("alloy", alloy);
		result.setTagCompound(tagCompound);
		result.setItemDamage(getDamageForAlloy(alloy));
		return result;
	}

	/** Does the inventory of the forge contain enough ingots to fulfill the current recipe? */
	private boolean hasSufficientIngots() {
		for(int i = 0; i < getIngotAmts().length; i++)
			if(getIngotAmts()[i] < recipeAmts[i])
				return false;
		return true;
	}

	public int getDamageForAlloy(int alloy) {
		for(int i = 0; i < Consts.VALID_ALLOY_COUNT; i++)
			if(alloy == Funcs.getValidAlloys()[i])
				return i + 1;
		return -1;
	}

	private ArrayList<Integer> getSlotsWithIngot() {
		final ArrayList<Integer> slots = new ArrayList<Integer>();
		for(int i = 1; i < 19; i++)
			if(inventoryStacks[i] != null)
				slots.add(i);
		return slots;
	}

	private int[] getIngotAmts() {
		final int[] amts = new int[Consts.METAL_COUNT];
		for(final int slot : getSlotsWithIngot())
			amts[MachineHelper.getIngotNum(inventoryStacks[slot])] += inventoryStacks[slot].stackSize;
		return amts;
	}

	private int getIngotsInRecipe() {
		int ingots = 0;
		for(final int amt : recipeAmts)
			ingots += amt;
		return ingots;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(MachineHelper.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(MachineHelper.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(MachineHelper.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(MachineHelper.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 1.0F;

		if(hasUpgrade(MachineHelper.CAPACITY2))
			stackLimit = 64;
		else if(hasUpgrade(MachineHelper.CAPACITY1))
			stackLimit = 48;
		else
			stackLimit = 32;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(MachineHelper.SPEED1);
		validUpgrades.add(MachineHelper.SPEED2);
		validUpgrades.add(MachineHelper.EFFICIENCY1);
		validUpgrades.add(MachineHelper.EFFICIENCY2);
		validUpgrades.add(MachineHelper.CAPACITY1);
		validUpgrades.add(MachineHelper.CAPACITY2);
		validUpgrades.add(MachineHelper.WIRELESS);
	}
}
