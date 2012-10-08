package infinitealloys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

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
	private int[][] networkMachineInfo;

	/**
	 * Array of IDs selected by the player, set using the "+" and "-" buttons on
	 * the GUI
	 */
	public int[] selectedIDs;

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
		orientation = 2;
	}

	/**
	 * Update the network to add and remove machines
	 */
	public void updateNetwork() {
		for(int i = 0; i < networkMachineInfo.length; i++) {
			int x = networkMachineInfo[i][1];
			int y = networkMachineInfo[i][1];
			int z = networkMachineInfo[i][1];
			if(!isMachine(Block.blocksList[worldObj.getBlockId(x, y, z)], x, y, z))
				Arrays.fill(networkMachineInfo[i], -1);
		}
		if(networkFull()) return;
		for(int x = 0; x < networkRange; x++)
			for(int y = 0; y < networkRange; y++)
				for(int z = 0; z < networkRange; z++)
					for(int i = 0; i < 2; i++)
						for(int j = 0; j < 2; j++)
							for(int k = 0; k < 2; k++) {
								int searchX = xCoord;
								int searchY = xCoord;
								int searchZ = xCoord;
								if(i == 0)
									searchX += x;
								else
									searchX -= x;
								if(j == 0)
									searchY += y;
								else
									searchY -= y;
								if(k == 0)
									searchZ += z;
								else
									searchZ -= z;
								Block block = Block.blocksList[worldObj.getBlockId(searchX, searchY, searchZ)];
								TileEntity te = worldObj.getBlockTileEntity(searchX, searchY, searchZ);
								if(te == null)
									continue;
								int networkID = ((TileEntityMachineInventory)te).networkID;
								if(isMachine(block, x, y, z) && rangeCheck(searchX, searchY, searchZ))
									addMachine(getSpotForId(networkID), networkID, searchX, searchY, searchZ);
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
		if(!init) {
			maxIdCount = maxIdCountA[getBlockMetadata()];
			networkRange = networkRangeA[getBlockMetadata()];
			networkMachineInfo = new int[maxIdCount][4];
			selectedIDs = new int[maxIdCount];
			for(int i = 0; i < networkMachineInfo.length; i++)
				Arrays.fill(networkMachineInfo[i], -1);
			updateNetwork();
			init = true;
		}
		BlockMachine.updateBlockState(worldObj, xCoord, yCoord, zCoord);
	}

	private boolean isMachine(Block block, int x, int y, int z) {
		return block instanceof BlockMachine;
	}

	/**
	 * @return True if no machines can connect, false if there is space on the
	 *         network
	 */
	private boolean networkFull() {
		for(int i = 0; i < networkMachineInfo.length; i++)
			if(networkMachineInfo[i][0] == -1) return false;
		return true;
	}

	private void addMachine(int computerSpot, int id, int x, int y, int z) {
		if(computerSpot == -1) return;
		networkMachineInfo[computerSpot][0] = id;
		networkMachineInfo[computerSpot][1] = x;
		networkMachineInfo[computerSpot][2] = y;
		networkMachineInfo[computerSpot][3] = z;
	}

	/**
	 * Slot IDs are the numbers adjusted with the "+" and "-" buttons on the GUI
	 * 
	 * @param id
	 * @return Slot on the computer with the given id, or -1 if there is none
	 */
	private int getSpotForId(int id) {
		for(int i = 0; i < selectedIDs.length; i++)
			if(selectedIDs[i] == id)
				return i;
		return -1;
	}

	/**
	 * Updates the settings based on the speed, capacity, and efficiency
	 * upgrades.
	 */
	private void updateUpgrades() {
		if((upgrades & 1) == 1)
			maxIdCount = maxIdCountA[1];
		if((upgrades & 2) == 2)
			maxIdCount = maxIdCountA[2];
		if((upgrades & 4) == 4)
			networkRange = networkRangeA[1];
		if((upgrades & 8) == 8)
			networkRange = networkRangeA[1];
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		for(int i = 0; i < maxIdCount; i++)
			selectedIDs[i] = nbttagcompound.getShort("SelectedID" + i);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		for(int i = 0; i < maxIdCount; i++)
			nbttagcompound.setShort("SelectedID" + i, (short)selectedIDs[i]);
	}

	@Override
	public boolean isUpgradeValid(ItemStack upgrade) {
		return false;
	}
}
