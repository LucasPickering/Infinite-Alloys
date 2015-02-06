package infinitealloys.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;

import infinitealloys.network.MessageNetworkEditToClient;
import infinitealloys.network.MessageNetworkEditToServer;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point3;
import io.netty.buffer.ByteBuf;

public class TEEEnergyStorage extends TileEntityElectric implements IHost {

  /**
   * The maximum amount of RK that this machine can store
   */
  private int maxRK;

  /**
   * The amount of RK currently stored in the unit
   */
  private int currentRK;

  /**
   * The max range that machines can be added at with the Internet Wand
   */
  public int range;

  /**
   * The ratio between how much RK an item produces and how long it will burn in a furnace. furnace
   * is numerator, ESU is denominator.
   */
  private final float FURNACE_TO_ESU__RATIO = 0.18F;

  /**
   * A list of clients currently connected to this energy network
   */
  private final ArrayList<Point3> networkClients = new ArrayList<Point3>();

  /**
   * False until the first call of {@link #updateEntity()}
   */
  private boolean initialized;

  public TEEEnergyStorage() {
    super(10);
    baseRKPerTick = 1;
  }

  @Override
  public EnumMachine getEnumMachine() {
    return EnumMachine.ENERGY_STORAGE;
  }

  @Override
  public void loadNBTData(NBTTagCompound tagCompound) {
    currentRK = tagCompound.getInteger("currentRK");
  }

  @Override
  public void updateEntity() {
    if (energyHost == null) {
      energyHost = coords();
    }

    if (!initialized) {
      initialized = true;
      if (!worldObj.isRemote) {
        for (Point3 client : networkClients) {
          ((TileEntityElectric) Funcs.getTileEntity(worldObj, client))
              .connectToEnergyNetwork(coords());
        }
      }
    }

    super.updateEntity();
  }

  @Override
  public void connectToEnergyNetwork(Point3 host) {
    if (!worldObj.isRemote) {
      deleteNetwork();
    }
    super.connectToEnergyNetwork(host);
  }

  @Override
  public void deleteNetwork() {
    for (Point3 client : networkClients) {
      TileEntity te = Funcs.getTileEntity(worldObj, client);
      if (te instanceof TileEntityElectric) {
        ((TileEntityElectric) te).disconnectFromEnergyNetwork();
      }
    }
  }

  @Override
  public boolean isClientValid(Point3 client) {
    return Funcs.getTileEntity(worldObj, client) instanceof TileEntityElectric;
  }

