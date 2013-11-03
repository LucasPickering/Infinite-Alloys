package infinitealloys.tile;

import net.minecraft.item.ItemStack;

public class TEMGenerator extends TileEntityMachine {

	private final float FURNACE_FUEL_TO_RK_RATIO = 9 / 25;

	// ---BEGIN GENERAL FUNCTIONS---

	public TEMGenerator(int facing) {
		this();
		front = facing;
	}

	public TEMGenerator() {
		super(0);
		inventoryStacks = new ItemStack[10];
		baseRKPerTick = 36;
		ticksToProcess = 12800;
	}

	@Override
	protected boolean shouldProcess() {
		for(int i = 1; i < inventoryStacks.length; i++)
			if(inventoryStacks[i] != null)
				return true;
		return false;
	}

	@Override
	protected void startProcess() {
		for(int i = 1; i < inventoryStacks.length; i++)
			if(inventoryStacks[i] != null)
				decrStackSize(i, 1);
	}

	public int getRKChange() {
		if(shouldProcess())
			return (int)(baseRKPerTick * rkPerTickMult / processTimeMult * FURNACE_FUEL_TO_RK_RATIO);
		return 0;
	}

	// ---END GENERAL FUNCTIONS
	// ---BEGIN INVENTORY FUNCTIONS

	@Override
	public String getInvName() {
		return "Generator";
	}

	// ---END INVENTORY FUNCTIONS
	// ---BEGIN UPGRADE FUNCTIONS---

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(TEHelper.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(TEHelper.EFFICIENCY2))
			rkPerTickMult = 2.0F;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			rkPerTickMult = 1.5F;
		else
			rkPerTickMult = 1.0F;

		if(hasUpgrade(TEHelper.CAPACITY2))
			stackLimit = 64;
		else if(hasUpgrade(TEHelper.CAPACITY1))
			stackLimit = 48;
		else
			stackLimit = 32;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(TEHelper.SPEED1);
		validUpgrades.add(TEHelper.SPEED2);
		validUpgrades.add(TEHelper.EFFICIENCY1);
		validUpgrades.add(TEHelper.EFFICIENCY2);
		validUpgrades.add(TEHelper.CAPACITY1);
		validUpgrades.add(TEHelper.CAPACITY2);
		validUpgrades.add(TEHelper.WIRELESS);
	}

	// ---END UPGRADE FUNCTIONS---
}
