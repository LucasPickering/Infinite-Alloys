package infinitealloys;

import net.minecraft.src.ItemStack;

public class TileEntityPrinter extends TileEntityMachine {

	private final int ticksToPrint = 20;
	private int printProgress;

	public TileEntityPrinter(byte facing) {
		this();
		orientation = facing;
	}

	public TileEntityPrinter() {
		super(3);
		inventoryStacks = new ItemStack[4];
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(inventoryStacks[0] != null && inventoryStacks[1] != null && inventoryStacks[2] == null) {
			printProgress++;
			if(printProgress >= ticksToPrint) {
				inventoryStacks[2] = inventoryStacks[0];
				inventoryStacks[1].stackSize--;
			}
		}
	}

	@Override
	public String getInvName() {
		return "Printer";
	}

	@Override
	protected void updateUpgrades() {}
}
