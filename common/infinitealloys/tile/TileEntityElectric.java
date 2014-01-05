package infinitealloys.tile;

import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** A base, abstract class for Tile Entities that can receive upgrades, use power, and have processes to run. A sub-type of TileEntityMachine. Often referred to
 * as TEEs.
 * 
 * @see TileEntityMachine */
public abstract class TileEntityElectric extends TileEntityMachine {

	/** Base amount of RK this machine produces/consumes per tick while working. Actual RK change is often also dependent on other conditions. For the actual
	 * amount of RK change per tick, see {@link #getRKChange()}. Positive is producing RK and negative is consuming RK */
	protected int baseRKPerTick;

	/** Amount of ticks it takes for this machine to finish one of its processes */
	public int ticksToProcess = 200;

	/** Amount of ticks this machine has been running its process for, when this reaches ticksToFinish it is done */
	private int processProgress;

	/** A multiplier for the time it takes to process, changed with upgrades. NOTE: Less is faster */
	protected float processTimeMult = 1.0F;

	/** A multiplier for the power used, changed with upgrades. NOTE: Less will consume less power, but also generate less */
	protected float rkPerTickMult = 1.0F;

	/** The energy storage unit that this machine supplies power to or receives power from */
	public TEMEnergyStorage energyStorage;

	public TileEntityElectric(int inventoryLength) {
		super(inventoryLength);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		// Under certain conditions, reset the progress of the machine
		if(shouldResetProgress())
			processProgress = 0;

		// If the machine should be processing and enough energy is available, increment the progress by one. If this is the first tick of the process, call
		// startProcess(). If it has reached or exceeded the limit for completion, then finish the process and reset the counter.
		if(shouldProcess() && energyStorage != null && energyStorage.changeRK(getRKChange())) {
			if(processProgress == 0)
				startProcess();
			if(++processProgress >= ticksToProcess) {
				processProgress = 0;
				finishProcess();
				onInventoryChanged();
			}
		}

		// If the energy storage unit that was connected to this no longer exists, make it null
		if(energyStorage != null && worldObj.getBlockTileEntity(energyStorage.xCoord, energyStorage.yCoord, energyStorage.zCoord) == null)
			energyStorage = null;
	}

	/** Should the process tick be increased? Called every tick to determine if energy should be used and if progress should continue. NOTE: This will return
	 * true even if there is not a nearby energy storage unit to support the process */
	public abstract boolean shouldProcess();

	/** Should processProgress be reset to 0? */
	protected boolean shouldResetProgress() {
		return false;
	}

	/** Called on the first tick of a process */
	protected void startProcess() {}

	/** Called when processProgress reaches ticksToProgress */
	protected void finishProcess() {}

	/** Actual amount of RK change per tick, after certain calculations and conditions. Positive is produced RK and negative is consumed RK. */
	public int getRKChange() {
		return (int)(baseRKPerTick * rkPerTickMult / processTimeMult);
	}

	public int getProcessProgress() {
		return processProgress;
	}

	@SideOnly(Side.CLIENT)
	public float getProcessProgressScaled(float scale) {
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

	@Override
	public Object[] getSyncDataToClient() {
		return ArrayUtils.addAll(super.getSyncDataToClient(), processProgress);
	}

	public void handlePacketDataFromServerElectric(int processProgress) {
		this.processProgress = processProgress;
	}
}
