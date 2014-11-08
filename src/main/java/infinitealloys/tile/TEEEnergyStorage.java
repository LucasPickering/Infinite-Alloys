package infinitealloys.tile;

import infinitealloys.item.IAItems;
import infinitealloys.network.MessageNetworkEditToClient;
import infinitealloys.network.MessageNetworkEditToServer;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.ArrayUtils;

public class TEEEnergyStorage extends TileEntityElectric implements IHost {

	/** The maximum amount of RK that this machine can store */
	private int maxRK;

	/** The amount of RK currently stored in the unit */
	private int currentRK;

	/** The max range that machines can be added at with the Internet Wand */
	public int range;

	/** The ratio between how much RK an item produces and how long it will burn in a furnace. furnace is numerator, ESU is denominator. */
	private final float FURNACE_TO_ESU__RATIO = 0.18F;

	/** A list of clients currently connected to this energy network */
	private final ArrayList<Point> networkClients = new ArrayList<Point>();

	/** False until the first call of {@link #updateEntity()} */
	private boolean initialized;

	public TEEEnergyStorage(byte front) {
		this();
		this.front = front;
	}

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
		if(energyHost == null)
			energyHost = coords();

		if(!initialized) {
			initialized = true;
			if(!worldObj.isRemote)
				for(Point client : networkClients)
					((TileEntityElectric)Funcs.getTileEntity(worldObj, client)).connectToEnergyNetwork(coords());
		}

