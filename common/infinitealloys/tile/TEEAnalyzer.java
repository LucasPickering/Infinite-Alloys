package infinitealloys.tile;

import infinitealloys.core.NetworkManager;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TEEAnalyzer extends TileEntityElectric implements IHost {

	/** 3D coords for each metal forge that is connected to this analyzer */
	public final List<Point> connectedMachines = new ArrayList<Point>();

	/** A binary integer that represents all the alloys that this machine has discovered, this works the same way as the upgrade int */
	private int alloys;

	/** A binary integer that represents the metals that were consumed and are currently being processed. This is used to compute how much energy to use. */
	private short usedMetals;

	/** The damage value of the alloy that is going to be made from the current process */
	private int targetAlloy = -1;

	/** The ID of the network hosted by this block that supplies alloy information to metal forges */
	private int analyzerNetworkID = -1;

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
	public void updateEntity() {
		if(analyzerNetworkID == -1)
			analyzerNetworkID = NetworkManager.buildNetwork(MachineHelper.ANALYZER_NETWORK, worldObj.provider.dimensionId, new Point(xCoord, yCoord, zCoord));

		super.updateEntity();
	}

	@Override
	public void connectToNetwork(int networkType, int networkID) {
		super.connectToNetwork(networkType, networkID);
		if(networkType == MachineHelper.ANALYZER_NETWORK)
			analyzerNetworkID = networkID;
	}

	@Override
	public void deleteNetworks() {
		NetworkManager.deleteNetwork(analyzerNetworkID);
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
		for(final EnumAlloy alloy : EnumAlloy.values()) {
			if(!hasAlloy(alloy.ordinal())) {
				int correctMetals = 0;
				for(int i = 0; i < Consts.METAL_COUNT; i++) {
					final boolean isMetalInAnalyzer = inventoryStacks[i] != null; // Is this metal currently in the analyzer? Check the inventory for it.
					final boolean isMetalInAlloy = Funcs.intAtPos(alloy.getAlloy(), Consts.ALLOY_RADIX, i) != 0; // Is this metal used in the alloy?

					if(isMetalInAlloy && !isMetalInAnalyzer)
						continue alloys; // If this alloy requires a metal that the analyzer doesn't have, skip to the next alloy
					if(isMetalInAlloy == isMetalInAnalyzer)
						correctMetals++; // If the presence of this metal in the analyzer matches its presence in the alloy,
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

	@SideOnly(Side.CLIENT)
	public int getAnalyzerNetworkID() {
		return analyzerNetworkID;
	}

	@Override
	public boolean isClientValid(Point client) {
		return Funcs.getBlockTileEntity(worldObj, client) instanceof TEEMetalForge;
	}

	@Override
	public boolean addClient(EntityPlayer player, Point client) {
		if(NetworkManager.hasClient(analyzerNetworkID, client)) {
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Error: Machine is already in this network");
		}
		else if(!isClientValid(client)) {
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Error: Machine is not a metal forge");
		}
		else {
			// Add the machine
			NetworkManager.addClient(analyzerNetworkID, client);
			if(player != null && worldObj.isRemote)
				player.addChatMessage("Adding machine at " + client.x + ", " + client.y + ", " + client.z);
			return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		alloys = tagCompound.getInteger("alloys");
		targetAlloy = tagCompound.getInteger("targetAlloy");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("alloys", alloys);
		tagCompound.setInteger("targetAlloy", targetAlloy);
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
		if(hasUpgrade(MachineHelper.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(MachineHelper.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(MachineHelper.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(MachineHelper.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 0.001F; // TODO: Change this back to 1.0F
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(MachineHelper.SPEED1);
		validUpgrades.add(MachineHelper.SPEED2);
		validUpgrades.add(MachineHelper.EFFICIENCY1);
		validUpgrades.add(MachineHelper.EFFICIENCY2);
		validUpgrades.add(MachineHelper.WIRELESS);
	}
}
