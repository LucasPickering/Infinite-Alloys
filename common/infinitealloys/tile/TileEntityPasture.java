package infinitealloys.tile;

import java.util.Arrays;
import infinitealloys.util.Consts;
import net.minecraft.item.ItemStack;

public class TileEntityPasture extends TileEntityMachine {

	/** The the mode value for turning the machine off */
	public static final int MODE_OFF = 0;
	/** The mode value for only trapping animals */
	public static final int MODE_ANIMALS = 1;
	/** The mode value for only repelling monsters */
	public static final int MODE_MONSTERS = 2;
	/** The mode value for both trapping animals and repelling monsters */
	public static final int MODE_BOTH = 3;
	/** The machines mode: 0 is off, 1 is animal-trapping only, 2 is monster-repulsion only, 3 is both (requires upgrade) */
	public int mode;
	/** Whether or not to trap each animal in the order of chicken, cow, pig, sheep */
	public boolean[] animals = new boolean[Consts.PASTURE_ANIMALS];
	/** Whether or not to repel each monster in the order of creeper, skeleton, spider, zombie */
	public boolean[] monsters = new boolean[Consts.PASTURE_MONSTERS];
	private int maxSpots;
	private int maxRange;

	public TileEntityPasture(int facing) {
		this();
		front = facing;
	}

	public TileEntityPasture() {
		super(0);
		inventoryStacks = new ItemStack[1];
	}

	@Override
	public String getInvName() {
		return "Pasture";
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return super.isItemValidForSlot(slot, itemstack) || TEHelper.stackValidForSlot(TEHelper.PASTURE, slot, itemstack);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
	}

	@Override
	public boolean shouldProcess() {
		return false;
	}

	@Override
	public void finishProcessing() {}

	@Override
	public int getJoulesUsed() {
		if(shouldProcess())
			return joulesUsedPerTick;
		return 0;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.EFFICIENCY2))
			joulesUsedPerTick = 180;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			joulesUsedPerTick = 270;
		else
			joulesUsedPerTick = 360;

		if(hasUpgrade(TEHelper.CAPACITY2))
			maxSpots = 8;
		else if(hasUpgrade(TEHelper.CAPACITY1))
			maxSpots = 4;
		else
			maxSpots = 2;

		if(hasUpgrade(TEHelper.RANGE2))
			maxRange = 15;
		else if(hasUpgrade(TEHelper.RANGE1))
			maxRange = 10;
		else
			maxRange = 5;

		canNetwork = hasUpgrade(TEHelper.WIRELESS);

		if(hasUpgrade(TEHelper.ELECCAPACITY2))
			setMaxEnergyStored(1000000);
		else if(hasUpgrade(TEHelper.ELECCAPACITY1))
			setMaxEnergyStored(750000);
		else
			setMaxEnergyStored(500000);
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(TEHelper.EFFICIENCY1);
		validUpgrades.add(TEHelper.EFFICIENCY2);
		validUpgrades.add(TEHelper.CAPACITY1);
		validUpgrades.add(TEHelper.CAPACITY2);
		validUpgrades.add(TEHelper.RANGE1);
		validUpgrades.add(TEHelper.RANGE2);
		validUpgrades.add(TEHelper.WIRELESS);
		validUpgrades.add(TEHelper.ELECCAPACITY1);
		validUpgrades.add(TEHelper.ELECCAPACITY2);
	}

	/** Does the pasture have enough space to enable another animal or monster
	 * 
	 * @return true if there is enough space to enable another animal or monster */
	public boolean hasFreeSpots() {
		int usedSpots = 0;
		for(boolean animal : animals)
			if(animal)
				usedSpots++;
		for(boolean monster : monsters)
			if(monster)
				usedSpots++;
		return usedSpots < maxSpots;
	}
}
