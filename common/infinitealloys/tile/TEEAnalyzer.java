package infinitealloys.tile;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;
import infinitealloys.util.MachineHelper;
import java.util.Arrays;
import net.minecraft.nbt.NBTTagCompound;

public class TEEAnalyzer extends TileEntityElectric {

	/** A boolean for each metal telling whether or not an ingot of that metal is required to being searching for the next alloy */
	private boolean[] requiredMetals = new boolean[Consts.METAL_COUNT];

	public TEEAnalyzer(int facing) {
		this();
		front = facing;
	}

	public TEEAnalyzer() {
		super(10);
		stackLimit = 1;
		baseRKPerTick = -36;
		ticksToProcess = 3600;
	}

	@Override
	public int getID() {
		return MachineHelper.ANALYZER;
	}

	@Override
	protected boolean shouldProcess() {
		if(getProcessProgress() > 0)
			return true;
		for(int i = 0; i < requiredMetals.length; i++)
			if(inventoryStacks[i] == null)
				return false;
		return true;
	}

	@Override
	protected void finishProcess() {
		InfiniteAlloys.instance.worldData.incrUnlockedAlloyCount();

		// If an alloy book is present, save the newly-discovered alloy to it
		if(inventoryStacks[8] != null) {
			int alloy = inventoryStacks[0].getTagCompound().getInteger("alloy");
			NBTTagCompound tagCompound;

			// Create two arrays for storing the saved alloys. What's in there, and a copy to edit
			int[] oldSave = new int[0];
			int[] newSave;

			// init the compound
			if(inventoryStacks[8].hasTagCompound())
				tagCompound = inventoryStacks[8].getTagCompound();
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
				inventoryStacks[8].setTagCompound(tagCompound);
			}
		}
		inventoryStacks[1] = inventoryStacks[0];
		inventoryStacks[0] = null;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(MachineHelper.SPEED2))
			processTimeMult = 1800;
		else if(hasUpgrade(MachineHelper.SPEED1))
			processTimeMult = 2700;
		else
			processTimeMult = 3600;

		if(hasUpgrade(MachineHelper.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(MachineHelper.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 1.0F;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(MachineHelper.SPEED1);
		validUpgrades.add(MachineHelper.SPEED2);
		validUpgrades.add(MachineHelper.EFFICIENCY1);
		validUpgrades.add(MachineHelper.EFFICIENCY2);
		validUpgrades.add(MachineHelper.WIRELESS);
	}
}
