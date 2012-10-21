package infinitealloys;

import java.util.ArrayList;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class TileEntityComputer extends TileEntityMachine {

	/**
	 * Array of preset values of max connected machines for each tier
	 */
	private static int[] maxIdCountA = { 3, 6, 10 };

	/**
	 * Array of preset values of network range for each tier
	 */
	private static int[] networkRangeA = { 15, 30, 45 };

	/**
	 * That amount of IDs that the computer can connect to
	 */
	public int maxIdCount;

	/**
	 * The range of the computer's wireless communication
	 */
	public int networkRange;

	/**
	 * 2D array, storing ID, x, y, and z for each connected machine
	 */
	public ArrayList<NetworkMachineInfo> networkMachineInfo;

	/**
	 * Array of IDs selected by the player, set using the "+" and "-" buttons on
	 * the GUI
	 */
	public byte[] selectedIDs;

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
		if(networkMachineInfo.size() != maxIdCount) {
			for(int x = 0; x < networkRange; x++)
				for(int y = 0; y < networkRange; y++)
					for(int z = 0; z < networkRange; z++)
						for(int i = 0; i < 2; i++)
							for(int j = 0; j < 2; j++)
								for(int k = 0; k < 2; k++) {
									int searchX = xCoord + i == 0 ? x : -x;
									int searchY = yCoord + j == 0 ? y : -y;
									int searchZ = zCoord + k == 0 ? z : -z;
									Block block = Block.blocksList[worldObj.getBlockId(searchX, searchY, searchZ)];
									TileEntity te = worldObj.getBlockTileEntity(searchX, searchY, searchZ);
									if(te == null)
										continue;
									byte networkID = ((TileEntityMachine)te).networkID;
									if(block instanceof BlockMachine && rangeCheck(searchX, searchY, searchZ) && getSpotForId(networkID) != -1)
										networkMachineInfo.add(getSpotForId(networkID), new NetworkMachineInfo(networkID, searchX, searchY, searchZ));
								}
		}
		for(int i = 0; i < networkMachineInfo.size(); i++) {
			NetworkMachineInfo info = networkMachineInfo.get(i);
			Block block = Block.blocksList[worldObj.getBlockId(info.x, info.y, info.z)];
			TileEntity te = worldObj.getBlockTileEntity(info.x, info.y, info.z);
			if(!(block instanceof BlockMachine) || info.id == ((TileEntityMachine)te).networkID)
				networkMachineInfo.remove(i);
		}
	}

	/**
	 * Return true if the machine is in range of the computer, false otherwise
	 * 
	 * @param machX
	 * @param machY
	 * @param machZ
	 * @return
	 */
	private boolean rangeCheck(int machX, int machY, int machZ) {
		int distance = 0;
		distance += Math.abs(xCoord - machX);
		distance += Math.abs(yCoord - machY);
		distance += Math.abs(zCoord - machZ);
		return distance <= networkRange;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!init) {
			maxIdCount = maxIdCountA[2];
			networkRange = networkRangeA[0];
			networkMachineInfo = new ArrayList<NetworkMachineInfo>(maxIdCount);
			selectedIDs = new byte[maxIdCount];
			init = true;
		}
		updateNetwork();
	}

	/**
	 * Slot IDs are the numbers adjusted with the "+" and "-" buttons on the GUI
	 * 
	 * @param id
	 * @return Slot on the computer with the given id, or -1 if there is none
	 */
	private int getSpotForId(byte id) {
		for(int i = 0; i < selectedIDs.length; i++)
			if(selectedIDs[i] == id)
				return i;
		return -1;
	}

	public void handlePacketDataFromClient(byte[] ids) {
		selectedIDs = ids;
	}

	public void handlePacketDataFromServer(byte[] ids) {
		selectedIDs = ids;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		for(int i = 0; i < maxIdCount; i++)
			selectedIDs[i] = nbttagcompound.getByte("SelectedID" + i);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		for(int i = 0; i < maxIdCount; i++)
			nbttagcompound.setByte("SelectedID" + i, selectedIDs[i]);
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
