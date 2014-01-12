package infinitealloys.tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TEEAnalyzer extends TileEntityElectric implements IHost {

	/** 3D coords for each metal forge that is connected to this analyzer */
	public final List<Point> connectedMachines = new ArrayList<Point>();

	/** A binary integer that represents all the alloys that this machine has discovered, this works the same way as the upgrade int */
	private short alloys;

	/** A binary integer that represents the metals that were consumed and are currently being processed */
	private short usedMetals;

	public TEEAnalyzer(byte front) {
		this();
		this.front = front;
	}

	public TEEAnalyzer() {
		super(9);
		stackLimit = 1;
		baseRKPerTick = -1000;
		ticksToProcess = 2400;
	}

	@Override
	public int getID() {
		return MachineHelper.ANALYZER;
	}

	@Override
	public boolean shouldProcess() {
		return getAlloyForMetals() != 0;
	}

	@Override
	protected void onStartProcess() {
		for(int i = 0; i < Consts.METAL_COUNT; i++) {
			if(inventoryStacks != null) {
				usedMetals &= 1 << i;
				decrStackSize(i, 1);
			}
		}
	}

	@Override
	protected void onFinishProcess() {
		if(Funcs.isServer())
			for(final String player : playersUsing)
				PacketDispatcher.sendPacketToPlayer(getDescriptionPacket(), Funcs.getPlayerForUsername(player));
	}

	/** Has the alloy with the given index been discovered? */
	public boolean hasAlloy(short alloy) {
		return (alloys >> alloy & 1) == 1;
	}

	public short getAlloys() {
		return alloys;
	}

	/** Return an alloy that can be made using the metals that are currently in the machine. This will return the alloy that uses the most of the the metals. */
	private short getAlloyForMetals() {
		return 0;
	}

	@Override
	public int getRKChange() {
		int metalModifier = 0;
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			if((usedMetals >> i & 1) == 1)
				metalModifier += Math.pow(10, i); // Each alloy contributes to the required RK based on its value
		return (int)(baseRKPerTick * rkPerTickMult / processTimeMult) * metalModifier * 10;
	}

	@Override
	public boolean addMachine(EntityPlayer player, int machineX, int machineY, int machineZ) {

		// Machine is already connected
		for(final Point coords : connectedMachines)
			if(coords.x == machineX && coords.y == machineY && coords.z == machineZ)
				return false;

		// Machine is not a metal forge
		if(!(worldObj.getBlockTileEntity(machineX, machineY, machineZ) instanceof TEEMetalForge))
			return false;

		// Add the machine
		else {
			final TEEMetalForge temf = (TEEMetalForge)worldObj.getBlockTileEntity(machineX, machineY, machineZ);
			if(temf.energyStorage != null) { // If the machine is already connected to another storage unit, disconnect it from that
				for(final Iterator iterator = temf.energyStorage.connectedMachines.iterator(); iterator.hasNext();) {
					final Point p = (Point)iterator.next();
					if(p.equals(machineX, machineY, machineZ)) {
						iterator.remove();
						break;
					}
				}
				temf.energyStorage = null;
			}
			connectedMachines.add(new Point(machineX, machineY, machineZ));
			temf.analyzer = this;
			return true;
		}
	}

	@Override
	public void clearMachines() {
		connectedMachines.clear();
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		alloys = tagCompound.getShort("alloys");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setShort("alloys", alloys);
	}

	@Override
	public Object[] getSyncDataToClient() {
		return ArrayUtils.addAll(super.getSyncDataToClient(), alloys);
	}

	public void handlePacketDataFromClient(short alloys) {
		this.alloys = alloys;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(MachineHelper.SPEED2))
			processTimeMult = 1800;
		else if(hasUpgrade(MachineHelper.SPEED1))
			processTimeMult = 2700;
		else
			processTimeMult = 3600;

		if(hasUpgrade(MachineHelper.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(MachineHelper.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 1.0F;
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
