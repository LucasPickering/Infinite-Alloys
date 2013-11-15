package infinitealloys.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TEEGenerator extends TileEntityElectric {

	/** The ratio between how long an item will burn in a furnace and how long it will burn in the generator. Furnace is numerator, generator is demoninator. */
	private final float FURNACE_TO_GENERATOR_TICK_RATIO = 2.0F;

	// ---BEGIN GENERAL FUNCTIONS---

	public TEEGenerator(int facing) {
		this();
		front = facing;
	}

	public TEEGenerator() {
		super(0);
		inventoryStacks = new ItemStack[10];
		baseRKPerTick = 72;
	}

	@Override
	public int getID() {
		return MachineHelper.GENERATOR;
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
		// Take one piece of fuel out of the first slot that has fuel
		for(int i = 1; i < inventoryStacks.length; i++) {
			if(inventoryStacks[i] != null) {
				ticksToProcess = (int)(TileEntityFurnace.getItemBurnTime(inventoryStacks[i]) * FURNACE_TO_GENERATOR_TICK_RATIO);
				decrStackSize(i, 1);
			}
		}
	}

	// ---END GENERAL FUNCTIONS
	// ---BEGIN UPGRADE FUNCTIONS---

	@Override
	protected void updateUpgrades() {
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
		validUpgrades.add(MachineHelper.WIRELESS);
	}

	// ---END UPGRADE FUNCTIONS---
}
