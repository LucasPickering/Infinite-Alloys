package infinitealloys.core;

import infinitealloys.network.PacketAddClient;
import infinitealloys.network.PacketCreateNetwork;
import infinitealloys.network.PacketRemoveClient;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/** This is meant for in-game IA networks, not real computer networks. It organizes and manages the connections between hosts, such as an ESU, and their
 * clients. */
public class NetworkManager {

	private static ArrayList<Network> networks = new ArrayList<Network>();

	/** A map of networks that have not yet been synced to certain clients, with those client's names as strings. This is populated when a client joins and
	 * should quickly empty out. */
	private static Map<Integer, String> networksToBeCreated = new HashMap<Integer, String>();

	/** Read network data from the tag compound and add these networks to the list
	 * 
	 * @param nbtTagCompound the tag compound containing the network data
	 * @param world the world that is being loaded, used for {@link #notifyNetwork} */
	public static void loadData(NBTTagCompound nbtTagCompound, World world) {
		if(Funcs.isServer()) {
			for(int i = 0; nbtTagCompound.hasKey("network" + i); i++) {
				NBTTagCompound network = nbtTagCompound.getCompoundTag("network" + i);

				byte type = network.getByte("type");
				int dimensionID = network.getInteger("dimensionID");
				int[] host = network.getIntArray("host");
				int networkID = buildNetwork(type, dimensionID, new Point(host[0], host[1], host[2]), false);

				for(int j = 0; network.hasKey("client" + j); j++) {
					int[] client = network.getIntArray("client" + j);
					addClient(networkID, new Point(client[0], client[1], client[2]));
				}

				notifyForConnect(networkID, world);
			}
		}
	}

	public static void saveData(NBTTagCompound nbtTagCompound) {
		if(Funcs.isServer()) {
			for(int i = 0; i < networks.size(); i++) {
				NBTTagCompound networkNBT = new NBTTagCompound();
				Network network = networks.get(i);

				networkNBT.setByte("type", network.type);
				networkNBT.setInteger("dimensionID", network.dimensionID);
				networkNBT.setIntArray("host", new int[] { network.host.x, network.host.y, network.host.z });

				for(int j = 0; j < network.clients.size(); j++) {
					Point client = network.clients.get(j);
					networkNBT.setIntArray("client" + j, new int[] { client.x, client.y, client.z });
				}
				nbtTagCompound.setCompoundTag("network" + i, networkNBT);
			}
		}
	}

	/** Create a new network for this host
	 * 
	 * @param notifyClients If true, a packet will be sent to all clients to create this network. This is true except for when the world is being loaded. */
	public static int buildNetwork(byte type, int dimensionID, Point host, boolean notifyClients) {
		if(Funcs.isClient())
			return -1;

		networks.add(new Network(type, dimensionID, host));
		int networkID = networks.size() - 1;

		System.out.println("Creating new network with index " + networkID);

		if(notifyClients)
			PacketDispatcher.sendPacketToAllPlayers(PacketCreateNetwork.getPacket(networkID, type, dimensionID, host));

		return networkID;
	}

	/** Check if there are any players that need a certain network to be synced because they just joined */
	public static void clientNotifyCheck(int networkID) {
		if(Funcs.isServer() && networksToBeCreated.containsKey(networkID)) {
			Player player = Funcs.getPlayerForUsername(networksToBeCreated.get(networkID));
			PacketDispatcher.sendPacketToPlayer(PacketCreateNetwork.getPacket(networkID, getType(networkID), getDimensionID(networkID), getHost(networkID)), player);
			for(Point client : getClients(networkID))
				PacketDispatcher.sendPacketToPlayer(PacketAddClient.getPacket(networkID, client), player);
			networksToBeCreated.remove(networkID);
		}
	}

	/** Create a new network in a certain position, only used on client to sync with server */
	public static void createNetwork(int networkID, byte type, int dimensionID, Point host) {
		if(Funcs.isClient()) {
			networks.set(networkID, new Network(type, dimensionID, host));
			notifyForConnect(networkID, Minecraft.getMinecraft().theWorld);
		}
	}

