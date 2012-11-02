package infinitealloys;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntityFurnace;

public class TileEntityAnalyzer extends TileEntityMachine {

	/**
	 * Ticks it takes to finish analyzing one ingot
	 */
	public final int ticksToAnalyze = 2400;

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

	public TileEntityAnalyzer() {
		super(2);
		inventoryStacks = new ItemStack[3];
		orientation = 2;
	}

	@Override
	public String getInvName() {
		return "Analyzer";
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		analysisProgress = tagCompound.getShort("AnalysisProgress");
		ticksSinceStart = tagCompound.getInteger("TicksSinceStart");
		ticksSinceFinish = tagCompound.getInteger("TicksSinceFinish");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setShort("AnalysisProgress", (short)analysisProgress);
		tagCompound.setInteger("TicksSinceStart", ticksSinceStart);
		tagCompound.setInteger("TicksSinceFinish", ticksSinceFinish);
	}

	public void handlePacketDataFromServer(int analysisProgress, int ticksSinceStart, int ticksSinceFinish) {
		this.analysisProgress = analysisProgress;
		this.ticksSinceStart = ticksSinceStart;
		this.ticksSinceFinish = ticksSinceFinish;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		updateUpgrades();
		boolean invChanged = false;
		if(inventoryStacks[0] != null) {
			ticksSinceFinish = 0;
			ticksSinceStart++;
			analysisProgress++;
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

	private void analyzeItem() {

	}

	@SideOnly(Side.CLIENT)
	/**
	 * Get a scaled analysis progress, used for the gui progress bar
	 * @param i Scale
	 * @return Scaled progress
	 */
	public int getAnalysisProgressScaled(int i) {
		return analysisProgress * i / ticksToAnalyze;
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
