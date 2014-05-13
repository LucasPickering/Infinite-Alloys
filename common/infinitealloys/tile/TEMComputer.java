package infinitealloys.tile;

import infinitealloys.util.MachineHelper;
import infinitealloys.util.NetworkManager;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TEMComputer extends TileEntityMachine implements IHost {

	/** The last point that was checked for a machine in the previous iteration of {@link #search}. The coords are relative to this TE block. */
	private Point lastSearch;

	/** The max range that machines can be added at with the Internet Wand */
	public int range = 0;

	/** The number of machines that this block can host */
	public int networkCapacity = 0;

	/** The wireless network that this block is hosting */
	private int computerNetworkID = -1;

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

		if(worldObj.isRemote && computerNetworkID == -1)
			computerNetworkID = NetworkManager.buildNetwork(MachineHelper.COMPUTER_NETWORK, worldObj, new Point(xCoord, yCoord, zCoord));
	}

	@Override
	public void connectToNetwork(int networkType, int networkID) {}

	@Override
	public void disconnectFromNetwork(int networkType) {}

	@Override
	public void deleteNetworks() {
		NetworkManager.deleteNetwork(computerNetworkID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void createNetwork(int networkID, int type) {
		if(type == MachineHelper.COMPUTER_NETWORK)
			computerNetworkID = networkID;
	}

	@SideOnly(Side.CLIENT)
	public int getComputerNetworkID() {
		return computerNetworkID;
	}

	@SuppressWarnings("unused")
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
					final TileEntity te = worldObj.getBlockTileEntity(xCoord + x, yCoord + y, zCoord + z);
					if(te instanceof TileEntityMachine && !(te instanceof TEMComputer) && hasUpgrade(MachineHelper.WIRELESS))
						addClient(null, new Point(xCoord + x, yCoord + y, zCoord + z));

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
	public boolean isClientValid(Point client) {
		return worldObj.getBlockTileEntity(client.x, client.y, client.z) instanceof TileEntityMachine &&
				((TileEntityMachine)worldObj.getBlockTileEntity(client.x, client.y, client.z)).hasUpgrade(MachineHelper.WIRELESS);
	}

	@Override
	public boolean addClient(EntityPlayer player, Point client) {
		if(NetworkManager.hasClient(computerNetworkID, client)) {
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Error: Machine is already in this network");
		}
		else if(NetworkManager.getSize(computerNetworkID) >= networkCapacity) {
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Error: Network full");
		}
		else if(client.equals(xCoord, yCoord, zCoord)) {
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Error: Cannot add self to network");
		}
		else if(client.distanceTo(xCoord, yCoord, zCoord) > range) {
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Error: Block out of range");
		}
		else if(!isClientValid(client)) {
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Error: Block is not capable of networking");
		}
		else {
			NetworkManager.addClient(computerNetworkID, client);
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Adding machine at " + client.x + ", " + client.y + ", " + client.z);
			return true;
		}
		return false;
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
