package infinitealloys;

import java.util.ArrayList;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3;

public class TileEntityComputer extends TileEntityMachine {

	/**
	 * Array of preset values of max connected machines for each tier
	 */
	private static int[] maxIdCountA = { 3, 6, 10 };

	/**
	 * Array of preset values of network range for each tier
	 */
	private static int[] networkRangeA = { 5, 10, 15 };

	/**
	 * That amount of IDs that the computer can connect to
	 */
	public int maxConnectionAmt;

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
	}

	/**
	 * Update the network to add and remove machines
	 */
	public void updateNetwork() {
		for(int i = 0; i < networkCoords.size(); i++) {
			Vec3 coords = networkCoords.get(i);
			Block block = Block.blocksList[worldObj.getBlockId((int)coords.xCoord, (int)coords.yCoord, (int)coords.zCoord)];
			TileEntity te = worldObj.getBlockTileEntity((int)coords.xCoord, (int)coords.yCoord, (int)coords.zCoord);
			if(!(block instanceof BlockMachine))
				networkCoords.remove(i);
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!init) {
			maxConnectionAmt = maxIdCountA[0];
			networkRange = networkRangeA[0];
			networkCoords = new ArrayList<Vec3>(maxConnectionAmt);
			init = true;
		}
	}

	public void addMachine(int machX, int machY, int machZ) {
		Vec3 vec = Vec3.createVectorHelper(machX, machY, machZ);
		boolean isMachine = worldObj.getBlockId(machX, machY, machZ) == InfiniteAlloys.machine.blockID;
		boolean inRange = vec.distanceTo(Vec3.createVectorHelper(xCoord, yCoord, zCoord)) <= networkRange;
		if(networkCoords.size() < maxConnectionAmt && isMachine && inRange)
			networkCoords.add(vec);
	}

	public void handlePacketDataFromServer() {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
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
