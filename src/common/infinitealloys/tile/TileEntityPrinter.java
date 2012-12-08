package infinitealloys.tile;

import net.minecraft.src.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityPrinter extends TileEntityMachine {

	public TileEntityPrinter(ForgeDirection facing) {
		this();
		front = facing;
	}

	public TileEntityPrinter() {
		super(3);
		inventoryStacks = new ItemStack[4];
	}

	@Override
	public String getInvName() {
		return "Printer";
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {
		if(side == ForgeDirection.UP)
			return 0;
		if(side == ForgeDirection.DOWN)
			return 2;
		return 1;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		return 1;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(inventoryStacks[0] != null && inventoryStacks[1] != null && inventoryStacks[2] == null) {
			if(joules >= joulesUsedPerTick) {
				joules -= joulesUsedPerTick;
				processProgress++;
				if(processProgress >= ticksToProcess) {
					inventoryStacks[2] = inventoryStacks[0].copy();
					inventoryStacks[1].stackSize--;
					if(inventoryStacks[1].stackSize == 0)
						inventoryStacks[1] = null;
					processProgress = 0;
				}
			}
		}
		else
			processProgress = 0;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.SPEED2))
			ticksToProcess = 100;
		else if(hasUpgrade(TEHelper.SPEED1))
			ticksToProcess = 150;
		else
			ticksToProcess = 200;

		if(hasUpgrade(TEHelper.EFFICIENCY2))
			joulesUsedPerTick = 180D;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			joulesUsedPerTick = 270D;
		else
			joulesUsedPerTick = 360D;

		canNetwork = hasUpgrade(TEHelper.WIRELESS);

		if(hasUpgrade(TEHelper.ELECCAPACITY2))
			maxJoules = 1000000D;
		else if(hasUpgrade(TEHelper.ELECCAPACITY1))
			maxJoules = 750000D;
		else
			maxJoules = 500000D;
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
}
