package infinitealloys.tile;

import java.util.ArrayList;
import java.util.List;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;

/** Machines that can host networks between other machines, e.g. the computer */
public abstract class TileEntityHost extends TileEntityMachine {

	/** The max range that machines can be added at with the Internet Wand */
	protected int range;

	/** The number of machines that this block can host */
	protected int networkCapacity;

	/** 3D coords for each machine that is connected to the computer */
	public final List<Point> connectedMachines = new ArrayList<Point>();

	public TileEntityHost(int inventoryLength) {
		super(inventoryLength);
	}

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
		else
			return true;
		return false;
	}
}
