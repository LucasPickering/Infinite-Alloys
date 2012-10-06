package infinitealloys;

import java.util.ArrayList;
import java.util.Random;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.BlockFurnace;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityFurnace;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityMetalForge extends TileEntityMachineInventory {

	/**
	 * The amount of ticks that the fuel in the slot will burn for.
	 */
	public int currentFuelBurnTime;

	/**
	 * The ticks that the metal forge can burn for before using more fuel.
	 */
	public int heatLeft;

	/**
	 * The multiplier for the fuel burn time
	 */
	public float fuelBonus = 1F;

	/**
	 * Ticks it takes to finish smelting one ingot.
	 */
	public int ticksToFinish = 200;

	/**
	 * The smelting progress
	 */
	public int smeltProgress;

	public TileEntityMetalForge(int facing) {
		this();
		orientation = facing;
	}

	public TileEntityMetalForge() {
		inventoryStacks = new ItemStack[29];
		orientation = 2;
	}

	@Override
	public String getInvName() {
		return "Metal Forge";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		heatLeft = nbttagcompound.getShort("BurnTime");
		smeltProgress = nbttagcompound.getShort("SmeltProgress");
		networkID = nbttagcompound.getShort("NetworkID");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setShort("Upgrades", (short)upgrades);
		nbttagcompound.setShort("BurnTime", (short)heatLeft);
		nbttagcompound.setShort("SmeltProgress", (short)smeltProgress);
		nbttagcompound.setShort("NetworkID", (short)networkID);
	}

	@Override
	public void updateEntity() {
		updateUpgrades();
		boolean invChanged = false;
		if(heatLeft <= 0) {
			currentFuelBurnTime = 0;
			if(inventoryStacks[0] != null)
				currentFuelBurnTime = (int)((float)TileEntityFurnace.getItemBurnTime(inventoryStacks[0]) * fuelBonus);
			if(shouldBurn()) {
				heatLeft = currentFuelBurnTime;
				invChanged = true;
				if(inventoryStacks[0] != null)
					if(--inventoryStacks[0].stackSize <= 0)
						inventoryStacks[0] = null;
			}
		}
		if(shouldBurn()) {
			smeltProgress++;
			heatLeft -= getIngotsInInv();
			if(smeltProgress == ticksToFinish) {
				smeltProgress = 0;
				smeltItem();
				invChanged = true;
			}
		}
		else
			smeltProgress = 0;
		BlockMachine.updateBlockState(worldObj, xCoord, yCoord, zCoord);
		if(invChanged)
			onInventoryChanged();
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {
		if(side == ForgeDirection.DOWN) return 1;
		if(side == ForgeDirection.UP) return 0;
		return 2;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		return 1;
	}

	@Override
	public boolean isUpgradeValid(ItemStack upgrade) {
		return false;
	}

	private boolean shouldBurn() {
		int ingotTypesInInv = 0;
		int ingotsInInv = 0;
		for(int i = 0; i < IAValues.metalCount; i++)
			if(inventoryStacks[i + 1] != null) {
				ingotTypesInInv++;
				ingotsInInv += inventoryStacks[i + 1].stackSize;
			}
		if(ingotTypesInInv > 1 && (heatLeft > ingotsInInv || currentFuelBurnTime != 0) && (inventoryStacks[10] == null || (inventoryStacks[10].isItemEqual(getIngotResult()) && getInventoryStackLimit() - inventoryStacks[10].stackSize >= getIngotsInInv())))
			return true;
		return false;
	}

	/**
	 * Updates the settings based on the speed, capacity, and efficiency
	 * upgrades.
	 */
	private void updateUpgrades() {
		if((upgrades & 32) == 32)
			ticksToFinish = 150;
		if((upgrades & 64) == 64)
			ticksToFinish = 100;
		if((upgrades & 128) == 256)
			fuelBonus = 1.5F;
		if((upgrades & 256) == 512)
			fuelBonus = 2F;
	}

	private void smeltItem() {
		for(int slot : getSlotsWithIngot())
			if(--inventoryStacks[slot].stackSize <= 0)
				inventoryStacks[slot] = null;
		if(inventoryStacks[10] == null)
			inventoryStacks[10] = getIngotResult();
		else if(inventoryStacks[10].isItemEqual(getIngotResult()))
			inventoryStacks[10].stackSize++;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Get a scaled cook progress, used for the gui progress bar.
	 * @param i Scale
	 * @return Scaled progress
	 */
	public int getCookProgressScaled(int i) {
		return smeltProgress * i / ticksToFinish;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Get a scaled burn time, used for the gui flames.
	 * @param i Scale
	 * @return Scaled burn time
	 */
	public int getBurnTimeRemainingScaled(int i) {
		return heatLeft * i / currentFuelBurnTime;
	}

	public int getIngotNum(ItemStack ingot) {
		int ingotNum = 0;
		if(ingot.itemID == Item.ingotIron.shiftedIndex)
			ingotNum = 1;
		else if(ingot.itemID == InfiniteAlloys.ingot.shiftedIndex && ingot.getItemDamage() < IAValues.metalCount)
			ingotNum = ingot.getItemDamage() + 2;
		return ingotNum;
	}

	/**
	 * Return the resulting ingot for the smelted ingots.
	 * 
	 * @return The resulting ingot.
	 */
	private ItemStack getIngotResult() {
		int damage = 0;
		ItemStack itemstack = new ItemStack(InfiniteAlloys.alloyIngot);
		for(int i = 0; i < IAValues.metalCount; i++)
			if(inventoryStacks[i + 1] != null)
				damage += Math.pow(8D, getIngotNum(inventoryStacks[i + 1])) * inventoryStacks[i + 1].stackSize;
		return new ItemStack(InfiniteAlloys.alloyIngot, 1, damage);
	}

	private ArrayList<Integer> getSlotsWithIngot() {
		ArrayList<Integer> slots = new ArrayList<Integer>();
		for(int i = 1; i <= 9; i++)
			if(inventoryStacks[i] != null)
				slots.add(i);
		return slots;
	}

	private int getIngotsInInv() {
		int ingots = 0;
		for(int i = 0; i < IAValues.metalCount; i++)
			if(inventoryStacks[i + 1] != null)
				ingots += inventoryStacks[i + 1].stackSize;
		return ingots;
	}
}
