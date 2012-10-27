package infinitealloys;

import java.util.ArrayList;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
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
	public ArrayList<Vec3> networkCoords;

	/**
	 * Has been initialized
	 */
	public boolean init;

	public TileEntityComputer(int facing) {
		this();
		orientation = (byte)facing;
	}

	public TileEntityComputer() {
		super();
		inventoryStacks = new ItemStack[1];
		orientation = 2;
		networkCapacity = networkCapacityA[0];
		networkRange = networkRangeA[0];
		networkCoords = new ArrayList<Vec3>(networkCapacity);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		for(int i = 0; i < networkCoords.size(); i++) {
			Vec3 coords = networkCoords.get(i);
			Block block = Block.blocksList[worldObj.getBlockId((int)coords.xCoord, (int)coords.yCoord, (int)coords.zCoord)];
			TileEntity te = worldObj.getBlockTileEntity((int)coords.xCoord, (int)coords.yCoord, (int)coords.zCoord);
			if(!(block instanceof BlockMachine))
				networkCoords.remove(i);
		}
	}

	public boolean addMachine(EntityPlayer player, int machX, int machY, int machZ) {
		for(Vec3 coords : networkCoords)
			if(coords.xCoord == machX && coords.yCoord == machY && coords.zCoord == machZ) {
				if(worldObj.isRemote)
					player.addChatMessage("Error: Machine already in network");
				return false;
			}
		Vec3 vec = Vec3.createVectorHelper(machX, machY, machZ);
		if(machX == xCoord && machY == yCoord && machZ == zCoord) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Cannot add self to network");
		}
		else if(networkCoords.size() >= networkCapacity) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Network full");
		}
		else if(worldObj.getBlockId(machX, machY, machZ) != InfiniteAlloys.machine.blockID) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Can only add machines");
		}
		else if(vec.distanceTo(Vec3.createVectorHelper(xCoord, yCoord, zCoord)) > networkRange) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Machine out of range");
		}
		else {
			networkCoords.add(vec);
			return true;
		}
		return false;
	}

	public void handlePacketDataFromServer() {
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		for(int i = 0; i < tagCompound.getInteger("NetworkSize"); i++) {
			int[] coords = tagCompound.getIntArray("Coords" + i);
			networkCoords.add(Vec3.createVectorHelper(coords[0], coords[1], coords[2]));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("NetworkSize", networkCoords.size());
		for(int i = 0; i < networkCoords.size(); i++) {
			Vec3 vec = networkCoords.get(i);
			tagCompound.setIntArray("Coords" + i, new int[] { (int)vec.xCoord, (int)vec.yCoord, (int)vec.zCoord });
		}
	}

	@Override
	public boolean isUpgradeValid(ItemStack upgrade) {
		return super.isUpgradeValid(upgrade);
	}

	@Override
	public String getInvName() {
		return "Computer";
	}

	@Override
	protected void updateUpgrades() {
	}
}
