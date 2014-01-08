package infinitealloys.tile;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TEEAnalyzer extends TileEntityElectric {

	/** The amount of alloys that this machine has unlocked. This can be increased by adding an alloy book containing more recipes than this machine already
	 * knows. */
	private byte unlockedAlloyCount;

	/** A boolean for each metal telling whether or not an ingot of that metal is required to being searching for the next alloy */
	private final boolean[] requiredMetals = new boolean[Consts.METAL_COUNT];

	public TEEAnalyzer(byte front) {
		this();
		this.front = front;
	}

	public TEEAnalyzer() {
		super(9);
		stackLimit = 1;
		baseRKPerTick = -1000;
		ticksToProcess = 2400;
		updateRequiredMetals();
	}

	@Override
	public int getID() {
		return MachineHelper.ANALYZER;
	}

	@Override
	public boolean shouldProcess() {
		// If we've run out of alloys to discover, don't process
		if(unlockedAlloyCount >= Consts.VALID_ALLOY_COUNT)
			return false;

		// Otherwise, if we're not already processing, check for the alloys that we need to start a new process
		if(getProcessProgress() <= 0)
			for(int i = 0; i < requiredMetals.length; i++)
				if(requiredMetals[i] && inventoryStacks[i] == null)
					return false;
		return true;
	}

	@Override
	protected void startProcess() {
		for(int i = 0; i < requiredMetals.length; i++)
			if(requiredMetals[i])
				decrStackSize(i, 1);
	}

	@Override
	protected void finishProcess() {
		// Increment the amount of alloys we've discovered (we just found a new one)
		unlockedAlloyCount++;

		// Update the required time and metals to fit the next alloy
		updateRequiredMetals();

		if(Funcs.isServer())
			for(final String player : playersUsing)
				PacketDispatcher.sendPacketToPlayer(getDescriptionPacket(), Funcs.getPlayerForUsername(player));
	}

	@Override
	public int getRKChange() {
		return (int)(baseRKPerTick * rkPerTickMult / processTimeMult * Math.pow(4D, unlockedAlloyCount)); // Every time an alloy is unlocked, it quadruples
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		unlockedAlloyCount = tagCompound.getByte("UnlockedAlloyCount");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setByte("UnlockedAlloyCount", unlockedAlloyCount);
	}

	@Override
	public Object[] getSyncDataToClient() {
		return ArrayUtils.addAll(super.getSyncDataToClient(), unlockedAlloyCount);
	}

	public void handlePacketDataFromClient(byte unlockedAlloyCount) {
		this.unlockedAlloyCount = unlockedAlloyCount;
	}

	public byte getUnlockedAlloyCount() {
		return unlockedAlloyCount;
	}

	private void updateRequiredMetals() {
		if(unlockedAlloyCount < Consts.VALID_ALLOY_COUNT)
			for(int i = 0; i < requiredMetals.length; i++)
				requiredMetals[i] = Funcs.intAtPos(EnumAlloy.values()[getUnlockedAlloyCount()].max, Consts.ALLOY_RADIX, i) > 0;
	}

	@Override
	public void onInventoryChanged() {
		// Is an alloy book in the book slot?
		if(inventoryStacks[8] != null) {
			// Does this book have a tag compound with alloys saved in it?
			final NBTTagCompound tagCompound = inventoryStacks[8].getTagCompound();
			if(tagCompound != null && tagCompound.hasKey("alloys"))
				// Set the amount of unlocked alloys to whichever is larger: itself, or the amount of alloys in the book
				// i.e. if the book has more alloys saved than the machine, teach those alloys to the machine
				unlockedAlloyCount = (byte)Math.max(unlockedAlloyCount, tagCompound.getIntArray("alloys").length);
		}
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
