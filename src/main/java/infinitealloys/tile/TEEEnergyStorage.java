package infinitealloys.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.LinkedList;
import java.util.List;

import infinitealloys.network.MessageNetworkEditToClient;
import infinitealloys.network.MessageNetworkEditToServer;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import io.netty.buffer.ByteBuf;

public final class TEEEnergyStorage extends TileEntityElectric implements IHost {

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
   * A list of clients currently connected to this energy network
   */
  private final List<BlockPos> networkClients = new LinkedList<>();

  public TEEEnergyStorage() {
    super(10);
    baseRKPerTick = 1;
  }

  @Override
  public EnumMachine getMachineType() {
    return EnumMachine.ENERGY_STORAGE;
  }

  @Override
  public void loadNBTData(NBTTagCompound tagCompound) {
    currentRK = tagCompound.getInteger("currentRK");
  }

  @Override
  public void update() {
    if (energyHost == null) {
      energyHost = pos;
    }
    super.update();
  }

  @Override
  public void onFirstTick() {
    super.onFirstTick();
    if (!worldObj.isRemote) {
      for (BlockPos client : networkClients) {
        ((TileEntityElectric) worldObj.getTileEntity(client))
            .connectToEnergyNetwork(pos);
      }
    }
  }

  @Override
  public void connectToEnergyNetwork(BlockPos host) {
    if (!worldObj.isRemote) {
      deleteNetwork();
    }
    super.connectToEnergyNetwork(host);
  }

  @Override
  public void deleteNetwork() {
    for (BlockPos client : networkClients) {
      TileEntity te = worldObj.getTileEntity(client);
      if (te instanceof TileEntityElectric) {
        ((TileEntityElectric) te).disconnectFromEnergyNetwork();
      }
    }
  }

  @Override
  public boolean isClientValid(BlockPos client) {
    return worldObj.getTileEntity(client) instanceof TileEntityElectric;
  }

  private void addClient(BlockPos client) {
    networkClients.add(client); // Add the machine
    if (worldObj != null) {
      TileEntity clientTE = worldObj.getTileEntity(client);
      if (clientTE instanceof TileEntityElectric) {
        // Tell the client machine to connect
        ((TileEntityElectric) clientTE).connectToEnergyNetwork(pos);
      }
    }
  }

  @Override
  public boolean addClientWithChecks(EntityPlayer player, BlockPos client, boolean sync) {
    if (energyHost != null && !energyHost.equals(pos)) {
      if (player != null && worldObj.isRemote) {
        MachineHelper.sendMachineErrorToPlayer(player, "machine.textOutput.error.notHosting");
      }
    } else if (worldObj != null && !isClientValid(client)) {
      if (player != null && worldObj.isRemote) {
        MachineHelper.sendMachineErrorToPlayer(player,  "machine.textOutput.error.notElectric");
      }
    } else if (networkClients.contains(client)) {
      if (player != null && worldObj.isRemote) {
        MachineHelper.sendMachineErrorToPlayer(player,  "machine.textOutput.error.alreadyInNetwork");
      }
    } else if (client.equals(pos)) {
      if (player != null && worldObj.isRemote) {
        MachineHelper.sendMachineErrorToPlayer(player,  "machine.textOutput.error.cannotAddSelf");
      }
    } else if (client.distanceSq(pos) > range) {
      if (player != null && worldObj.isRemote) {
        MachineHelper.sendMachineErrorToPlayer(player,  "machine.textOutput.error.outOfRange");
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
                Funcs.formatLoc("%k %s", "machine.textOutput.addingMachine", client)));
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
    TileEntity te = worldObj.getTileEntity(client);
    if (te instanceof TileEntityElectric) {
      ((TileEntityElectric) te).disconnectFromEnergyNetwork();
    }
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
      Funcs.sendPacketToPlayer(new MessageNetworkEditToClient(
          true, worldObj.provider.getDimensionId(), pos, client), player);
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
    return energyHost.equals(pos);
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
        baseRKPerTick = (int) (TileEntityFurnace.getItemBurnTime(inventoryStacks[i]) *
                               Consts.FURNACE_TO_ESU__RATIO);
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
      addClient(new BlockPos(client[0], client[1], client[2]));
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    tagCompound.setInteger("currentRK", currentRK);
    tagCompound.setInteger("baseRKPerTick", baseRKPerTick);
    for (int i = 0; i < networkClients.size(); i++) {
      BlockPos client = networkClients.get(i);
      tagCompound.setIntArray("client" + i, new int[]{client.getX(), client.getY(), client.getZ()});
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
  public void onNeighborChange(BlockPos pos) {
    TileEntity te = worldObj.getTileEntity(pos);
    if (te instanceof TileEntityElectric && ((TileEntityElectric) te).energyHost == null) {
      addClientWithChecks(null, new BlockPos(pos), false);
    }
  }

  /**
   * Will the unit support the specified change in RK, i.e. if changeInRK is added to currentRK, will
   * the result be between zero and the machine's capacity? If this condition is true, make the
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
