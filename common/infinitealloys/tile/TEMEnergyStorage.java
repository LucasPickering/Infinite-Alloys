package infinitealloys.tile;

import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.ArrayUtils;

public class TEMEnergyStorage extends TileEntityHost {

	/** The maximum amount of RK that this machine can store */
	private int maxRK;

	/** The amount of RK currently stored in the unit */
	private int currentRK;

	public TEMEnergyStorage(byte front) {
		this();
		this.front = front;
	}

	public TEMEnergyStorage() {
		super(1);
	}

	@Override
	public int getID() {
		return MachineHelper.ENERGY_STORAGE;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		// If a connected machine no longer exists, remove it from the network
		for(Iterator iterator = connectedMachines.iterator(); iterator.hasNext();) {
			Point p = (Point)iterator.next();
			if(!MachineHelper.isElectric(worldObj, p.x, p.y, p.z))
				iterator.remove();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		currentRK = tagCompound.getInteger("currentRK");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("currentRK", currentRK);
	}

	@Override
	public Object[] getSyncDataToClient() {
		List<Object> coords = new ArrayList<Object>();
		for(Point point : connectedMachines) {
			coords.add(point.x);
			coords.add((short)point.y);
			coords.add(point.z);
		}
		return ArrayUtils.addAll(super.getSyncDataToClient(), currentRK, (byte)connectedMachines.size(), coords.toArray());
	}

	public void handlePacketDataFromServer(int currentRK) {
		this.currentRK = currentRK;
	}

	public boolean addMachine(EntityPlayer player, int machineX, int machineY, int machineZ) {
		if(!super.addMachine(player, machineX, machineY, machineZ))
			return false;
		if(!MachineHelper.isElectric(worldObj, machineX, machineY, machineZ)) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Block is not electrical");
		}
		else {
			TileEntity te = worldObj.getBlockTileEntity(machineX, machineY, machineZ);
			if(((TileEntityElectric)te).energyStorage != null) { // If the machine is already connected to another storage unit, disconnect it from that
				for(Iterator iterator = ((TileEntityElectric)te).energyStorage.connectedMachines.iterator(); iterator.hasNext();) {
					Point p = (Point)iterator.next();
					if(p.equals(machineX, machineY, machineZ)) {
						iterator.remove();
						break;
					}
				}
				((TileEntityElectric)te).energyStorage = null;
			}
			connectedMachines.add(new Point(machineX, machineY, machineZ));
			if(worldObj.isRemote)
				player.addChatMessage("Adding machine at " + machineX + ", " + machineY + ", " + machineZ);
			return true;
		}
		return false;
	}

	/** Will the unit support the specified change in RK, i.e. if changeInRK is added to currentRK, will the result be less than zero or overflow the machine? If
	 * this condition is true, make said change, i.e. actually add changeInRK to currentRK
	 * 
	 * @param changeInRK the specified change in RK
	 * @return True if changeInRK plus currentRK is between 0 and maxRK, False otherwise */
	public boolean changeRK(int changeInRK) {
		if(0 <= currentRK + changeInRK && currentRK + changeInRK <= maxRK) {
			currentRK += changeInRK;
			return true;
		}
		return false;
	}

	public int getCurrentRK() {
		return currentRK;
	}

	public int getCurrentRKScaled(int scale) {
		return currentRK * scale / maxRK;
	}

	public int getMaxRK() {
		return maxRK;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(MachineHelper.CAPACITY2))
			maxRK = 400000000; // 400,000,000 (400 million)
		else if(hasUpgrade(MachineHelper.CAPACITY1))
			maxRK = 200000000; // 200,000,000 (200 million)
		else
			maxRK = 100000000; // 100,000,000 (100 million)

		if(hasUpgrade(MachineHelper.RANGE2))
			range = 60;
		else if(hasUpgrade(MachineHelper.RANGE1))
			range = 45;
		else
			range = 30;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(MachineHelper.CAPACITY1);
		validUpgrades.add(MachineHelper.CAPACITY2);
		validUpgrades.add(MachineHelper.RANGE1);
		validUpgrades.add(MachineHelper.RANGE2);
	}
}
