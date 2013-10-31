package infinitealloys.tile;

import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityMachine extends TileEntityUpgradable {

	/** Base amount of RK this machine produces/consumes per tick while working. Actual RK change is often also dependent on other conditions. For the actual
	 * amount of RK change per tick, see {@link #getRKChange()}. Positive is producing RK and negative is consuming RK */
	protected int baseRKPerTick;

	/** Amount of ticks it takes for this machine to finish one of its processes */
	public int ticksToProcess = 200;

	/** Amount of ticks this machine has been running its process for, when this reaches ticksToFinish it is done */
	public int processProgress;

	/** The RK storage unit that this machine supplies power to or receives power from */
	public TileEntityRKStorage powerStorageUnit;

	public TileEntityMachine(int upgradeSlotIndex) {
		this();
		this.upgradeSlotIndex = upgradeSlotIndex;
	}

	public TileEntityMachine() {
		populateValidUpgrades();
		updateUpgrades();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		// If the conditions are correct so that the process can continue, increment the progress by one. If it has reached or exceeded the limit for
		// completion, then finish the process and reset the counter.
		if(shouldProcess() && powerStorageUnit.consumeRK(getRKChange())) {
			if(++processProgress >= ticksToProcess) {
				processProgress = 0;
				finishProcessing();
				onInventoryChanged();
			}
		}
	}

	public void handlePacketDataFromServer(int processProgress) {
		this.processProgress = processProgress;
	}

	/** Should the process tick be increased? Called every tick to determine if power should be used and if progress should continue. NOTE: This will return true
	 * even if there is not a nearby power storage unit to support the process */
	protected abstract boolean shouldProcess();

	/** Called when processProgress reaches ticksToProgress */
	protected abstract void finishProcessing();

	/** Actual amount of RK change per tick, after certain calculations and conditions. Positive is produced RK and negative is consumed RK. */
	public int getRKChange() {
		if(shouldProcess())
			return baseRKPerTick;
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public int getProcessProgressScaled(int scale) {
		return processProgress * scale / ticksToProcess;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		processProgress = tagCompound.getInteger("ProcessProgress");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("ProcessProgress", processProgress);
	}
}
