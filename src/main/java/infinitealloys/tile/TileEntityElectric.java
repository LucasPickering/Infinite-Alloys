package infinitealloys.tile;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point3;
import io.netty.buffer.ByteBuf;

/**
 * A base class for Tile Entities that can receive upgrades, use power, and run processes. A
 * sub-type of TileEntityMachine. Often referred to as TEEs.
 *
 * @see {@link TileEntityMachine}
 */
public abstract class TileEntityElectric extends TileEntityMachine {

  /**
   * Base amount of RK this machine produces/consumes per tick while working. Actual RK change is
   * often also dependent on other conditions. For the actual amount of RK change per tick, see
   * {@link #getRKChange()}. Positive is producing RK and negative is consuming RK
   */
  protected int baseRKPerTick;

  /**
   * Amount of ticks it takes for this machine to finish one of its processes
   */
  public int ticksToProcess = 200;

  /**
   * Amount of ticks this machine has been running its process for, when this reaches ticksToFinish
   * it is done
   */
  private float processProgress;

  /**
   * A multiplier for the time it takes to process, changed with upgrades. Greater is faster
   */
  protected float processSpeedMult = 1F;

  /**
   * A multiplier for the power used, changed with upgrades. Less will consume less power,
   * but also generate less power in the case of the ESU.
   */
  protected float rkPerTickMult = 1F;

  /**
   * The coordinates of the {@link infinitealloys.tile.TEEEnergyStorage ESU} that is providing
   * energy to this machine.
   */
  public Point3 energyHost;

  public TileEntityElectric(int inventoryLength) {
    super(inventoryLength);
  }

  @Override
  public void updateEntity() {
    super.updateEntity();

    // Under certain conditions, reset the progress of the machine
    if (shouldResetProgress()) {
      processProgress = 0;
    }

    if (energyHost != null) {
      // If the host has been destroyed, reset it
      if (!(Funcs.getTileEntity(worldObj, energyHost) instanceof TEEEnergyStorage)) {
        energyHost = null;
      }

      // If the machine should be processing and enough energy is available, increment the progress
      // by one. If this is the first tick of the process, call startProcess(). If it has reached
      // or exceeded the limit for completion, then finish the process and reset the counter.
      else if (shouldProcess() && ((TEEEnergyStorage) Funcs.getTileEntity(worldObj, energyHost))
          .changeRK(getRKChange())) {
        if (processProgress == 0) {
          onStartProcess();
        }
        processProgress += processSpeedMult;
        if (processProgress >= ticksToProcess) {
          processProgress = 0;
          onFinishProcess();
        }
      }
    }
  }

  @Override
  public void onBlockDestroyed() {
    super.onBlockDestroyed();
    if (energyHost != null) {
      ((IHost) Funcs.getTileEntity(worldObj, energyHost)).removeClient(coords(), true);
    }
  }

  public void connectToEnergyNetwork(Point3 host) {
    if (energyHost != null) {
      ((TEEEnergyStorage) worldObj.getTileEntity(energyHost.x, energyHost.y, energyHost.z))
          .removeClient(coords(), false);
    }
    energyHost = host;
  }

  public void disconnectFromEnergyNetwork() {
    energyHost = null;
    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

  /**
   * Should the process tick be increased? Called every tick to determine if energy should be used
   * and if progress should continue. NOTE: This will return true even if there is not a nearby
   * energy storage unit to support the process
   */
  public abstract boolean shouldProcess();

  /**
   * Should processProgress be reset to 0?
   */
  protected boolean shouldResetProgress() {
    return false;
  }

  /**
   * Called on the first tick of the process.
   */
  protected void onStartProcess() {
  }

  /**
   * Called on the last tick of the process.
   */
  protected void onFinishProcess() {
  }

  /**
   * Actual amount of RK change per tick, after certain calculations and conditions. Positive is
   * produced RK and negative is consumed RK.
   */
  public int getRKChange() {
    return (int) (baseRKPerTick * rkPerTickMult * processSpeedMult);
  }

  public float getProcessProgress() {
    return processProgress;
  }

  @SideOnly(Side.CLIENT)
  public float getProcessProgressScaled(float scale) {
    return processProgress * scale / ticksToProcess;
  }

  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    processProgress = tagCompound.getFloat("processProgress");
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    tagCompound.setFloat("processProgress", processProgress);
  }

  @Override
  public void readToClientData(ByteBuf bytes) {
    super.readToClientData(bytes);
    processProgress = bytes.readFloat();
  }

  @Override
  public void writeToClientData(ByteBuf bytes) {
    super.writeToClientData(bytes);
    bytes.writeFloat(processProgress);
  }
}
