package infinitealloys.tile;

import infinitealloys.item.IAItems;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;

public class TEEMetalForge extends TileEntityElectric {

	/** The ID of the alloy that is currently set as the recipe. This is drawn from the connected analyzer. */
	public byte recipeAlloyID = -1;

	/** True if the alloy recipe has been changed by the client, used to reset progress */
	private boolean recipeChanged;

	/** The coordinates of the analyzer that is providing alloy data to this machine */
	public Point analyzerHost;

	public TEEMetalForge(byte front) {
		this();
		this.front = front;
	}

	public TEEMetalForge() {
		super(20);
		baseRKPerTick = -18;
	}

	@Override
	public EnumMachine getEnumMachine() {
		return EnumMachine.METAL_FORGE;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		recipeChanged = false;

		if(analyzerHost == null)
			recipeAlloyID = -1;
	}

	@Override
	public void onBlockDestroyed() {
		super.onBlockDestroyed();
		if(analyzerHost != null)
			((IHost)Funcs.getTileEntity(worldObj, analyzerHost)).removeClient(coords(), true);
	}

	public void connectToAnalyzerNetwork(Point host) {
		analyzerHost = host;
	}

	public void disconnectFromAnalyzerNetwork() {
		analyzerHost = null;
	}

	@Override
	public boolean shouldProcess() {
		return (inventoryStacks[0] == null || inventoryStacks[0].isItemEqual(getIngotResult()) && inventoryStacks[0].stackSize < getInventoryStackLimit()) && hasSufficientIngots();
	}

	@Override
	protected boolean shouldResetProgress() {
		return !hasSufficientIngots() || recipeChanged;
	}

	@Override
	protected void onFinishProcess() {
		final int[] ingotsToRemove = new int[Consts.METAL_COUNT];
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			ingotsToRemove[i] = EnumAlloy.getMetalAmt(recipeAlloyID, i);
		for(final int slot : getSlotsWithIngot()) {
			final int ingotNum = MachineHelper.getIngotNum(inventoryStacks[slot]);
			final int ingots = ingotsToRemove[ingotNum];
			ingotsToRemove[ingotNum] -= Math.min(ingotsToRemove[ingotNum], inventoryStacks[slot].stackSize);
			decrStackSize(slot, Math.min(ingots, inventoryStacks[slot].stackSize));
		}
		final ItemStack result = getIngotResult();

		if(inventoryStacks[0] == null)
			inventoryStacks[0] = result; // If there are no alloys in the output slot, add this one

		else
			inventoryStacks[0].stackSize += result.stackSize; // Otherwise, increment the stack size
	}

	@Override
	public int getRKChange() {
		return (int)(baseRKPerTick * rkPerTickMult / processTimeMult * getIngotsInRecipe());
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		recipeAlloyID = tagCompound.getByte("recipeAlloyID");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setByte("recipeAlloyID", recipeAlloyID);
	}

	@Override
	public Object[] getSyncDataToClient() {
		return ArrayUtils.addAll(super.getSyncDataToClient(), recipeAlloyID);
	}

	@Override
	public Object[] getSyncDataToServer() {
		return new Object[] { recipeAlloyID };
	}

	public void handlePacketDataFromServer(byte recipeAlloyID) {
		if(recipeAlloyID != this.recipeAlloyID)
			recipeChanged = true;
		this.recipeAlloyID = recipeAlloyID;
	}

	public void handlePacketDataFromClient(byte recipeAlloyID) {
		if(recipeAlloyID != this.recipeAlloyID)
			recipeChanged = true;
		this.recipeAlloyID = recipeAlloyID;
	}

	/** Return the resulting ingot for the smelted ingots
	 * 
	 * @return The resulting ingot. */
	private ItemStack getIngotResult() {
		final ItemStack result = new ItemStack(IAItems.alloyIngot);
		final NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setInteger("alloy", EnumAlloy.getAlloyForID(recipeAlloyID));
		result.setTagCompound(tagCompound);
		result.setItemDamage(recipeAlloyID + 1);
		return result;
	}

	/** Does the inventory of the forge contain enough ingots to fulfill the current recipe? */
	private boolean hasSufficientIngots() {
		if(recipeAlloyID == -1)
			return false;
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			if(getAvailableIngots()[i] < EnumAlloy.getMetalAmt(recipeAlloyID, i))
				return false;
		return true;
	}

	/** Get a list of the metal slots that contain an ingot */
	private ArrayList<Integer> getSlotsWithIngot() {
		final ArrayList<Integer> slots = new ArrayList<Integer>();
		for(int i = 1; i < 19; i++)
			if(inventoryStacks[i] != null)
				slots.add(i);
		return slots;
	}

	/** Get the amount of ingots of each metal in the inventory that are available for use */
	private int[] getAvailableIngots() {
		final int[] amts = new int[Consts.METAL_COUNT];
		for(final int slot : getSlotsWithIngot())
			amts[MachineHelper.getIngotNum(inventoryStacks[slot])] += inventoryStacks[slot].stackSize;
		return amts;
	}

	/** Get the total quantity of ingots in the recipe */
	private int getIngotsInRecipe() {
		int ingots = 0;
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			ingots += EnumAlloy.getMetalAmt(recipeAlloyID, i);
		return ingots;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(EnumUpgrade.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(EnumUpgrade.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(EnumUpgrade.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(EnumUpgrade.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 1.0F;

		if(hasUpgrade(EnumUpgrade.CAPACITY2))
			stackLimit = 64;
		else if(hasUpgrade(EnumUpgrade.CAPACITY1))
			stackLimit = 48;
		else
			stackLimit = 32;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(EnumUpgrade.SPEED1);
		validUpgrades.add(EnumUpgrade.SPEED2);
		validUpgrades.add(EnumUpgrade.EFFICIENCY1);
		validUpgrades.add(EnumUpgrade.EFFICIENCY2);
		validUpgrades.add(EnumUpgrade.CAPACITY1);
		validUpgrades.add(EnumUpgrade.CAPACITY2);
		validUpgrades.add(EnumUpgrade.WIRELESS);
	}
}
