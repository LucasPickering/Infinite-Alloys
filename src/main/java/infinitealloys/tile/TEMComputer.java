package infinitealloys.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;

import infinitealloys.network.MessageNetworkEditToClient;
import infinitealloys.network.MessageNetworkEditToServer;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;

public final class TEMComputer extends TileEntityMachine implements IHost {

  /**
   * The max range that machines can be added at with the Internet Wand
   */
  public int range;

  /**
   * The number of machines that this block can host
   */
  public int networkCapacity;

  /**
   * A list of clients currently connected to this computer control network
   */
  private final ArrayList<BlockPos> networkClients = new ArrayList<>();

  public TEMComputer() {
    super(1);
  }

  @Override
  public EnumMachine getEnumMachine() {
    return EnumMachine.COMPUTER;
  }

  @Override
  public void updateEntity() {
    if (computerHost == null) {
      computerHost = pos;
    }
    super.updateEntity();
  }

  @Override
  public void onFirstTick() {
    super.onFirstTick();
    if (!worldObj.isRemote) {
      for (BlockPos client : networkClients) {
        ((TileEntityMachine) worldObj.getTileEntity(client)).connectToComputerNetwork(pos);
      }
    }
  }

  @Override
  public void onBlockDestroyed() {
    if (computerHost.equals(pos)) {
      deleteNetwork();
    }
    super.onBlockDestroyed();
  }

  @Override
  public void connectToComputerNetwork(BlockPos host) {
    deleteNetwork();
    super.connectToComputerNetwork(host);
  }

  @Override
  public void deleteNetwork() {
    for (BlockPos client : networkClients) {
      TileEntity te = worldObj.getTileEntity(client);
      if (te instanceof TileEntityMachine) {
        ((TileEntityMachine) te).disconnectFromComputerNetwork();
      }
    }
  }

  @Override
  public boolean isClientValid(BlockPos client) {
    TileEntity te = worldObj.getTileEntity(client);
    return te instanceof TileEntityMachine
           && ((TileEntityMachine) te).hasUpgrade(EnumUpgrade.WIRELESS, 1);
  }

  private void addClient(BlockPos client) {
    networkClients.add(client); // Add the machine
    TileEntity clientTE = worldObj.getTileEntity(client);
    if (clientTE instanceof TileEntityMachine) {
      // Tell the client machine to connect
      ((TileEntityMachine) clientTE).connectToComputerNetwork(pos);
    }
  }

  @Override
  public boolean addClientWithChecks(EntityPlayer player, BlockPos client, boolean sync) {
    if (networkClients.contains(client)) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc(
            "machine.textOutput.error", "/: ", "machine.textOutput.error.alreadyInNetwork")));
      }
    } else if (networkClients.size() >= networkCapacity) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc(
            "machine.textOutput.error", "/: ", "machine.textOutput.error.networkFull")));
      }
    } else if (client.equals(pos)) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc(
            "machine.textOutput.error", "/: ", "machine.textOutput.error.cannotAddSelf")));
      }
    } else if (client.distanceSq(pos) > range) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc(
            "machine.textOutput.error", "/: ", "machine.textOutput.error.outOfRange")));
      }
    } else if (!isClientValid(client)) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc(
            "machine.textOutput.error", "/: ", "machine.textOutput.error.notWireless")));
      }
    } else {
      addClient(client);

      // Sync the data to the server/all clients
      if (sync) { // If we should sync
        if (worldObj.isRemote) { // If this is the client
          // Sync to server
          Funcs.sendPacketToServer(
              new MessageNetworkEditToServer(true, worldObj.provider.getDimensionId(), pos, client));
          if (player != null) {
            // Send a chat message
            player.addChatComponentMessage(new ChatComponentText(
                Funcs.getLoc("machine.textOutput.addingMachine") + client));
          }
        } else {
          // Sync to clients
          Funcs.sendPacketToAllPlayers(
              new MessageNetworkEditToClient(true, worldObj.provider.getDimensionId(), pos, client));
        }
      }

      return true;
    }
    return false;
  }

  @Override
  public void removeClient(BlockPos client, boolean sync) {
    networkClients.remove(client);
    if (sync) {
      if (worldObj.isRemote) {
        Funcs.sendPacketToServer(
            new MessageNetworkEditToServer(false, worldObj.provider.getDimensionId(), pos, client));
      } else {
        Funcs.sendPacketToAllPlayers(
            new MessageNetworkEditToClient(false, worldObj.provider.getDimensionId(), pos, client));
      }
    }
  }

  @Override
  public void syncAllClients(EntityPlayer player) {
    for (BlockPos client : networkClients) {
      Funcs
          .sendPacketToPlayer(new MessageNetworkEditToClient(true, worldObj.provider.getDimensionId(),
                                                             pos, client), player);
    }
  }

  @Override
  public int getNetworkSize() {
    return networkClients.size();
  }

  /**
   * Get an array of clients connected to this machine.
   */
  public BlockPos[] getClients() {
    return networkClients.toArray(new BlockPos[networkClients.size()]);
  }

  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    for (int i = 0; tagCompound.hasKey("client" + i); i++) {
      int[] client = tagCompound.getIntArray("client" + i);
      addClient(new BlockPos(client[0], client[1], client[2]));
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    for (int i = 0; i < networkClients.size(); i++) {
      BlockPos client = networkClients.get(i);
      tagCompound.setIntArray("client" + i, new int[]{client.getX(), client.getY(), client.getZ()});
    }
  }

  @Override
  public void onNeighborChange(BlockPos pos) {
    TileEntity te = worldObj.getTileEntity(pos);
    if (te instanceof TileEntityMachine && ((TileEntityMachine) te).computerHost == null) {
      addClientWithChecks(null, pos, false);
    }
  }

  @Override
  protected void updateUpgrades() {
    int[] capacityUpgradeValues = {3, 5, 7, 10};
    networkCapacity = capacityUpgradeValues[getUpgradeTier(EnumUpgrade.CAPACITY)];

    int[] rangeUpgradeValues = {30, 40, 50, 60};
    range = rangeUpgradeValues[getUpgradeTier(EnumUpgrade.RANGE)];
  }

  @Override
  protected void populateValidUpgrades() {
    addValidUpgradeType(EnumUpgrade.CAPACITY);
    addValidUpgradeType(EnumUpgrade.RANGE);
  }
}
