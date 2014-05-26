package infinitealloys.tile;

import infinitealloys.network.PacketClient;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
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
	private ArrayList<Point> networkClients = new ArrayList<Point>();

	public TEEEnergyStorage(byte front) {
		this();
		this.front = front;
	}

	public TEEEnergyStorage() {
		super(10);
		baseRKPerTick = 1;
		onInventoryChanged();
	}

	@Override
	public int getID() {
		return MachineHelper.ENERGY_STORAGE;
	}

	@Override
	public void updateEntity() {
		if(energyHost == null)
			energyHost = coords();

		super.updateEntity();
	}

	@Override
	public void onBlockDestroyed() {
		if(energyHost.equals(coords()))
			deleteNetwork();
		super.onBlockDestroyed();
	}

	@Override
	public void connectToEnergyNetwork(Point host) {
		deleteNetwork();
		super.connectToEnergyNetwork(host);
	}

	public void deleteNetwork() {
		for(Point client : networkClients) {
			TileEntity te = Funcs.getTileEntity(worldObj, client);
			if(te instanceof TileEntityElectric)
				((TileEntityElectric)te).disconnectFromEnergyNetwork();
		}
	}

	@Override
	public boolean isClientValid(Point client) {
		return Funcs.getTileEntity(worldObj, client) instanceof TileEntityElectric;
	}

	@Override
	public boolean addClient(EntityPlayer player, Point client, boolean sync) {
		if(!energyHost.equals(coords())) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText("Error: This machine is not currently hosting a network because it is connected to another host"));
		}
		else if(networkClients.contains(client)) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText("Error: Machine is already in network"));
		}
		else if(client.equals(xCoord, yCoord, zCoord)) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText("Error: Cannot add self to network"));
		}
		else if(client.distanceTo(xCoord, yCoord, zCoord) > range) {
			if(player != null && worldObj.isRemote)
				player.addChatComponentMessage(new ChatComponentText("Error: Block out of range"));
		}
		else {
			// Add the machine
			networkClients.add(client);
			((TileEntityElectric)Funcs.getTileEntity(worldObj, client)).connectToEnergyNetwork(coords());

			// Sync the data to the server/all clients
			if(worldObj.isRemote) {
				if(player != null)
					player.addChatComponentMessage(new ChatComponentText("Adding machine at " + client));
				if(sync)
					Funcs.sendPacketToServer(new PacketClient(true, worldObj.provider.dimensionId, coords(), client));
			}
			else if(sync)
				Funcs.sendPacketToAllPlayers(new PacketClient(true, worldObj.provider.dimensionId, coords(), client));

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
				Funcs.sendPacketToServer(new PacketClient(false, worldObj.provider.dimensionId, coords(), client));
			else
				Funcs.sendPacketToAllPlayers(new PacketClient(false, worldObj.provider.dimensionId, coords(), client));
		}
	}

	@Override
	public void syncAllClients(EntityPlayer player) {
		for(Point client : networkClients)
			Funcs.sendPacketToPlayer(new PacketClient(true, worldObj.provider.dimensionId, coords(), client), player);
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
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		currentRK = tagCompound.getInteger("currentRK");
		for(int i = 0; tagCompound.hasKey("client" + i); i++) {
			int[] client = tagCompound.getIntArray("client" + i);
			networkClients.add(new Point(client[0], client[1], client[2]));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("currentRK", currentRK);
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

	/** Will the unit support the specified change in RK, i.e. if changeInRK is added to currentRK, will the result be between zero and the machine's capacity? If
	 * this condition is true, make the change, i.e. add changeInRK to currentRK
	 * 
	 * @param changeInRK the specified change in RK
	 * @return true if changeInRK plus currentRK is between 0 and maxRK, False otherwise */
	public boolean changeRK(int changeInRK) {
		if(currentRK + changeInRK > 0 && currentRK + changeInRK <= maxRK) {
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
		if(hasUpgrade(EnumUpgrade.CAPACITY2))
			maxRK = 400000000; // 400 million
		else if(hasUpgrade(EnumUpgrade.CAPACITY1))
			maxRK = 200000000; // 200 million
		else
			maxRK = 100000000; // 100 million

		if(hasUpgrade(EnumUpgrade.RANGE2))
			range = 60;
		else if(hasUpgrade(EnumUpgrade.RANGE1))
			range = 45;
		else
			range = 30;

		if(hasUpgrade(EnumUpgrade.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(EnumUpgrade.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(EnumUpgrade.EFFICIENCY2))
			rkPerTickMult = 2.0F;
		else if(hasUpgrade(EnumUpgrade.EFFICIENCY1))
			rkPerTickMult = 1.5F;
		else
			rkPerTickMult = 1.0F;

		if(hasUpgrade(EnumUpgrade.CAPACITY2))
			stackLimit = 64;
		else if(hasUpgrade(EnumUpgrade.CAPACITY1))
			stackLimit = 48;
		else
			stackLimit = 32;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(EnumUpgrade.SPEED1);
		validUpgrades.add(EnumUpgrade.SPEED2);
		validUpgrades.add(EnumUpgrade.EFFICIENCY1);
		validUpgrades.add(EnumUpgrade.EFFICIENCY2);
		validUpgrades.add(EnumUpgrade.CAPACITY1);
		validUpgrades.add(EnumUpgrade.CAPACITY2);
		validUpgrades.add(EnumUpgrade.RANGE1);
		validUpgrades.add(EnumUpgrade.RANGE2);
		validUpgrades.add(EnumUpgrade.WIRELESS);
	}
}
