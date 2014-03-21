package infinitealloys.tile;

import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.NetworkManager.Network;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;

public class TEEMetalForge extends TileEntityElectric {

	/** The ID of the alloy that is currently set as the recipe. This is drawn from the connected analyzer. */
	public int recipeAlloyID = -1;

	/** True if the alloy recipe has been changed by the client, used to reset progress */
	private boolean recipeChanged;

	/** The id of the network to which this metal forge is connected */
	private int analyzerNetworkID = -1;

	public TEEMetalForge(byte front) {
		this();
		this.front = front;
	}

	public TEEMetalForge() {
		super(20);
		baseRKPerTick = -18;
	}

	@Override
	public int getID() {
		return MachineHelper.METAL_FORGE;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		recipeChanged = false;

		if(analyzerNetworkID == -1)
			recipeAlloyID = -1;
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
		tagCompound.setByte("recipeAlloyID", (byte)recipeAlloyID);
	}

	@Override
	public Object[] getSyncDataToClient() {
		return ArrayUtils.addAll(super.getSyncDataToClient(), recipeAlloyID);
	}

	@Override
	public Object[] getSyncDataToServer() {
		return new Object[] { recipeAlloyID };
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
		final ItemStack result = new ItemStack(Items.alloyIngot);
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
		if(hasUpgrade(MachineHelper.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(MachineHelper.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(MachineHelper.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(MachineHelper.EFFICIENCY1))
			rkPerTickMult = 0.75F;
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
}
