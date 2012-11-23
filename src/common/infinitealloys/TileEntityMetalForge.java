package infinitealloys;

import infinitealloys.handlers.PacketHandler;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityMetalForge extends TileEntityMachine {

	/** An array for the "stack sizes" of each ingot in the recipe setting */
	public byte[] recipeAmts = new byte[References.metalCount];

	public TileEntityMetalForge(ForgeDirection facing) {
		this();
		front = facing;
	}

	public TileEntityMetalForge() {
		super(9);
		inventoryStacks = new ItemStack[29];
		ticksToProcess = 12800;
	}

	@Override
	public String getInvName() {
		return "Metal Forge";
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		for(int i = 0; i < recipeAmts.length; i++)
			recipeAmts[i] = tagCompound.getByte("Recipe Amount " + i);
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		for(int i = 0; i < recipeAmts.length; i++)
			tagCompound.setByte("Recipe Amount " + i, recipeAmts[i]);
	}

	public void handlePacketDataFromServer(byte[] recipeAmts) {
		this.recipeAmts = recipeAmts;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		boolean invChanged = false;
		joulesUsedPerTick = (double)getIngotsInRecipe() * 360D * joulesUseMult;
		if(shouldBurn()) {
			processProgress += (float)(getInventoryStackLimit() - getIngotsInRecipe() + 1);
			joules -= joulesUsedPerTick;
			if(processProgress >= ticksToProcess) {
				processProgress = 0;
				smeltItem();
				invChanged = true;
			}
		}
		else
			processProgress = 0;
		if(invChanged)
			onInventoryChanged();
		for(String playerName : playersUsing)
			PacketDispatcher.sendPacketToPlayer(PacketHandler.getTEJoulesPacket(this), (Player)FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(playerName));
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {
		if(side == ForgeDirection.DOWN)
			return 1;
		if(side == ForgeDirection.UP)
			return 0;
		return 2;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		return 1;
	}

	private boolean shouldBurn() {
		int typesInRecipe = 0;
		ArrayList<Boolean> sufficientIngots = new ArrayList<Boolean>();
		for(int amt : recipeAmts)
			if(amt > 0)
				typesInRecipe++;
		for(int i = 0; i < getIngotAmts().length; i++)
			sufficientIngots.add(getIngotAmts()[i] >= recipeAmts[i]);
		return typesInRecipe > 1 && !sufficientIngots.contains(false) && joules >= joulesUsedPerTick && (inventoryStacks[10] == null || (inventoryStacks[10].isItemEqual(getIngotResult()) && getInventoryStackLimit() - inventoryStacks[10].stackSize >= 1));
	}

	private void smeltItem() {
		byte[] ingotsToRemove = Arrays.copyOf(recipeAmts, recipeAmts.length);
		for(int slot : getSlotsWithIngot()) {
			int ingotNum = getIngotNum(inventoryStacks[slot]);
			int ingots = ingotsToRemove[ingotNum];
			ingotsToRemove[ingotNum] -= Math.min(ingotsToRemove[ingotNum], inventoryStacks[slot].stackSize);
			decrStackSize(slot, Math.min(ingots, inventoryStacks[slot].stackSize));
		}
		ItemStack ingotResult = getIngotResult();
		if(inventoryStacks[10] == null)
			inventoryStacks[10] = ingotResult;
		else if(inventoryStacks[10].getTagCompound().getInteger("alloy") == ingotResult.getTagCompound().getInteger("alloy"))
			inventoryStacks[10].stackSize += ingotResult.stackSize;
	}

	public int getIngotNum(ItemStack ingot) {
		if(ingot.itemID == InfiniteAlloys.ingot.shiftedIndex && ingot.getItemDamage() < References.metalCount)
			return ingot.getItemDamage();
		return -1;
	}

	/**
	 * Return the resulting ingot for the smelted ingots
	 * 
	 * @return The resulting ingot.
	 */
	private ItemStack getIngotResult() {
		int alloy = 0;
		for(int i = 0; i < recipeAmts.length; i++)
			alloy += Math.pow(References.alloyRadix, i) * recipeAmts[i];
		ItemStack result = new ItemStack(InfiniteAlloys.alloyIngot);
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setInteger("alloy", alloy);
		result.setTagCompound(tagCompound);
		int[] validAlloys = InfiniteAlloys.instance.worldData.validAlloys;
		for(int i = 0; i < validAlloys.length; i++) {
			if(alloy == validAlloys[i]) {
				result.setItemDamage(i + 1);
				break;
			}
		}
		return result;
	}

	private ArrayList<Integer> getSlotsWithIngot() {
		ArrayList<Integer> slots = new ArrayList<Integer>();
		for(int i = 11; i < 29; i++)
			if(inventoryStacks[i] != null)
				slots.add(i);
		return slots;
	}

	private int[] getIngotAmts() {
		int[] amts = new int[References.metalCount];
		for(int slot : getSlotsWithIngot())
			amts[getIngotNum(inventoryStacks[slot])] += inventoryStacks[slot].stackSize;
		return amts;
	}

	private int getIngotsInRecipe() {
		int ingots = 0;
		for(int amt : recipeAmts)
			ingots += amt;
		return ingots;
	}

	protected void updateUpgrades() {
		if(hasUpgrade(SPEED2))
			ticksToProcess = 6400;
		else if(hasUpgrade(SPEED1))
			ticksToProcess = 9600;
		else
			ticksToProcess = 12800;

		if(hasUpgrade(EFFICIENCY2))
			joulesUseMult = 0.5D;
		else if(hasUpgrade(EFFICIENCY1))
			joulesUseMult = 0.75D;
		else
			joulesUseMult = 1D;

		if(hasUpgrade(CAPACITY2))
			stackLimit = 48;
		else if(hasUpgrade(CAPACITY1))
			stackLimit = 64;
		else
			stackLimit = 32;

		canNetwork = hasUpgrade(WIRELESS);

		if(hasUpgrade(ELECCAPACITY2))
			maxJoules = 1000000D;
		else if(hasUpgrade(ELECCAPACITY1))
			maxJoules = 750000D;
		else
			maxJoules = 500000D;

	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(SPEED1);
		validUpgrades.add(SPEED2);
		validUpgrades.add(EFFICIENCY1);
		validUpgrades.add(EFFICIENCY2);
		validUpgrades.add(CAPACITY1);
		validUpgrades.add(CAPACITY2);
		validUpgrades.add(WIRELESS);
		validUpgrades.add(ELECCAPACITY1);
		validUpgrades.add(ELECCAPACITY2);
	}
}
