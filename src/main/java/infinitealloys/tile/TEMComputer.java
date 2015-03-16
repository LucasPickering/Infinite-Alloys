package infinitealloys.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;

import infinitealloys.network.MessageNetworkEditToClient;
import infinitealloys.network.MessageNetworkEditToServer;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point3;

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
  private final ArrayList<Point3> networkClients = new ArrayList<>();

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
      computerHost = coords();
    }
    super.updateEntity();
  }

  @Override
  public void onFirstTick() {
    super.onFirstTick();
    if (!worldObj.isRemote) {
      for (Point3 client : networkClients) {
        ((TileEntityMachine) Funcs.getTileEntity(worldObj, client))
            .connectToComputerNetwork(coords());
      }
    }
  }

  @Override
  public void onBlockDestroyed() {
    if (computerHost.equals(coords())) {
      deleteNetwork();
    }
    super.onBlockDestroyed();
  }

  @Override
  public void connectToComputerNetwork(Point3 host) {
    deleteNetwork();
    super.connectToComputerNetwork(host);
  }

  @Override
  public void deleteNetwork() {
    for (Point3 client : networkClients) {
      TileEntity te = Funcs.getTileEntity(worldObj, client);
      if (te instanceof TileEntityMachine) {
        ((TileEntityMachine) te).disconnectFromComputerNetwork();
      }
    }
  }

  @Override
  public boolean isClientValid(Point3 client) {
    TileEntity te = Funcs.getTileEntity(worldObj, client);
    return te instanceof TileEntityMachine
           && ((TileEntityMachine) te).hasUpgrade(EnumUpgrade.WIRELESS, 1);
  }

  private void addClient(Point3 client) {
    networkClients.add(client); // Add the machine
    TileEntity clientTE = Funcs.getTileEntity(worldObj, client);
    if (clientTE instanceof TileEntityMachine) {
      // Tell the client machine to connect
      ((TileEntityMachine) clientTE).connectToComputerNetwork(coords());
    }
  }

  @Override
  public boolean addClientWithChecks(EntityPlayer player, Point3 client, boolean sync) {
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
    } else if (client.equals(xCoord, yCoord, zCoord)) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc(
            "machine.textOutput.error", "/: ", "machine.textOutput.error.cannotAddSelf")));
      }
    } else if (client.distanceTo(xCoord, yCoord, zCoord) > range) {
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
          Funcs.sendPacketToServer(
              new MessageNetworkEditToServer(true, worldObj.provider.dimensionId, coords(),
                                             client)); // Sync to server
          if (player != null) {
            player.addChatComponentMessage(new ChatComponentText(
                Funcs.getLoc("machine.textOutput.addingMachine") + client)); // Send a chat message
          }
        } else {
          Funcs.sendPacketToAllPlayers(
              new MessageNetworkEditToClient(true, worldObj.provider.dimensionId, coords(),
                                             client)); // Sync to clients
        }
      }

      return true;
    }
    return false;
  }

  @Override
  public void removeClient(Point3 client, boolean sync) {
    networkClients.remove(client);
    if (sync) {
      if (worldObj.isRemote) {
        Funcs.sendPacketToServer(
            new MessageNetworkEditToServer(false, worldObj.provider.dimensionId, coords(), client));
      } else {
        Funcs.sendPacketToAllPlayers(
            new MessageNetworkEditToClient(false, worldObj.provider.dimensionId, coords(), client));
      }
    }
  }

  @Override
  public void syncAllClients(EntityPlayer player) {
    for (Point3 client : networkClients) {
      Funcs.sendPacketToPlayer(new MessageNetworkEditToClient(true, worldObj.provider.dimensionId,
                                                              coords(), client), player);
    }
  }

  @Override
  public int getNetworkSize() {
    return networkClients.size();
  }

  /**
   * Get an array of clients connected to this machine.
   */
  public Point3[] getClients() {
    return networkClients.toArray(new Point3[networkClients.size()]);
  }

  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    for (int i = 0; tagCompound.hasKey("client" + i); i++) {
      int[] client = tagCompound.getIntArray("client" + i);
      addClient(new Point3(client[0], client[1], client[2]));
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    for (int i = 0; i < networkClients.size(); i++) {
      Point3 client = networkClients.get(i);
      tagCompound.setIntArray("client" + i, new int[]{client.x, client.y, client.z});
    }
  }

  @Override
  public void onNeighborChange(int x, int y, int z) {
    TileEntity te = worldObj.getTileEntity(x, y, z);
    if (te instanceof TileEntityMachine
        && ((TileEntityMachine) te).computerHost == null) {
      addClientWithChecks(null, new Point3(x, y, z), false);
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
