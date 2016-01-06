package infinitealloys.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

/**
 * Machines that can host networks between other machines, e.g. the computer
 */
public interface IHost {

  /**
   * Is the block at the specified point a valid client for this network? This checks general
   * critera, e.g. the ESU just checks that it's an electrical block. More extensive checks, such as
   * if the client is already in the network, are done in {@link #addClientWithChecks}.
   */
  boolean isClientValid(BlockPos client);

  /**
   * Check if a client is valid, and if so add it to the network.
   *
   * @param player the player that is adding the client
   * @param sync   if true, a packet will be sent to the other side to add the client
   */
  boolean addClientWithChecks(EntityPlayer player, BlockPos client, boolean sync);

  /**
   * Remove a client from the network.
   *
   * @param sync if true, a packet will be sent to the other side to remove the client
   */
  void removeClient(BlockPos client, boolean sync);

  /**
   * Send the network data to all clients.
   */
  void syncAllClients(EntityPlayer player);

  /**
   * Get the amount of clients in the network.
   */
  int getNetworkSize();

  /**
   * Remove all clients from the network.
   */
  void deleteNetwork();
}
