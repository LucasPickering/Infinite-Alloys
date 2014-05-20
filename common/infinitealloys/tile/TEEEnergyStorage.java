package infinitealloys.tile;

import infinitealloys.network.PacketAddClient;
import infinitealloys.network.PacketRemoveClient;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import org.apache.commons.lang3.ArrayUtils;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TEEEnergyStorage extends TileEntityElectric implements IHost {

	/** The maximum amount of RK that this machine can store */
	private int maxRK;

	/** The amount of RK currently stored in the unit */
	private int currentRK;

	/** The max range that machines can be added at with the Internet Wand */
	public int range;

	/** The ratio between how long an item will burn in an ESU and how long it will burn in a furnace. ESU is numerator, furnace is denominator. */
	private final float ESU_TO_FURNACE_TICK_RATIO = 0.5F;

	/** A list of clients currently connected to this energy network */
	private ArrayList<Point> networkClients = new ArrayList<Point>();

	public TEEEnergyStorage(byte front) {
		this();
		this.front = front;
	}

	public TEEEnergyStorage() {
		super(10);
		baseRKPerTick = 72;
	}

	@Override
	public int getID() {
		return MachineHelper.ENERGY_STORAGE;
	}

	@Override
	public void updateEntity() {
		EntityPlayer syncPlayer = MachineHelper.networkSyncCheck(worldObj.provider.dimensionId, coords());
		if(syncPlayer != null)
			for(Point client : networkClients)
				PacketDispatcher.sendPacketToPlayer(PacketAddClient.getPacket(worldObj.provider.dimensionId, coords(), client), (Player)syncPlayer);

		if(energyHost == null)
			energyHost = coords();

		super.updateEntity();
	}

	@Override
	public void deleteNetwork() {
		for(Point client : networkClients)
			removeClient(client, true);
	}

	@Override
	public void connectToEnergyNetwork(Point host) {
		super.connectToEnergyNetwork(host);
		deleteNetwork();
	}

	@Override
	public boolean isClientValid(Point client) {
		return Funcs.getBlockTileEntity(worldObj, client) instanceof TileEntityElectric;
	}

	@Override
	public boolean addClient(EntityPlayer player, Point client, boolean sync) {
		if(!energyHost.equals(coords())) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: This machine is not currently hosting a network because it is connected to another host");
		}
		else if(networkClients.contains(client)) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Machine is already in network");
		}
		else if(client.equals(xCoord, yCoord, zCoord)) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Cannot add self to network");
		}
		else if(client.distanceTo(xCoord, yCoord, zCoord) > range) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Block out of range");
		}
		else {
			// Add the machine
			networkClients.add(client);
			((TileEntityElectric)Funcs.getBlockTileEntity(worldObj, client)).energyHost = coords();

			// Sync the data to the server/all clients
			if(worldObj.isRemote) {
				PacketDispatcher.sendPacketToServer(PacketAddClient.getPacket(worldObj.provider.dimensionId, coords(), client));
				if(sync)
					player.addChatMessage("Adding machine at " + client);
			}
			else if(sync)
				PacketDispatcher.sendPacketToAllInDimension(PacketAddClient.getPacket(worldObj.provider.dimensionId, coords(), client), worldObj.provider.dimensionId);

			return true;
		}
		return false;
	}

	@Override
	public void removeClient(Point client, boolean sync) {
		((TileEntityElectric)Funcs.getBlockTileEntity(worldObj, client)).disconnectFromEnergyNetwork();
		networkClients.remove(client);
		if(sync) {
			if(worldObj.isRemote)
				PacketDispatcher.sendPacketToServer(PacketRemoveClient.getPacket(worldObj.provider.dimensionId, coords(), client));
			else
				PacketDispatcher.sendPacketToAllInDimension(PacketRemoveClient.getPacket(worldObj.provider.dimensionId, coords(), client), worldObj.provider.dimensionId);
		}
	}

	@Override
	public int getNetworkSize() {
		return networkClients.size();
	}

	/** Is this ESU hosting the energy network that it's connected to, or is it just acting as a generator for another ESU */
	public boolean isHostingNetwork() {
		return energyHost.equals(coords());
	}

	@Override
	public boolean shouldProcess() {
		if(getProcessProgress() > 0)
			return true;
		for(int i = 0; i < inventoryStacks.length - 1; i++)
			if(inventoryStacks[i] != null)
				return true;
		return false;
	}

	@Override
	protected void onStartProcess() {
		// Take one piece of fuel out of the first slot that has fuel
		for(int i = 0; i < 9; i++) {
			if(inventoryStacks[i] != null) {
				ticksToProcess = (int)(TileEntityFurnace.getItemBurnTime(inventoryStacks[i]) * ESU_TO_FURNACE_TICK_RATIO);
				decrStackSize(i, 1);
				break;
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		currentRK = tagCompound.getInteger("currentRK");
		ticksToProcess = tagCompound.getInteger("ticksToProcess");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("currentRK", currentRK);
		tagCompound.setInteger("ticksToProcess", ticksToProcess);
	}

	@Override
	public Object[] getSyncDataToClient() {
		return ArrayUtils.addAll(super.getSyncDataToClient(), ticksToProcess, currentRK);
	}

	public void handlePacketDataFromServer(int ticksToProcess, int currentRK) {
		this.ticksToProcess = ticksToProcess;
		this.currentRK = currentRK;
	}

	/** Will the unit support the specified change in RK, i.e. if changeInRK is added to currentRK, will the result be less than zero or overflow the machine? If
	 * this condition is true, make said change, i.e. actually add changeInRK to currentRK
	 * 
	 * @param changeInRK the specified change in RK
	 * @return True if changeInRK plus currentRK is between 0 and maxRK, False otherwise */
	public boolean changeRK(int changeInRK) {
		if(0 <= currentRK + changeInRK && currentRK + changeInRK <= maxRK) {
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
		if(hasUpgrade(MachineHelper.CAPACITY2))
			maxRK = 400000000; // 400,000,000 (400 million)
		else if(hasUpgrade(MachineHelper.CAPACITY1))
			maxRK = 200000000; // 200,000,000 (200 million)
		else
			maxRK = 100000000; // 100,000,000 (100 million)

		if(hasUpgrade(MachineHelper.RANGE2))
			range = 60;
		else if(hasUpgrade(MachineHelper.RANGE1))
			range = 45;
		else
			range = 30;

		if(hasUpgrade(MachineHelper.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(MachineHelper.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(MachineHelper.EFFICIENCY2))
			rkPerTickMult = 2.0F;
		else if(hasUpgrade(MachineHelper.EFFICIENCY1))
			rkPerTickMult = 1.5F;
		else
			rkPerTickMult = 1.0F;

		if(hasUpgrade(MachineHelper.CAPACITY2))
			stackLimit = 64;
		else if(hasUpgrade(MachineHelper.CAPACITY1))
			stackLimit = 48;
		else
			stackLimit = 32;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(MachineHelper.SPEED1);
		validUpgrades.add(MachineHelper.SPEED2);
		validUpgrades.add(MachineHelper.EFFICIENCY1);
		validUpgrades.add(MachineHelper.EFFICIENCY2);
		validUpgrades.add(MachineHelper.CAPACITY1);
		validUpgrades.add(MachineHelper.CAPACITY2);
		validUpgrades.add(MachineHelper.RANGE1);
		validUpgrades.add(MachineHelper.RANGE2);
		validUpgrades.add(MachineHelper.WIRELESS);
	}
}
