package infinitealloys.tile;

import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Machines that can host networks between other machines, e.g. the computer */
public interface IHost {

	/** Is the block at the specified point a valid client for this network? */
	public boolean isClientValid(Point client);

	/** Add a client to the network that is being hosted by this machine */
	public boolean addClient(EntityPlayer player, Point client);

	/** Delete all networks hosted by this machine. This is only called when the TE block is broken. */
	public void deleteNetworks();

	/** Called when the client receives a packet from the server with information on the network that this machine is hosting. */
	@SideOnly(Side.CLIENT)
	public void createNetwork(int networkID, int type);
}