		super.updateEntity();
	}

	@Override
	public void connectToEnergyNetwork(Point host) {
		if(!worldObj.isRemote)
			deleteNetwork();
		super.connectToEnergyNetwork(host);
	}

	@Override
	public void deleteNetwork() {
		for(Point client : networkClients) {
			TileEntity te = Funcs.getTileEntity(worldObj, client);
			if(te instanceof TileEntityElectric) {
				((TileEntityElectric)te).disconnectFromEnergyNetwork();
			}
		}
	}

	@Override
	public boolean isClientValid(Point client) {
		return Funcs.getTileEntity(worldObj, client) instanceof TileEntityElectric;
	}

	@Override
	public boolean addClient(EntityPlayer player, Point client, boolean sync) {
		if(energyHost != null && !energyHost.equals(coords())) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.error", "/: ", "machine.textOutput.error.notHosting")));
		}
		else if(initialized && !isClientValid(client)) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.error", "/: ", "machine.textOutput.error.notElectric")));
		}
		else if(networkClients.contains(client)) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.error", "/: ", "machine.textOutput.error.alreadyInNetwork")));
		}
		else if(client.equals(xCoord, yCoord, zCoord)) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.error", "/: ", "machine.textOutput.error.cannotAddSelf")));
		}
		else if(client.distanceTo(xCoord, yCoord, zCoord) > range) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.error", "/: ", "machine.textOutput.error.outOfRange")));
		}
		else {
			networkClients.add(client); // Add the machine

			if(initialized)
				((TileEntityElectric)Funcs.getTileEntity(worldObj, client)).connectToEnergyNetwork(coords()); // Tell the client machine to connect

			// Sync the data to the server/all clients
			if(sync) { // If we should sync
				if(worldObj.isRemote) { // If this is the client
					Funcs.sendPacketToServer(new MessageNetworkEditToServer(true, worldObj.provider.dimensionId, coords(), client)); // Sync to server
					if(player != null)
						player.addChatComponentMessage(new ChatComponentText(Funcs.getLoc("machine.textOutput.addingMachine") + client)); // Send a chat message
				}
				else
					Funcs.sendPacketToAllPlayers(new MessageNetworkEditToClient(true, worldObj.provider.dimensionId, coords(), client)); // Sync to clients
			}

			return true;
		}
		return false;
	}

	@Override
	public void removeClient(Point client, boolean sync) {
		TileEntity te = Funcs.getTileEntity(worldObj, client);
		if(te instanceof TileEntityElectric)
			((TileEntityElectric)te).disconnectFromEnergyNetwork();
		networkClients.remove(client);
		if(sync) {
			if(worldObj.isRemote)
				Funcs.sendPacketToServer(new MessageNetworkEditToServer(false, worldObj.provider.dimensionId, coords(), client));
			else
				Funcs.sendPacketToAllPlayers(new MessageNetworkEditToClient(false, worldObj.provider.dimensionId, coords(), client));
		}
	}

	@Override
	public void syncAllClients(EntityPlayer player) {
		for(Point client : networkClients)
			Funcs.sendPacketToPlayer(new MessageNetworkEditToClient(true, worldObj.provider.dimensionId, coords(), client), player);
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
	public void onInventoryChanged() {
		// Set baseRKPerTick based on the first fuel in the supply slots
		for(int i = 0; i < 9; i++) {
			if(inventoryStacks[i] != null) {
				baseRKPerTick = (int)(TileEntityFurnace.getItemBurnTime(inventoryStacks[i]) * FURNACE_TO_ESU__RATIO);
				break;
			}
		}
	}

	@Override
	protected void onStartProcess() {
		// Take one piece of fuel out of the first slot that has fuel
		for(int i = 0; i < 9; i++) {
			if(inventoryStacks[i] != null) {
				decrStackSize(i, 1);
				break;
			}
		}
	}

	@Override
	protected NBTTagCompound getDropTagCompound() {
		if(currentRK != 0) {
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
		for(int i = 0; tagCompound.hasKey("client" + i); i++) {
			int[] client = tagCompound.getIntArray("client" + i);
			addClient(null, new Point(client[0], client[1], client[2]), false);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("currentRK", currentRK);
		tagCompound.setInteger("baseRKPerTick", baseRKPerTick);
		for(int i = 0; i < networkClients.size(); i++) {
			Point client = networkClients.get(i);
			tagCompound.setIntArray("client" + i, new int[] { client.x, client.y, client.z });
		}
	}

	@Override
	public Object[] getSyncDataToClient() {
		return ArrayUtils.addAll(super.getSyncDataToClient(), currentRK, baseRKPerTick);
	}

	public void handlePacketDataFromServer(int currentRK, int baseRKPerTick) {
		this.currentRK = currentRK;
		this.baseRKPerTick = baseRKPerTick;
	}

	@Override
	public void onNeighborChange(int x, int y, int z) {
		TileEntity te = worldObj.getTileEntity(x, y, z);
		if(initialized && te instanceof TileEntityElectric && ((TileEntityElectric)te).energyHost == null)
			addClient(null, new Point(x, y, z), false);
	}

	/** Will the unit support the specified change in RK, i.e. if changeInRK is added to currentRK, will the result be between zero and the machine's capacity? If
	 * this condition is true, make the change, i.e. add changeInRK to currentRK
	 *
	 * @param changeInRK the specified change in RK
	 * @return true if changeInRK plus currentRK is between 0 and maxRK, False otherwise */
	public boolean changeRK(int changeInRK) {
		if(currentRK + changeInRK >= 0 && currentRK + changeInRK <= maxRK) {
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
		float[] speedUpgradeValues = { 1F, 0.83F, 0.67F, 0.5F };
		processTimeMult = speedUpgradeValues[getUpgradeTier(Consts.SPEED)];

		float[] efficiencyUpgradeValues = { 1F, 1.33F, 1.67F, 2F };
		rkPerTickMult = efficiencyUpgradeValues[getUpgradeTier(Consts.EFFICIENCY)];

		int[] capacityUpgradeValues = { 100000000, 200000000, 300000000, 400000000 };
		maxRK = capacityUpgradeValues[getUpgradeTier(Consts.CAPACITY)];

		int[] rangeUpgradeValues = { 30, 40, 50, 60 };
		range = rangeUpgradeValues[getUpgradeTier(Consts.RANGE)];
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgradeTypes.add(IAItems.upgrades[Consts.SPEED]);
		validUpgradeTypes.add(IAItems.upgrades[Consts.EFFICIENCY]);
		validUpgradeTypes.add(IAItems.upgrades[Consts.CAPACITY]);
		validUpgradeTypes.add(IAItems.upgrades[Consts.RANGE]);
		validUpgradeTypes.add(IAItems.upgrades[Consts.WIRELESS]);
	}
}
