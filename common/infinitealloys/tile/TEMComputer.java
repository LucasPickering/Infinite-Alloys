package infinitealloys.tile;

import infinitealloys.network.PacketClient;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TEMComputer extends TileEntityMachine implements IHost {

	/** The last point that was checked for a machine in the previous iteration of {@link #search}. The coords are relative to this TE block. */
	private Point lastSearch;

	/** The max range that machines can be added at with the Internet Wand */
	public int range = 0;

	/** The number of machines that this block can host */
	public int networkCapacity = 0;

	/** A list of clients currently connected to this computer control network */
	private ArrayList<Point> networkClients = new ArrayList<Point>();

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
	public void deleteNetwork() {
		for(Point client : networkClients)
			removeClient(client, true);
	}

	@Override
	public boolean isClientValid(Point client) {
		TileEntity te = Funcs.getBlockTileEntity(worldObj, client);
		return te instanceof TileEntityMachine && ((TileEntityMachine)te).hasUpgrade(EnumUpgrade.WIRELESS);
	}

	@Override
	public boolean addClient(EntityPlayer player, Point client, boolean sync) {
		if(networkClients.contains(client)) {
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Error: Machine is already in this network");
		}
		else if(networkClients.size() >= networkCapacity) {
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
			// Add the machine
			networkClients.add(client);

			// Sync the data to the server/all clients
			if(worldObj.isRemote) {
				if(player != null)
					player.addChatMessage("Adding machine at " + client);
				if(sync)
					PacketDispatcher.sendPacketToServer(PacketClient.getPacket(true, worldObj.provider.dimensionId, coords(), client));
			}
			else if(sync)
				PacketDispatcher.sendPacketToAllInDimension(PacketClient.getPacket(true, worldObj.provider.dimensionId, coords(), client), worldObj.provider.dimensionId);

			return true;
		}
		return false;
	}

	@Override
	public void removeClient(Point client, boolean sync) {
		networkClients.remove(client);
		if(sync) {
			if(worldObj.isRemote)
				PacketDispatcher.sendPacketToServer(PacketClient.getPacket(false, worldObj.provider.dimensionId, coords(), client));
			else
				PacketDispatcher.sendPacketToAllInDimension(PacketClient.getPacket(false, worldObj.provider.dimensionId, coords(), client), worldObj.provider.dimensionId);
		}
	}

	@Override
	public void syncAllClients(Player player) {
		for(Point client : networkClients)
			PacketDispatcher.sendPacketToPlayer(PacketClient.getPacket(true, worldObj.provider.dimensionId, coords(), client), player);
	}

	@Override
	public int getNetworkSize() {
		return networkClients.size();
	}

	public Point[] getClients() {
		return networkClients.toArray(new Point[] {});
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		for(int i = 0; tagCompound.hasKey("client" + i); i++) {
			int[] client = tagCompound.getIntArray("client" + i);
			networkClients.add(new Point(client[0], client[1], client[2]));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		for(int i = 0; i < networkClients.size(); i++) {
			Point client = networkClients.get(i);
			tagCompound.setIntArray("client" + i, new int[] { client.x, client.y, client.z });
		}
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
					if(te instanceof TileEntityMachine && !(te instanceof TEMComputer) && hasUpgrade(EnumUpgrade.WIRELESS))
						addClient(null, new Point(xCoord + x, yCoord + y, zCoord + z), true);

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
	protected void updateUpgrades() {
		if(hasUpgrade(EnumUpgrade.CAPACITY2))
			networkCapacity = 10;
		else if(hasUpgrade(EnumUpgrade.CAPACITY1))
			networkCapacity = 6;
		else
			networkCapacity = 3;

		if(hasUpgrade(EnumUpgrade.RANGE2))
			range = 60;
		else if(hasUpgrade(EnumUpgrade.RANGE1))
			range = 45;
		else
			range = 30;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(EnumUpgrade.CAPACITY1);
		validUpgrades.add(EnumUpgrade.CAPACITY2);
		validUpgrades.add(EnumUpgrade.RANGE1);
		validUpgrades.add(EnumUpgrade.RANGE2);
	}
}