  @Override
  public boolean addClient(EntityPlayer player, Point3 client, boolean sync) {
    if (energyHost != null && !energyHost.equals(coords())) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs
                                                                 .getLoc("machine.textOutput.error",
                                                                         "/: ",
                                                                         "machine.textOutput.error.notHosting")));
      }
    } else if (initialized && !isClientValid(client)) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs
                                                                 .getLoc("machine.textOutput.error",
                                                                         "/: ",
                                                                         "machine.textOutput.error.notElectric")));
      }
    } else if (networkClients.contains(client)) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs
                                                                 .getLoc("machine.textOutput.error",
                                                                         "/: ",
                                                                         "machine.textOutput.error.alreadyInNetwork")));
      }
    } else if (client.equals(xCoord, yCoord, zCoord)) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs
                                                                 .getLoc("machine.textOutput.error",
                                                                         "/: ",
                                                                         "machine.textOutput.error.cannotAddSelf")));
      }
    } else if (client.distanceTo(xCoord, yCoord, zCoord) > range) {
      if (player != null && worldObj.isRemote) {
        player.addChatComponentMessage(new ChatComponentText(Funcs
                                                                 .getLoc("machine.textOutput.error",
                                                                         "/: ",
                                                                         "machine.textOutput.error.outOfRange")));
      }
    } else {
      networkClients.add(client); // Add the machine

      if (initialized) {
        ((TileEntityElectric) Funcs.getTileEntity(worldObj, client))
            .connectToEnergyNetwork(coords()); // Tell the client machine to connect
      }

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
    TileEntity te = Funcs.getTileEntity(worldObj, client);
    if (te instanceof TileEntityElectric) {
      ((TileEntityElectric) te).disconnectFromEnergyNetwork();
    }
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
      Funcs.sendPacketToPlayer(
          new MessageNetworkEditToClient(true, worldObj.provider.dimensionId, coords(), client),
          player);
    }
  }

  @Override
  public int getNetworkSize() {
    return networkClients.size();
  }

  /**
   * Is this ESU hosting the energy network that it's connected to, or is it just acting as a
   * generator for another ESU
   */
  public boolean isHostingNetwork() {
    return energyHost.equals(coords());
  }

  @Override
  public boolean shouldProcess() {
    if (getProcessProgress() > 0) {
      return true;
    }
    for (int i = 0; i < inventoryStacks.length - 1; i++) {
      if (inventoryStacks[i] != null) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void onStartProcess() {
    // Take one piece of fuel out of the first slot that has fuel
    for (int i = 0; i < 9; i++) {
      if (inventoryStacks[i] != null) {
        baseRKPerTick =
            (int) (TileEntityFurnace.getItemBurnTime(inventoryStacks[i]) * FURNACE_TO_ESU__RATIO);
        decrStackSize(i, 1);
        return;
      }
    }
  }

  @Override
  protected NBTTagCompound getDropTagCompound() {
    if (currentRK != 0) {
      NBTTagCompound tagCompound = new NBTTagCompound();
      tagCompound.setInteger("currentRK", currentRK);
      return tagCompound;
    }
    return null;
  }

  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    currentRK = tagCompound.getInteger("currentRK");
    baseRKPerTick = tagCompound.getInteger("baseRKPerTick");
    for (int i = 0; tagCompound.hasKey("client" + i); i++) {
      int[] client = tagCompound.getIntArray("client" + i);
      addClient(null, new Point3(client[0], client[1], client[2]), false);
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    tagCompound.setInteger("currentRK", currentRK);
    tagCompound.setInteger("baseRKPerTick", baseRKPerTick);
    for (int i = 0; i < networkClients.size(); i++) {
      Point3 client = networkClients.get(i);
      tagCompound.setIntArray("client" + i, new int[]{client.x, client.y, client.z});
    }
  }

  @Override
  public void readToClientData(ByteBuf bytes) {
    super.readToClientData(bytes);
    currentRK = bytes.readInt();
    baseRKPerTick = bytes.readInt();
  }

  @Override
  public void writeToClientData(ByteBuf bytes) {
    super.writeToClientData(bytes);
    bytes.writeInt(currentRK);
    bytes.writeInt(baseRKPerTick);
  }

  @Override
  public void onNeighborChange(int x, int y, int z) {
    TileEntity te = worldObj.getTileEntity(x, y, z);
    if (initialized && te instanceof TileEntityElectric
        && ((TileEntityElectric) te).energyHost == null) {
      addClient(null, new Point3(x, y, z), false);
    }
  }

  /**
   * Will the unit support the specified change in RK, i.e. if changeInRK is added to currentRK,
   * will the result be between zero and the machine's capacity? If this condition is true, make the
   * change, i.e. add changeInRK to currentRK
   *
   * @param changeInRK the specified change in RK
   * @return true if changeInRK plus currentRK is between 0 and maxRK, False otherwise
   */
  public boolean changeRK(int changeInRK) {
    if (currentRK + changeInRK >= 0 && currentRK + changeInRK <= maxRK) {
      currentRK += changeInRK;
      return true;
    }
    return false;
  }

  public int getCurrentRK() {
    return currentRK;
  }

  public int getMaxRK() {
    return maxRK;
  }

  @Override
  protected void updateUpgrades() {
    float[] speedUpgradeValues = {1F, 1.33F, 1.67F, 2F};
    processSpeedMult = speedUpgradeValues[getUpgradeTier(EnumUpgrade.SPEED)];

    float[] efficiencyUpgradeValues = {1F, 1.33F, 1.67F, 2F};
    rkPerTickMult = efficiencyUpgradeValues[getUpgradeTier(EnumUpgrade.EFFICIENCY)];

    int[] capacityUpgradeValues = {100000000, 200000000, 300000000, 400000000};
    maxRK = capacityUpgradeValues[getUpgradeTier(EnumUpgrade.CAPACITY)];

    int[] rangeUpgradeValues = {30, 40, 50, 60};
    range = rangeUpgradeValues[getUpgradeTier(EnumUpgrade.RANGE)];
  }

  @Override
  protected void populateValidUpgrades() {
    addValidUpgradeType(EnumUpgrade.SPEED);
    addValidUpgradeType(EnumUpgrade.EFFICIENCY);
    addValidUpgradeType(EnumUpgrade.CAPACITY);
    addValidUpgradeType(EnumUpgrade.RANGE);
    addValidUpgradeType(EnumUpgrade.WIRELESS);
  }
}
