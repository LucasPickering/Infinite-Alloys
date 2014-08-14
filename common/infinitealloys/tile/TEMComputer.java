package infinitealloys.tile;

import infinitealloys.item.IAItems;
import infinitealloys.network.MessageNetworkEditToClient;
import infinitealloys.network.MessageNetworkEditToServer;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

public class TEMComputer extends TileEntityMachine implements IHost {

	/** The last point that was checked for a machine in the previous iteration of {@link #search}. The coords are relative to this TE block. */
	private Point lastSearch;

	/** The max range that machines can be added at with the Internet Wand */
	public int range;

	/** The number of machines that this block can host */
	public int networkCapacity;

	/** A list of clients currently connected to this computer control network */
	private final ArrayList<Point> networkClients = new ArrayList<Point>();

	public boolean shouldSearch;

	/** False until the first call of {@link #updateEntity()} */
	private boolean initialized;

	public TEMComputer(byte front) {
		this();
		this.front = front;
	}

	public TEMComputer() {
		super(1);
	}

	@Override
	public EnumMachine getEnumMachine() {
		return EnumMachine.COMPUTER;
	}

	@Override
	public void updateEntity() {
		if(computerHost == null)
			computerHost = coords();

		if(!initialized) {
			initialized = true;
			if(!worldObj.isRemote)
				for(Point client : networkClients)
					((TileEntityMachine)Funcs.getTileEntity(worldObj, client)).connectToComputerNetwork(coords());
		}

		super.updateEntity();
	}

	@Override
	public void onBlockDestroyed() {
		if(computerHost.equals(coords()))
			deleteNetwork();
		super.onBlockDestroyed();
	}

	@Override
	public void connectToComputerNetwork(Point host) {
		deleteNetwork();
		super.connectToComputerNetwork(host);
	}

	@Override
	public void deleteNetwork() {
		for(Point client : networkClients) {
			TileEntity te = Funcs.getTileEntity(worldObj, client);
			if(te instanceof TileEntityMachine)
				((TileEntityMachine)te).disconnectFromComputerNetwork();
		}
	}

	@Override
	public boolean isClientValid(Point client) {
		TileEntity te = Funcs.getTileEntity(worldObj, client);
		return te instanceof TileEntityMachine && ((TileEntityMachine)te).hasUpgrade(Consts.WIRELESS, 1);
	}

	@Override
	public boolean addClient(EntityPlayer player, Point client, boolean sync) {
		if(networkClients.contains(client)) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.error", "/: ", "machine.textOutput.error.alreadyInNetwork")));
		}
		else if(networkClients.size() >= networkCapacity) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.error", "/: ", "machine.textOutput.error.networkFull")));
		}
		else if(client.equals(xCoord, yCoord, zCoord)) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.error", "/: ", "machine.textOutput.error.cannotAddSelf")));
		}
		else if(client.distanceTo(xCoord, yCoord, zCoord) > range) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.error", "/: ", "machine.textOutput.error.outOfRange")));
		}
		else if(!isClientValid(client)) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.error", "/: ", "machine.textOutput.error.notWireless")));
		}
		else {
			networkClients.add(client); // Add the machine

			if(initialized)
				((TileEntityMachine)Funcs.getTileEntity(worldObj, client)).connectToComputerNetwork(coords()); // Tell the client machine to connect

			// Sync the data to the server/all clients
			if(sync) { // If we should sync
				if(worldObj.isRemote) { // If this is the client
					Funcs.sendPacketToServer(new MessageNetworkEditToServer(true, worldObj.provider.dimensionId, coords(), client)); // Sync to server
					if(player != null)
						player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.addingMachine") + client)); // Send a chat message
				}
				else
					Funcs.sendPacketToAllPlayers(new MessageNetworkEditToClient(true, worldObj.provider.dimensionId, coords(), client)); // Sync to clients
			}

			return true;
		}
		return false;
	}

	@Override
	public void removeClient(Point client, boolean sync) {
		networkClients.remove(client);
		if(sync) {
			if(worldObj.isRemote)
				Funcs.sendPacketToServer(new MessageNetworkEditToServer(false, worldObj.provider.dimensionId, coords(), client));
			else
				Funcs.sendPacketToAllPlayers(new MessageNetworkEditToClient(false, worldObj.provider.dimensionId, coords(), client));
		}
	}

	@Override
	public void syncAllClients(EntityPlayer player) {
		for(Point client : networkClients)
			Funcs.sendPacketToPlayer(new MessageNetworkEditToClient(true, worldObj.provider.dimensionId, coords(), client), player);
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
					final TileEntity te = worldObj.getTileEntity(xCoord + x, yCoord + y, zCoord + z);
					if(te instanceof TileEntityMachine && !(te instanceof TEMComputer) && hasUpgrade(Consts.WIRELESS, 1))
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
		int[] capacityUpgradeValues = { 3, 5, 7, 10 };
		networkCapacity = capacityUpgradeValues[getUpgradeTier(Consts.CAPACITY)];

		int[] rangeUpgradeValues = { 30, 40, 50, 60 };
		range = rangeUpgradeValues[getUpgradeTier(Consts.RANGE)];
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgradeTypes.add(IAItems.upgrades[Consts.CAPACITY]);
		validUpgradeTypes.add(IAItems.upgrades[Consts.RANGE]);
	}
}
