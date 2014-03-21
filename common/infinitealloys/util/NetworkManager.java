package infinitealloys.util;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/** This is meant for in-game IA networks, not real computer networks. It organizes and manages the connections between hosts, such as an ESU, and their
 * clients. */
public class NetworkManager {

	private static ArrayList<Network> networks = new ArrayList<Network>();

	/** Create a new network for this host */
	public static int buildNetwork(int type, World world, Point host) {
		networks.add(new Network(type, world, host));
		return networks.size() - 1;
	}

	public static Network getNetwork(int networkID) {
		return networks.get(networkID);
	}

	public static void deleteNetwork(Point host) {
		for(Iterator iterator = networks.iterator(); iterator.hasNext();) {
			Network network = (Network)iterator.next();
			if(network.host.equals(host)) {
				for(Point client : network.clients) {

				}
			}
		}
	}

	public static Point getHost(int networkID) {
		return networks.get(networkID).host;
	}

	public static TileEntity getHostTE(int networkID) {
		Network network = networks.get(networkID);
		return network.world.getBlockTileEntity(network.host.x, network.host.y, network.host.z);
	}

	/** Add a client to a network
	 * 
	 * @param networkID the ID of the network in question
	 * @param client the coordinates of the block that is being added as a client */
	public static void addClient(int networkID, Point client) {
		networks.get(networkID).clients.add(client);
	}

	/** Remove a client from a network
	 * 
	 * @param networkID the ID of the network in question
	 * @param client the coordinates of the block that is being removed */
	public static void removeClient(int networkID, Point client) {
		networks.get(networkID).clients.remove(client);
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

	private static class Network {

		private World world;
		private Point host;
		private ArrayList<Point> clients = new ArrayList<Point>();

		private Network(int type, World world, Point host) {
			this.host = host;
			this.world = world;
		}
	}
}