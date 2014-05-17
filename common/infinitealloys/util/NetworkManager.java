package infinitealloys.util;

import infinitealloys.network.PacketAddClient;
import infinitealloys.network.PacketCreateNetwork;
import infinitealloys.tile.TileEntityMachine;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** This is meant for in-game IA networks, not real computer networks. It organizes and manages the connections between hosts, such as an ESU, and their
 * clients. */
public class NetworkManager {

	private static ArrayList<Network> networks = new ArrayList<Network>();

	@SideOnly(Side.SERVER)
	public static void loadData(NBTTagCompound nbtTagCompound) {
		networks.clear();
		for(int i = 0; nbtTagCompound.hasKey("network" + i); i++) {
			NBTTagCompound network = nbtTagCompound.getCompoundTag("network" + i);

			byte type = network.getByte("type");
			int dimensionID = network.getInteger("dimensionID");
			int[] host = network.getIntArray("host");
			int networkID = buildNetwork(type, dimensionID, new Point(host[0], host[1], host[2]));

			for(int j = 0; network.hasKey("client" + j); j++) {
				int[] client = network.getIntArray("client" + j);
				addClient(networkID, new Point(client[0], client[1], client[2]));
			}

			notifyNetwork(networkID);
		}
	}

	@SideOnly(Side.SERVER)
	public static void saveData(NBTTagCompound nbtTagCompound) {
		for(int i = 0; i < networks.size(); i++) {
			NBTTagCompound networkNBT = new NBTTagCompound();
			Network network = getNetwork(i);

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

	/** Create a new network for this host */
	public static int buildNetwork(byte type, int dimensionID, Point host) {
		if(Funcs.isClient())
			return -1;

		// Look through the list for null spaces that were left by deleted networks
		for(int i = 0; i < networks.size(); i++) {
			if(networks.get(i) == null) {
				networks.set(i, new Network(type, dimensionID, host));
				PacketDispatcher.sendPacketToAllPlayers(PacketCreateNetwork.getPacket(i, type, dimensionID, host.x, (short)host.y, host.z));
				return i;
			}
		}

		// If there are no empty spaces to be filled, add a new network to the end of the list
		networks.add(new Network(type, dimensionID, host));
		System.out.println("Creating new network with index " + (networks.size() - 1));
		PacketDispatcher.sendPacketToAllPlayers(PacketCreateNetwork.getPacket(networks.size() - 1, type, dimensionID, host.x, (short)host.y, host.z));
		return networks.size() - 1;
	}

	public static Network getNetwork(int networkID) {
		return networks.get(networkID);
	}

	public static void deleteNetwork(int networkID) {
		Network network = networks.get(networkID);
		((TileEntityMachine)network.getWorld().getBlockTileEntity(network.host.x, network.host.y, network.host.z)).disconnectFromNetwork(network.type);
		for(Point client : network.clients)
			((TileEntityMachine)network.getWorld().getBlockTileEntity(client.x, client.y, client.z)).disconnectFromNetwork(network.type);
		networks.set(networkID, null);
	}

	public static Point getHost(int networkID) {
		return networks.get(networkID).host;
	}

	public static TileEntityMachine getHostTE(int networkID) {
		Network network = getNetwork(networkID);
		World world;
		if(Funcs.isServer())
			world = network.getWorld();
		else
			world = Minecraft.getMinecraft().theWorld;
		return (TileEntityMachine)world.getBlockTileEntity(network.host.x, network.host.y, network.host.z);
	}

	/** Add a client to a network
	 * 
	 * @param networkID the ID of the network in question
	 * @param client the coordinates of the block that is being added as a client */
	public static void addClient(int networkID, Point client) {
		Network network = networks.get(networkID);
		network.clients.add(client);
		((TileEntityMachine)network.getWorld().getBlockTileEntity(client.x, client.y, client.z)).connectToNetwork(network.type, networkID);
		if(Funcs.isClient())
			PacketDispatcher.sendPacketToServer(PacketAddClient.getPacket(networkID, client.x, (short)client.y, client.z));
		else
			PacketDispatcher.sendPacketToAllPlayers(PacketAddClient.getPacket(networkID, client.x, (short)client.y, client.z));
	}

	/** Remove a client from a network
	 * 
	 * @param networkID the ID of the network in question
	 * @param client the coordinates of the block that is being removed */
	@SideOnly(Side.SERVER)
	public static void removeClient(int networkID, Point client) {
		Network network = networks.get(networkID);
		network.clients.remove(client);
		((TileEntityMachine)network.getWorld().getBlockTileEntity(client.x, client.y, client.z)).disconnectFromNetwork(network.type);
		PacketDispatcher.sendPacketToAllPlayers(PacketAddClient.getPacket(networkID, client.x, (short)client.y, client.z));
	}

	/** Get a specific client from a network
	 * 
	 * @param networkID the ID of the network in question
	 * @param clientIndex the index of the client in the network's list of clients
	 * @return a Point that contains the coordinates to the client */
	public static Point getClient(int networkID, int clientIndex) {
		return networks.get(networkID).clients.get(clientIndex);
	}

	/** Get an array of the clients on a specific network
	 * 
	 * @param networkID the ID of the network in question
	 * @return an array of Points that contain the coordinates to each client */
	public static Point[] getClients(int networkID) {
		return networks.get(networkID).clients.toArray(new Point[networks.get(networkID).clients.size()]);
	}

	/** Check if a machine is in a specific network
	 * 
	 * @param networkID the ID of the network in question
	 * @param p the location of the machine in question
	 * @return true if the machine is already a client on the network, false otherwise */
	public static boolean hasClient(int networkID, Point p) {
		return networks.get(networkID).clients.contains(p);
	}

	/** Get the amount of clients on a specific network
	 * 
	 * @param networkID the ID of the network in question
	 * @return the amount of clients on the network */
	public static int getSize(int networkID) {
		return networks.get(networkID).clients.size();
	}

	/** Tell the TE for the host and each client in the network to connect to this network */
	private static void notifyNetwork(int networkID) {
		Network network = getNetwork(networkID);
		getHostTE(networkID).connectToNetwork(network.type, networkID);

		for(Point client : network.clients)
			((TileEntityMachine)network.getWorld().getBlockTileEntity(client.x, client.y, client.z)).connectToNetwork(network.type, networkID);
	}

	/** Create a new network in a certain position, only used on client to sync with server */
	@SideOnly(Side.CLIENT)
	public static void createNetwork(int networkID, byte type, int dimensionID, Point host) {
		networks.set(networkID, new Network(type, dimensionID, host));
		notifyNetwork(networkID);
	}

	/** Delete all networks. Typically precedes a full network sync */
	@SideOnly(Side.CLIENT)
	public static void clearNetworks() {
		networks.clear();
	}

	/** Sends packets to a certain client to completely sync all network data */
	@SideOnly(Side.SERVER)
	public static void syncAllNetworks(Player player) {
		for(int id = 0; id < networks.size(); id++) {
			if(getNetwork(id) != null) {
				System.out.println("Sending " + id);
				Network network = getNetwork(id);
				PacketDispatcher.sendPacketToPlayer(PacketCreateNetwork.getPacket(id, network.type, network.dimensionID, network.host.x, (short)network.host.y, network.host.z), player);
				for(Point client : getClients(id))
					PacketDispatcher.sendPacketToAllPlayers(PacketAddClient.getPacket(id, client.x, (short)client.y, client.z));
			}
		}
	}

	private static class Network {

		private final byte type;
		private int dimensionID = 0;
		private final Point host;
		private final ArrayList<Point> clients = new ArrayList<Point>();

		private Network(byte type, int dimensionID, Point host) {
			this.type = type;
			this.dimensionID = dimensionID;
			this.host = host;
		}

		@SideOnly(Side.CLIENT)
		private Network(byte type, Point host) {
			this.type = type;
			this.host = host;
		}

		private World getWorld() {
			return DimensionManager.getWorld(dimensionID);
		}
	}
}