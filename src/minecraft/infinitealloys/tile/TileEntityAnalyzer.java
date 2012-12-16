package infinitealloys.tile;

import infinitealloys.InfiniteAlloys;
import infinitealloys.References;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityAnalyzer extends TileEntityMachine {

	public TileEntityAnalyzer(ForgeDirection facing) {
		this();
		front = facing;
	}

	public TileEntityAnalyzer() {
		super(3);
		inventoryStacks = new ItemStack[4];
		stackLimit = 1;
	}

	@Override
	public String getInvName() {
		return "Analyzer";
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {
		if(side == ForgeDirection.UP)
			return 0;
		if(side == ForgeDirection.DOWN)
			return 2;
		return 1;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		return 1;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		boolean invChanged = false;
		if(inventoryStacks[0] != null && inventoryStacks[1] == null) {
			if(joules >= joulesUsedPerTick) {
				joules -= joulesUsedPerTick;
				processProgress++;
				if(processProgress >= ticksToProcess) {
					if(InfiniteAlloys.instance.worldData.alloysUnlocked == Math.min(inventoryStacks[0].getItemDamage() - 1, References.validAlloyCount))
						InfiniteAlloys.instance.worldData.alloysUnlocked = inventoryStacks[0].getItemDamage();
					processProgress = 0;
					if(inventoryStacks[2] != null) {
						int alloy = inventoryStacks[0].getTagCompound().getInteger("alloy");
						NBTTagCompound tagCompound;

						// Create two arrays for storing the saved alloys.
						// What's in there, and a copy to edit
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
						if(newSave.length < References.validAlloyCount && Arrays.binarySearch(newSave, alloy) < 0) {
							newSave[newSave.length - 1] = alloy;
							tagCompound.setIntArray("alloys", newSave);
							inventoryStacks[2].setTagCompound(tagCompound);
						}
					}
					inventoryStacks[1] = inventoryStacks[0];
					inventoryStacks[0] = null;
					invChanged = true;
				}
			}
		}
		else
			processProgress = 0;
		if(invChanged)
			onInventoryChanged();
	}

	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.SPEED2))
			ticksToProcess = 1800;
		else if(hasUpgrade(TEHelper.SPEED1))
			ticksToProcess = 2700;
		else
			ticksToProcess = 36;

		if(hasUpgrade(TEHelper.EFFICIENCY2))
			joulesUsedPerTick = 180D;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			joulesUsedPerTick = 270D;
		else
			joulesUsedPerTick = 360D;

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
		validUpgrades.add(TEHelper.WIRELESS);
		validUpgrades.add(TEHelper.ELECCAPACITY1);
		validUpgrades.add(TEHelper.ELECCAPACITY2);
	}
}
