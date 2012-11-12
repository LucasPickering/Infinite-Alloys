package infinitealloys;

import java.util.ArrayList;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Vec3;

public class TileEntityComputer extends TileEntityMachine {

	/**
	 * Array of preset values of max connected machines for each tier
	 */
	private static int[] networkCapacityA = { 3, 6, 10 };

	/**
	 * Array of preset values of network range for each tier
	 */
	private static int[] networkRangeA = { 5, 10, 15 };

	/**
	 * That amount of machines that the computer can control
	 */
	public int networkCapacity;

	/**
	 * The range of the computer's wireless communication
	 */
	public int networkRange;

	/**
	 * 3D coords for each machine
	 */
	public ArrayList<Point> networkCoords;

	public TileEntityComputer(byte facing) {
		this();
		orientation = facing;
	}

	public TileEntityComputer() {
		super();
		inventoryStacks = new ItemStack[1];
		canNetwork = true;
		networkCapacity = networkCapacityA[2];
		networkRange = networkRangeA[2];
		networkCoords = new ArrayList<Point>(networkCapacity);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		for(int i = 0; i < networkCoords.size(); i++) {
			Point coords = networkCoords.get(i);
			Block block = Block.blocksList[worldObj.getBlockId((int)coords.x, (int)coords.y, (int)coords.z)];
			if(!(block instanceof BlockMachine))
				networkCoords.remove(i);
		}
	}

	public boolean addMachine(EntityPlayer player, int machX, int machY, int machZ) {
		for(Point coords : networkCoords) {
			if(coords.x == machX && coords.y == machY && coords.z == machZ) {
				if(worldObj.isRemote)
					player.addChatMessage("Error: Machine already in network");
				return false;
			}
		}
		if(networkCoords.size() >= networkCapacity) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Network full");
		}
		else if(machX == xCoord && machY == yCoord && machZ == zCoord) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Cannot add self to network");
		}
		else if(Vec3.createVectorHelper(machX, machY, machZ).distanceTo(Vec3.createVectorHelper(xCoord, yCoord, zCoord)) > networkRange) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Machine out of range");
		}
		else if(worldObj.getBlockId(machX, machY, machZ) != InfiniteAlloys.machine.blockID) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Can only add machines");
		}
		else if(!((TileEntityMachine)worldObj.getBlockTileEntity(machX, machY, machZ)).canNetwork) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Machine not capable of networking");
		}
		else {
			networkCoords.add(new Point(machX, machY, machZ));
			return true;
		}
		return false;
	}

	public void handlePacketDataFromServer(ArrayList networkCoords) {
		this.networkCoords = networkCoords;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		for(int i = 0; i < tagCompound.getInteger("NetworkSize"); i++) {
			int[] coords = tagCompound.getIntArray("Coords" + i);
			networkCoords.add(new Point(coords[0], coords[1], coords[2]));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("NetworkSize", networkCoords.size());
		for(int i = 0; i < networkCoords.size(); i++) {
			Point coords = networkCoords.get(i);
			tagCompound.setIntArray("Coords" + i, new int[] { (int)coords.x, (int)coords.y, (int)coords.z });
		}
	}

	@Override
	public String getInvName() {
		return "Computer";
	}

	@Override
	protected void updateUpgrades() {}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(RANGE1);
		validUpgrades.add(RANGE2);
	}
}
