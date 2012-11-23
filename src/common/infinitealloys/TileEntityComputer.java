package infinitealloys;

import java.util.ArrayList;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityComputer extends TileEntityMachine {

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

	public TileEntityComputer(ForgeDirection facing) {
		this();
		front = facing;
	}

	public TileEntityComputer() {
		super();
		inventoryStacks = new ItemStack[1];
		canNetwork = true;
		networkCapacity = 3;
		networkRange = 10;
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
		else if(new Point(machX, machY, machZ).distanceTo(xCoord, yCoord, zCoord) > networkRange) {
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
	protected void updateUpgrades() {
		if(hasUpgrade(CAPACITY2))
			networkCapacity = 10;
		else if(hasUpgrade(CAPACITY1))
			networkCapacity = 6;
		else
			networkCapacity = 3;
		
		if(hasUpgrade(CAPACITY2))
			networkCapacity = 10;
		else if(hasUpgrade(CAPACITY1))
			networkCapacity = 6;
		else
			networkCapacity = 3;
		
		if(hasUpgrade(CAPACITY2))
			networkCapacity = 10;
		else if(hasUpgrade(CAPACITY1))
			networkCapacity = 6;
		else
			networkCapacity = 3;

		if(hasUpgrade(RANGE2))
			networkRange = 20;
		else if(hasUpgrade(RANGE1))
			networkRange = 15;
		else
			networkRange = 10;

		if(hasUpgrade(ELECCAPACITY2))
			maxJoules = 1000000D;
		else if(hasUpgrade(ELECCAPACITY1))
			maxJoules = 750000D;
		else
			maxJoules = 500000D;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(CAPACITY1);
		validUpgrades.add(CAPACITY2);
		validUpgrades.add(RANGE1);
		validUpgrades.add(RANGE2);
	}
}