	public static void deleteNetwork(int networkID) {
		// Disconnect the network and each one above it in the list because the ones above will drop down to a new ID
		for(int i = networkID; i < networks.size(); i++)
			notifyForDisconnect(networkID, getWorld(networkID));

		// Remove this network
		networks.remove(networkID);

		// Re-connect all the networks that were above the deleted one with a new ID that is one lower than before
		for(int i = networkID; i < networks.size(); i++)
			notifyForConnect(i, getWorld(networkID));
	}

	/** Add a client to a network
	 * 
	 * @param networkID the ID of the network in question
	 * @param client the coordinates of the block that is being added as a client */
	public static void addClient(int networkID, Point client) {
		Network network = networks.get(networkID);
		network.clients.add(client);
		((TileEntityMachine)Funcs.getBlockTileEntity(getWorld(networkID), client)).connectToNetwork(network.type, networkID);
		if(Funcs.isClient())
			PacketDispatcher.sendPacketToServer(PacketAddClient.getPacket(networkID, client));
		else
			PacketDispatcher.sendPacketToAllPlayers(PacketAddClient.getPacket(networkID, client));
	}

	/** Remove a client from a network
	 * 
	 * @param networkID the ID of the network in question
	 * @param client the coordinates of the block that is being removed */
	public static void removeClient(int networkID, Point client) {
		if(Funcs.isServer()) {
			networks.get(networkID).clients.remove(client);
			((TileEntityMachine)Funcs.getBlockTileEntity(getWorld(networkID), client)).disconnectFromNetwork(getType(networkID));
			PacketDispatcher.sendPacketToAllPlayers(PacketRemoveClient.getPacket(networkID, client));
		}
	}

	/** Check if a machine is in a specific network */
	public static boolean hasClient(int networkID, Point client) {
		return networks.get(networkID).clients.contains(client);
	}

	/** Get the coordinates of the host block of the network */
	public static Point getHost(int networkID) {
		return networks.get(networkID).host;
	}

	/** Get an array of the clients on a specific network */
	public static Point[] getClients(int networkID) {
		return networks.get(networkID).clients.toArray(new Point[networks.get(networkID).clients.size()]);
	}

	/** Get the amount of clients on a specific network */
	public static int getSize(int networkID) {
		return networks.get(networkID).clients.size();
	}

	/** Get the number that corresponds to the network's type. See {@link infinitealloys.util.MachineHelper MachineHelper} for the IDs */
	private static byte getType(int networkID) {
		return networks.get(networkID).type;
	}

	/** Get the ID of the dimension that the network is in */
	private static int getDimensionID(int networkID) {
		return networks.get(networkID).dimensionID;
	}

	/** Get an instance of World that corresponds to the world that the network is in.
	 * On the server, this gets the world by dimensionID. On the client, it gets it from the Minecraft instance. */
	private static World getWorld(int networkID) {
		if(Funcs.isServer())
			return DimensionManager.getWorld(networks.get(networkID).dimensionID);
		return Minecraft.getMinecraft().theWorld;
	}

	/** Sends packets to a certain client to completely sync all network data */
	public static void syncAllNetworks(String playerName) {
		for(int id = 0; id < networks.size(); id++)
			networksToBeCreated.put(id, playerName);
	}

	/** Delete all networks from the list. Only called when the world is unloaded. */
	public static void clearNetworks() {
		networks.clear();
	}

	/** Tell the host and each client in the network to connect to this network */
	private static void notifyForConnect(int networkID, World world) {
		((TileEntityMachine)Funcs.getBlockTileEntity(world, getHost(networkID))).connectToNetwork(getType(networkID), networkID);

		for(Point client : getClients(networkID))
			((TileEntityMachine)Funcs.getBlockTileEntity(world, client)).connectToNetwork(getType(networkID), networkID);
	}

	/** Tell the host and each client in the network to disconnect from this network */
	private static void notifyForDisconnect(int networkID, World world) {
		((TileEntityMachine)Funcs.getBlockTileEntity(world, getHost(networkID))).disconnectFromNetwork(networkID);

		for(Point client : getClients(networkID))
			((TileEntityMachine)Funcs.getBlockTileEntity(world, client)).disconnectFromNetwork(networkID);
	}

	private static class Network {

		private final byte type;
		private int dimensionID;
		private final Point host;
		private final ArrayList<Point> clients = new ArrayList<Point>();

		private Network(byte type, int dimensionID, Point host) {
			this.type = type;
			this.dimensionID = dimensionID;
			this.host = host;
		}
	}
}