package infinitealloys.tile;

import net.minecraft.item.ItemStack;

public class TileEntityRKStorage extends TileEntityUpgradable {

	/** The maximum amount of RK that this machine can store */
	private int maxRK = 10000000;

	/** The distance over which energy can be added to and taken from this machine */
	private int range;

	public TileEntityRKStorage(int facing) {
		this();
		front = facing;
	}

	public TileEntityRKStorage() {
		super();
		inventoryStacks = new ItemStack[1];
	}

	@Override
	public String getInvName() {
		return "RK Storage"; // TODO: Change this one I figure out a better name for this TE
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.CAPACITY2))
			maxRK = 40000000;
		else if(hasUpgrade(TEHelper.CAPACITY1))
			maxRK = 20000000;
		else
			maxRK = 10000000;

		if(hasUpgrade(TEHelper.RANGE2))
			range = 20;
		else if(hasUpgrade(TEHelper.RANGE1))
			range = 15;
		else
			range = 10;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(TEHelper.CAPACITY1);
		validUpgrades.add(TEHelper.CAPACITY2);
		validUpgrades.add(TEHelper.RANGE1);
		validUpgrades.add(TEHelper.RANGE2);
	}
}
