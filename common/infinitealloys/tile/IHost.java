package infinitealloys.tile;

import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;

/** Machines that can host networks between other machines, e.g. the computer */
public interface IHost {

	/** Is the block at the specified point a valid client for this network? This checks a general criterion, e.g. for the ESU it just checks that it's an electrical block.
	 * More extensive checks, such as if the client is already in the network, are done in {@link #addClient} */
	public boolean isClientValid(Point client);

	/** Add a client to the network
	 * 
	 * @param player the player that is adding the client, this is used to send chat messages to them */
	public boolean addClient(EntityPlayer player, Point client);

	/** Remove a client from the network */
	public void removeClient(Point client);

	/** Delete all the network hosted by this machine. This is typically called when the TE block is broken. */
	public void deleteNetwork();

	/** Get the amount of clients in the network */
	public int getNetworkSize();
}
