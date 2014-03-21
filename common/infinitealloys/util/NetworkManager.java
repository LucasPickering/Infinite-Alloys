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
	public static Network buildNetwork(World world, Point host) {
		Network network = new Network(world, host);
		networks.add(network);
		return network;
	}

	public static void deleteNetwork(Point host) {
		for(Iterator iterator = networks.iterator(); iterator.hasNext();) {
			Network network = (Network)iterator.next();
			if(network.getHost().equals(host)) {
				for(Point client : network.getClients()) {
					
				}
			}
		}
	}

	public static class Network {

		private World world;
		private Point host;
		private ArrayList<Point> clients = new ArrayList<Point>();

		private Network(World world, Point host) {
			this.host = host;
			this.world = world;
		}

		public Point getHost() {
			return host;
		}

		public TileEntity getHostTE() {
			return world.getBlockTileEntity(host.x, host.y, host.z);
		}

		public Point[] getClients() {
			return clients.toArray(new Point[clients.size()]);
		}

		/** Is the given machine already in the network?
		 * 
		 * @param p the location of the machine in question
		 * @return true if the machine is already a client on the network, false otherwise */
		public boolean hasClient(Point p) {
			return clients.contains(p);
		}

		public void addClient(Point p) {
			clients.add(p);
		}

		public void removeClient(Point p) {
			clients.remove(p);
		}

		/** Get the amount of clients on the network */
		public int getSize() {
			return clients.size();
		}
	}
}