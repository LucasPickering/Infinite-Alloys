package infinitealloys.util;

import infinitealloys.network.PacketAddClient;
import infinitealloys.network.PacketCreateNetwork;
import infinitealloys.tile.TileEntityMachine;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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

	/** Read network data from the tag compound and add these networks to the list
	 * 
	 * @param nbtTagCompound the tag compound containing the network data
	 * @param world the world that is being loaded, used for {@link #notifyNetwork} */
	public static void loadData(NBTTagCompound nbtTagCompound, World world) {
		if(Funcs.isServer()) {
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

				notifyNetwork(networkID, world);
			}
		}
	}

	public static void saveData(NBTTagCompound nbtTagCompound) {
		if(Funcs.isServer()) {
			for(int i = 0; i < networks.size(); i++) {
				NBTTagCompound networkNBT = new NBTTagCompound();
				Network network = getNetwork(i);

				if(network != null) {
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

	/** Create a new network in a certain position, only used on client to sync with server */
	public static void createNetwork(int networkID, byte type, int dimensionID, Point host) {
		if(Funcs.isClient()) {
			networks.set(networkID, new Network(type, dimensionID, host));
			notifyNetwork(networkID, Minecraft.getMinecraft().theWorld);
		}
	}

	/** Add a client to a network
	 * 
	 * @param networkID the ID of the network in question
	 * @param client the coordinates of the block that is being added as a client */
	public static void addClient(int networkID, Point client) {
		Network network = networks.get(networkID);
		network.clients.add(client);
		((TileEntityMachine)Funcs.getBlockTileEntity(network.getWorld(), client)).connectToNetwork(network.type, networkID);
		if(Funcs.isClient())
			PacketDispatcher.sendPacketToServer(PacketAddClient.getPacket(networkID, client.x, (short)client.y, client.z));
		else
			PacketDispatcher.sendPacketToAllPlayers(PacketAddClient.getPacket(networkID, client.x, (short)client.y, client.z));
	}

	/** Remove a client from a network
	 * 
	 * @param networkID the ID of the network in question
	 * @param client the coordinates of the block that is being removed */
	public static void removeClient(int networkID, Point client) {
		if(Funcs.isServer()) {
			Network network = networks.get(networkID);
			network.clients.remove(client);
			((TileEntityMachine)Funcs.getBlockTileEntity(network.getWorld(), client)).disconnectFromNetwork(network.type);
			PacketDispatcher.sendPacketToAllPlayers(PacketAddClient.getPacket(networkID, client.x, (short)client.y, client.z));
		}
	}

	public static Network getNetwork(int networkID) {
		return networks.get(networkID);
	}

	/** Get the amount of clients on a specific network
	 * 
	 * @param networkID the ID of the network in question
	 * @return the amount of clients on the network */
	public static int getSize(int networkID) {
		return networks.get(networkID).clients.size();
	}

	public static void deleteNetwork(int networkID) {
		Network network = networks.get(networkID);
		((TileEntityMachine)Funcs.getBlockTileEntity(network.getWorld(), network.host)).disconnectFromNetwork(network.type);
		for(Point client : network.clients)
			((TileEntityMachine)Funcs.getBlockTileEntity(network.getWorld(), client)).disconnectFromNetwork(network.type);
		networks.set(networkID, null);
	}

	public static Point getHost(int networkID) {
		return networks.get(networkID).host;
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

	/** Sends packets to a certain client to completely sync all network data */
	public static void syncAllNetworks(Player player) {
		if(Funcs.isServer()) {
			for(int id = 0; id < networks.size(); id++) {
				if(getNetwork(id) != null) {
					Network network = getNetwork(id);
					PacketDispatcher.sendPacketToPlayer(PacketCreateNetwork.getPacket(id, network.type, network.dimensionID, network.host.x, (short)network.host.y, network.host.z), player);
					for(Point client : getClients(id))
						PacketDispatcher.sendPacketToAllPlayers(PacketAddClient.getPacket(id, client.x, (short)client.y, client.z));
				}
			}
		}
	}

	/** Tell the TE for the host and each client in the network to connect to this network */
	private static void notifyNetwork(int networkID, World world) {
		Network network = getNetwork(networkID);
		TileEntity te = Funcs.getBlockTileEntity(world, getHost(networkID));
		((TileEntityMachine)te).connectToNetwork(network.type, networkID);

		for(Point client : network.clients)
			((TileEntityMachine)Funcs.getBlockTileEntity(world, client)).connectToNetwork(network.type, networkID);
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