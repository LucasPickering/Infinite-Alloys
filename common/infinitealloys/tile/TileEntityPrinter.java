package infinitealloys.tile;

import net.minecraft.item.ItemStack;

public class TileEntityPrinter extends TileEntityMachine {

	public TileEntityPrinter(int facing) {
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
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return super.isItemValidForSlot(slot, itemstack) || TEHelper.stackValidForSlot(TEHelper.PRINTER, slot, itemstack);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(inventoryStacks[0] != null && inventoryStacks[1] != null && inventoryStacks[2] == null)
			processProgress = 0;
	}

	@Override
	public boolean shouldProcess() {
		return inventoryStacks[0] != null && inventoryStacks[1] != null && inventoryStacks[2] == null;
	}

	@Override
	public void finishProcessing() {
		inventoryStacks[2] = inventoryStacks[0].copy();
		inventoryStacks[1].stackSize--;
		if(inventoryStacks[1].stackSize == 0)
			inventoryStacks[1] = null;
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
			baseRKPerTick = -180;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			baseRKPerTick = -270;
		else
			baseRKPerTick = -360;

		canNetwork = hasUpgrade(TEHelper.WIRELESS);
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
