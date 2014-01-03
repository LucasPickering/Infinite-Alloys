package infinitealloys.tile;

import infinitealloys.block.Blocks;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.ArrayUtils;

public class TEMComputer extends TileEntityMachine implements IHost {

	/** The last point that was checked for a machine in the previous iteration of {@link #search}. The coords are relative to this TE block. */
	private Point lastSearch;

	/** The max range that machines can be added at with the Internet Wand */
	public int range = 0;

	/** The number of machines that this block can host */
	public int networkCapacity = 0;

	/** 3D coords for each machine that is connected to the computer */
	public final List<Point> connectedMachines = new ArrayList<Point>();

	public boolean shouldSearch;

	public TEMComputer(byte front) {
		this();
		this.front = front;
	}

	public TEMComputer() {
		super(1);
	}

	@Override
	public int getID() {
		return MachineHelper.COMPUTER;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		// If a connected machine no longer exists, remove it from the network
		for(Iterator iterator = connectedMachines.iterator(); iterator.hasNext();) {
			Point p = (Point)iterator.next();
			if(!MachineHelper.isWireless(worldObj, p.x, p.y, p.z))
				iterator.remove();
		}
	}

	@Deprecated
	/** Perform a search for machines that can be controlled. This checks {@link infinitealloys.util.MachineHelper#SEARCH_PER_TICK a set amount of} blocks in a
	 * tick, then saves its place and picks up where it left off next tick, which eliminates stutter during searches. */
	private void search() {
		// The amount of blocks that have been iterated over this tick. When this reaches TEHelper.SEARCH_PER_TICK, the loops break
		int blocksSearched = 0;

		// Iterate over each block that is within the given range in all three dimensions. The searched area will be a cube with each side being (2 * range + 1)
		// blocks long.
		for(int x = lastSearch.x; x <= range; x++) {
			for(int y = lastSearch.y; y <= range; y++) {
				for(int z = lastSearch.z; z <= range; z++) {

					// If the block at the given coords (which have been converted to absolute coordinates) is a machine and it is not already connected to an
					// energy storage unit, add it to the power network.
					TileEntity te = worldObj.getBlockTileEntity(xCoord + x, yCoord + y, zCoord + z);
					if(te instanceof TileEntityMachine && !(te instanceof TEMComputer) && hasUpgrade(MachineHelper.WIRELESS))
						connectedMachines.add(new Point(xCoord + x, yCoord + y, zCoord + z));

					// If the amounts of blocks search this tick has reached the limit, save our place and end the function. The search will be
					// continued next tick.
					if(++blocksSearched >= MachineHelper.SEARCH_PER_TICK) {
						lastSearch.set(x, y, z);
						return;
					}
				}
				lastSearch.z = -range; // If we've search all the z values, reset the z position.
			}
			lastSearch.y = -range; // If we've search all the y values, reset the y position.
		}
		lastSearch.x = -range; // If we've search all the x values, reset the x position.

		shouldSearch = false; // The search is done. Stop running the function until another search is initiated.
	}

	@Override
	public boolean addMachine(EntityPlayer player, int machineX, int machineY, int machineZ) {
		for(Point coords : connectedMachines) {
			if(coords.x == machineX && coords.y == machineY && coords.z == machineZ) {
				if(worldObj.isRemote)
					player.addChatMessage("Error: Machine is already in network");
				return false;
			}
		}
		if(connectedMachines.size() >= networkCapacity) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Network full");
		}
		else if(machineX == xCoord && machineY == yCoord && machineZ == zCoord) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Cannot add self to network");
		}
		else if(new Point(machineX, machineY, machineZ).distanceTo(xCoord, yCoord, zCoord) > range) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Block out of range");
		}
		else if(!MachineHelper.isWireless(worldObj, machineX, machineY, machineZ)) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Block is not capable of networking");
		}
		else {
			connectedMachines.add(new Point(machineX, machineY, machineZ));
			if(worldObj.isRemote)
				player.addChatMessage("Adding machine at " + machineX + ", " + machineY + ", " + machineZ);
			return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		for(int i = 0; i < tagCompound.getInteger("NetworkSize"); i++) {
			int[] coords = tagCompound.getIntArray("Coords" + i);
			connectedMachines.add(new Point(coords[0], coords[1], coords[2]));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("NetworkSize", connectedMachines.size());
		for(int i = 0; i < connectedMachines.size(); i++) {
			Point coords = connectedMachines.get(i);
			tagCompound.setIntArray("Coords" + i, new int[] { coords.x, coords.y, coords.z });
		}
	}

	@Override
	public Object[] getSyncDataToClient() {
		List<Object> coords = new ArrayList<Object>();
		for(Point point : connectedMachines) {
			coords.add(point.x);
			coords.add((short)point.y);
			coords.add(point.z);
		}
		return ArrayUtils.addAll(super.getSyncDataToClient(), (byte)connectedMachines.size(), coords.toArray());
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(MachineHelper.CAPACITY2))
			networkCapacity = 10;
		else if(hasUpgrade(MachineHelper.CAPACITY1))
			networkCapacity = 6;
		else
			networkCapacity = 3;

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
		validUpgrades.add(MachineHelper.WIRELESS);
	}
}
