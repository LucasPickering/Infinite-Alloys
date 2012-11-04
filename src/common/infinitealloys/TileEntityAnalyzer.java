package infinitealloys;

import java.util.ArrayList;
import java.util.Arrays;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public class TileEntityAnalyzer extends TileEntityMachine {

	/**
	 * Ticks it takes to finish analyzing one ingot
	 */
	public final int ticksToAnalyze = 20;

	/**
	 * The analyzing progress
	 */
	public int analysisProgress;

	/**
	 * The report of the last analysis
	 */
	public String alloyReport = "";

	/**
	 * Ticks since machine first started, only used for animation
	 */
	public int ticksSinceStart;

	/**
	 * Ticks since the machine stopped running, only used for animation
	 */
	public int ticksSinceFinish;

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
		if(inventoryStacks[0] != null && inventoryStacks[1] == null) {
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
		if(inventoryStacks[2] != null) {
			ArrayList<Integer> validAlloys = new ArrayList<Integer>();
			for(int validAlloy : References.validAlloys)
				validAlloys.add(validAlloy);
			int alloy = inventoryStacks[0].getTagCompound().getInteger("alloy");
			NBTTagCompound tagCompound;
			ArrayList<Integer> savedAlloys = new ArrayList<Integer>();
			if(inventoryStacks[2].hasTagCompound()) {
				tagCompound = inventoryStacks[2].getTagCompound();
				for(int savedAlloy : tagCompound.getIntArray("savedAlloys"))
					savedAlloys.add(savedAlloy);
			}
			else
				tagCompound = new NBTTagCompound();
			if(savedAlloys.size() < References.alloyBookMaxSaves || validAlloys.contains(alloy) && inventoryStacks[2] != null) {
				savedAlloys.add(alloy);
				inventoryStacks[2].setTagCompound(tagCompound);
			}
		}
		inventoryStacks[1] = inventoryStacks[0];
		inventoryStacks[0] = null;
		alloyReport = "I'm an idiot. Ask someone else. Actually, you know what? You're an idiot too. How could you expect me to answer that? Just go away before I cry.";
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

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	/**
	 * Updates the settings based on the speed, capacity, and efficiency
	 * upgrades.
	 */
	protected void updateUpgrades() {
		canNetwork = (upgrades & 256) == 256;
	}
}
