package infinitealloys;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntityFurnace;

public class TileEntityAnalyzer extends TileEntityMachine {

	/**
	 * The amount of ticks that the fuel in the slot will burn for
	 */
	public int currentFuelBurnTime;
	public int heatLeft;

	/**
	 * Ticks it takes to finish analyzing one ingot
	 */
	public int ticksToAnalyze = 2000;

	/**
	 * The analyzing progress
	 */
	public int analysisProgress;

	/**
	 * Ticks since machine first started, only used for animation
	 */
	public int ticksSinceStart;

	/**
	 * Ticks since the machine stopped running, only used for animation
	 */
	public int ticksSinceFinish;

	public TileEntityAnalyzer(int facing) {
		this();
		orientation = (byte)facing;
	}

	public TileEntityAnalyzer() {
		super(3);
		inventoryStacks = new ItemStack[4];
		orientation = 2;
	}

	@Override
	public String getInvName() {
		return "Analyzer";
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		analysisProgress = nbttagcompound.getShort("AnalysisProgress");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setShort("AnalysisProgress", (short)analysisProgress);
	}

	public void handlePacketDataFromServer(int currentFuelBurnTime, int heatLeft, int smeltProgress, byte[] recipeAmts) {}

	@Override
	public void updateEntity() {
		super.updateEntity();
		updateUpgrades();
		boolean invChanged = false;
		if(heatLeft <= 0) {
			currentFuelBurnTime = 0;
			if(inventoryStacks[0] != null)
				currentFuelBurnTime = TileEntityFurnace.getItemBurnTime(inventoryStacks[0]);
			if(shouldBurn()) {
				heatLeft = currentFuelBurnTime;
				invChanged = true;
				if(--inventoryStacks[0].stackSize <= 0)
					inventoryStacks[0] = null;
			}
		}
		if(shouldBurn()) {
			ticksSinceFinish = 0;
			ticksSinceStart++;
			analysisProgress++;
			heatLeft--;
			if(analysisProgress >= ticksToAnalyze) {
				analysisProgress = 0;
				analyzeItem();
				invChanged = true;
			}
		}
		else {
			ticksSinceStart = 0;
			ticksSinceFinish++;
			analysisProgress = 0;
		}
		BlockMachine.updateBlockState(worldObj, xCoord, yCoord, zCoord);
		if(invChanged)
			onInventoryChanged();
	}

	private boolean shouldBurn() {
		return (heatLeft > 0 || currentFuelBurnTime > 0) && (inventoryStacks[1] == null);
	}

	private void analyzeItem() {

	}

	@Override
	public boolean isUpgradeValid(ItemStack upgrade) {
		return super.isUpgradeValid(upgrade);
	}

	/**
	 * Updates the settings based on the speed, capacity, and efficiency
	 * upgrades.
	 */
	protected void updateUpgrades() {
		canNetwork = (upgrades & 256) == 256;
	}
}
