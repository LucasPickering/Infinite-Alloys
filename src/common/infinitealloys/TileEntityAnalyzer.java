package infinitealloys;

import java.util.ArrayList;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class TileEntityAnalyzer extends TileEntityMachine {

	/**
	 * Ticks it takes to finish analyzing one ingot
	 */
	public int ticksToAnalyze;

	/**
	 * The analyzing progress
	 */
	public int analysisProgress;

	public TileEntityAnalyzer(ForgeDirection facing) {
		this();
		front = facing;
	}

	public TileEntityAnalyzer() {
		super(3);
		inventoryStacks = new ItemStack[4];
		ticksToAnalyze = 3600;
		joulesUsedPerTick = 15;
	}

	@Override
	public String getInvName() {
		return "Analyzer";
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		analysisProgress = tagCompound.getShort("AnalysisProgress");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setShort("AnalysisProgress", (short)analysisProgress);
	}

	public void handlePacketDataFromServer(int analysisProgress) {
		this.analysisProgress = analysisProgress;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		boolean invChanged = false;
		if(inventoryStacks[0] != null && inventoryStacks[1] == null) {
			if(joules >= joulesUsedPerTick) {
				joules -= joulesUsedPerTick;
				analysisProgress++;
				if(analysisProgress >= ticksToAnalyze) {
					analysisProgress = 0;
					if(inventoryStacks[2] != null) {
						ArrayList<Integer> validAlloys = new ArrayList<Integer>();
						for(int validAlloy : InfiniteAlloys.instance.worldData.validAlloys)
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
					invChanged = true;
				}
			}
		}
		else
			analysisProgress = 0;
		if(invChanged)
			onInventoryChanged();
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
	public int getInventoryStackLimit() {
		return 1;
	}

	/**
	 * Updates the settings based on the speed, capacity, and efficiency
	 * upgrades.
	 */
	protected void updateUpgrades() {
		if(hasUpgrade(SPEED1))
			ticksToAnalyze = 2400;
		if(hasUpgrade(SPEED2))
			ticksToAnalyze = 3200;
		if(hasUpgrade(EFFICIENCY1))
			joulesUsedPerTick = 240;
		if(hasUpgrade(EFFICIENCY2))
			joulesUsedPerTick = 120;
		canNetwork = hasUpgrade(WIRELESS);
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(SPEED1);
		validUpgrades.add(SPEED2);
		validUpgrades.add(EFFICIENCY1);
		validUpgrades.add(EFFICIENCY2);
		validUpgrades.add(WIRELESS);
	}
}
