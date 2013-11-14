package infinitealloys.tile;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;
import java.util.Arrays;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TEMAnalyzer extends TileEntityMachine {

	public TEMAnalyzer(int facing) {
		this();
		front = facing;
	}

	public TEMAnalyzer() {
		super(3);
		inventoryStacks = new ItemStack[4];
		stackLimit = 1;
		baseRKPerTick = -36;
		ticksToProcess = 3600;
	}

	@Override
	public int getID() {
		return TEHelper.ANALYZER;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return super.isItemValidForSlot(slot, itemstack) || TEHelper.stackValidForSlot(TEHelper.ANALYZER, slot, itemstack);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(inventoryStacks[0] == null && inventoryStacks[1] != null)
			processProgress = 0;
	}

	@Override
	public boolean shouldProcess() {
		return inventoryStacks[0] != null && inventoryStacks[1] == null;
	}

	@Override
	public void finishProcess() {
		if(InfiniteAlloys.instance.worldData.alloysUnlocked == Math.min(inventoryStacks[0].getItemDamage() - 1, Consts.VALID_ALLOY_COUNT))
			InfiniteAlloys.instance.worldData.alloysUnlocked = inventoryStacks[0].getItemDamage();
		if(inventoryStacks[2] != null) {
			int alloy = inventoryStacks[0].getTagCompound().getInteger("alloy");
			NBTTagCompound tagCompound;

			// Create two arrays for storing the saved alloys. What's in there, and a copy to edit
			int[] oldSave = new int[0];
			int[] newSave;

			// init the compound
			if(inventoryStacks[2].hasTagCompound())
				tagCompound = inventoryStacks[2].getTagCompound();
			else
				tagCompound = new NBTTagCompound();

			// If it has a save, set oldSave to it
			if(tagCompound.hasKey("alloys"))
				oldSave = tagCompound.getIntArray("alloys");

			// Make new save a copy of oldSave with one more spot
			newSave = Arrays.copyOf(oldSave, oldSave.length + 1);

			// Sort newSave so that it can be searched in the next step
			Arrays.sort(newSave);

			// Add the new alloy to newSave if there is room and it is not a repeat then set the compound to newSave
			if(newSave.length < Consts.VALID_ALLOY_COUNT && Arrays.binarySearch(newSave, alloy) < 0) {
				newSave[newSave.length - 1] = alloy;
				tagCompound.setIntArray("alloys", newSave);
				inventoryStacks[2].setTagCompound(tagCompound);
			}
		}
		inventoryStacks[1] = inventoryStacks[0];
		inventoryStacks[0] = null;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.SPEED2))
			processTimeMult = 1800;
		else if(hasUpgrade(TEHelper.SPEED1))
			processTimeMult = 2700;
		else
			processTimeMult = 3600;

		if(hasUpgrade(TEHelper.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 1.0F;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(TEHelper.SPEED1);
		validUpgrades.add(TEHelper.SPEED2);
		validUpgrades.add(TEHelper.EFFICIENCY1);
		validUpgrades.add(TEHelper.EFFICIENCY2);
		validUpgrades.add(TEHelper.WIRELESS);
	}
}
