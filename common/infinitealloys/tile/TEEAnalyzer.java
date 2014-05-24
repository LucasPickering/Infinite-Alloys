package infinitealloys.tile;

import infinitealloys.network.PacketClient;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TEEAnalyzer extends TileEntityElectric implements IHost {

	/** 3D coords for each metal forge that is connected to this analyzer */
	public final List<Point> connectedMachines = new ArrayList<Point>();

	/** A binary integer that represents all the alloys that this machine has discovered, this works the same way as the upgrade int */
	private int alloys;

	/** A binary integer that represents the metals that were consumed and are currently being processed. This is used to compute how much energy to use. */
	private short usedMetals;

	/** The damage value of the alloy that is going to be made from the current process */
	private int targetAlloy = -1;

	/** A list of clients currently connected to this energy network */
	private ArrayList<Point> networkClients = new ArrayList<Point>();

	public TEEAnalyzer(byte front) {
		this();
		this.front = front;
	}

	public TEEAnalyzer() {
		super(Consts.METAL_COUNT + 1);
		stackLimit = 1;
		baseRKPerTick = -1000;
		ticksToProcess = 20; // TODO: Change this back to 2400
	}

	@Override
	public int getID() {
		return MachineHelper.ANALYZER;
	}

	@Override
	public void deleteNetwork() {
		for(Point client : networkClients)
			removeClient(client, true);
	}

	@Override
	public boolean isClientValid(Point client) {
		return Funcs.getBlockTileEntity(worldObj, client) instanceof TEEMetalForge;
	}

	@Override
	public boolean addClient(EntityPlayer player, Point client, boolean sync) {
		if(networkClients.contains(client)) {
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Error: Machine is already in this network");
		}
		else if(!isClientValid(client)) {
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Error: Machine is not a metal forge");
		}
		else {
			// Add the machine
			networkClients.add(client);
			((TEEMetalForge)Funcs.getBlockTileEntity(worldObj, client)).analyzerHost = coords();

			// Sync the data to the server/all clients
			if(worldObj.isRemote) {
				if(player != null)
					player.addChatMessage("Adding machine at " + client);
				if(sync)
					PacketDispatcher.sendPacketToServer(PacketClient.getPacket(true, worldObj.provider.dimensionId, coords(), client));
			}
			else if(sync)
				PacketDispatcher.sendPacketToAllInDimension(PacketClient.getPacket(true, worldObj.provider.dimensionId, coords(), client), worldObj.provider.dimensionId);

			return true;
		}
		return false;
	}

	@Override
	public void removeClient(Point client, boolean sync) {
		((TEEMetalForge)Funcs.getBlockTileEntity(worldObj, client)).disconnectFromAnalyzerNetwork();
		networkClients.remove(client);
		if(sync) {
			if(worldObj.isRemote)
				PacketDispatcher.sendPacketToServer(PacketClient.getPacket(false, worldObj.provider.dimensionId, coords(), client));
			else
				PacketDispatcher.sendPacketToAllInDimension(PacketClient.getPacket(false, worldObj.provider.dimensionId, coords(), client), worldObj.provider.dimensionId);
		}
	}

	@Override
	public void syncAllClients(Player player) {
		for(Point client : networkClients)
			PacketDispatcher.sendPacketToPlayer(PacketClient.getPacket(true, worldObj.provider.dimensionId, coords(), client), player);
	}

	@Override
	public int getNetworkSize() {
		return networkClients.size();
	}

	@Override
	public boolean shouldProcess() {
		return targetAlloy >= 0 || getAlloyForMetals() >= 0; // Return true if we are already processing or we are ready for a new process
	}

	@Override
	protected void onStartProcess() {
		targetAlloy = getAlloyForMetals(); // Set the alloy that we are looking for
		for(int i = 0; i < Consts.METAL_COUNT; i++) { // Remove the ingots that are in the inventory
			if(inventoryStacks[i] != null) {
				usedMetals |= 1 << i;
				decrStackSize(i, 1);
			}
		}
	}

	@Override
	protected void onFinishProcess() {
		alloys |= 1 << targetAlloy; // Add the alloy that we discovered to the alloys that have been discovered
		targetAlloy = -1; // Reset the alloy that we are discovering
		if(Funcs.isServer())
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public int getAlloys() {
		return alloys;
	}

	/** Has the alloy with the given index been discovered? */
	public boolean hasAlloy(int alloy) {
		return (alloys >> alloy & 1) == 1;
	}

	/** Get the alloy that best fits the metals that are currently in the machine. This will return the ID of the alloy that uses the most of the the metals.
	 * If the alloy has already been discovered, it will not be returned. */
	private int getAlloyForMetals() {
		int bestAlloy = -1; // The index of the best match
		int mostCorrectMetals = 0;
		alloys:
		for(EnumAlloy alloy : EnumAlloy.values()) {
			if(!hasAlloy(alloy.ordinal())) {
				int correctMetals = 0;
				for(int i = 0; i < Consts.METAL_COUNT; i++) {
					boolean isMetalInAnalyzer = inventoryStacks[i] != null; // Is this metal currently in the analyzer? Check the inventory for it.
					boolean isMetalInAlloy = Funcs.intAtPos(alloy.getAlloy(), Consts.ALLOY_RADIX, i) != 0; // Is this metal used in the alloy?

					if(isMetalInAlloy) {
						if(!isMetalInAnalyzer)
							continue alloys; // If this alloy requires a metal that the analyzer doesn't have, skip to the next alloy
						correctMetals++; // If the presence of this metal in the analyzer matches its presence in the alloy,
					}
				}
				if(correctMetals > mostCorrectMetals) {
					mostCorrectMetals = correctMetals;
					bestAlloy = alloy.getID();
				}
			}
		}
		return bestAlloy;
	}

	@Override
	public int getRKChange() {
		int metalModifier = 0;
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			if((usedMetals >> i & 1) == 1)
				metalModifier += Math.pow(4, i); // Each alloy contributes to the required RK based on its value
		return (int)(baseRKPerTick * rkPerTickMult / processTimeMult) * metalModifier;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		alloys = tagCompound.getInteger("alloys");
		targetAlloy = tagCompound.getInteger("targetAlloy");
		for(int i = 0; tagCompound.hasKey("client" + i); i++) {
			int[] client = tagCompound.getIntArray("client" + i);
			networkClients.add(new Point(client[0], client[1], client[2]));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("alloys", alloys);
		tagCompound.setInteger("targetAlloy", targetAlloy);
		for(int i = 0; i < networkClients.size(); i++) {
			Point client = networkClients.get(i);
			tagCompound.setIntArray("client" + i, new int[] { client.x, client.y, client.z });
		}
	}

	@Override
	public Object[] getSyncDataToClient() {
		final List<Object> coords = new ArrayList<Object>();
		for(final Point point : connectedMachines) {
			coords.add(point.x);
			coords.add((short)point.y);
			coords.add(point.z);
		}
		return ArrayUtils.addAll(super.getSyncDataToClient(), alloys, targetAlloy, (byte)connectedMachines.size(), coords.toArray());
	}

	public void handlePacketDataFromClient(int alloys, int targetAlloy) {
		this.alloys = alloys;
		this.targetAlloy = targetAlloy;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(EnumUpgrade.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(EnumUpgrade.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(EnumUpgrade.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(EnumUpgrade.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 0.001F; // TODO: Change this back to 1.0F
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(EnumUpgrade.SPEED1);
		validUpgrades.add(EnumUpgrade.SPEED2);
		validUpgrades.add(EnumUpgrade.EFFICIENCY1);
		validUpgrades.add(EnumUpgrade.EFFICIENCY2);
		validUpgrades.add(EnumUpgrade.WIRELESS);
	}
}
