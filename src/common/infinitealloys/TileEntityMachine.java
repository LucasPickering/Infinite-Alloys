package infinitealloys;

import infinitealloys.handlers.PacketHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.core.implement.IElectricityReceiver;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public abstract class TileEntityMachine extends TileEntity implements ISidedInventory, IElectricityReceiver {

	public static final int SPEED1 = 1;
	public static final int SPEED2 = 2;
	public static final int EFFICIENCY1 = 4;
	public static final int EFFICIENCY2 = 8;
	public static final int CAPACITY1 = 16;
	public static final int CAPACITY2 = 32;
	public static final int RANGE1 = 64;
	public static final int RANGE2 = 128;
	public static final int WIRELESS = 256;
	public static final int ELECCAPACITY1 = 512;
	public static final int ELECCAPACITY2 = 1024;

	/** The controlling computer for each player */
	public static HashMap<String, Point> controllers = new HashMap<String, Point>();

	public Random random = new Random();
	public ArrayList<String> playersUsing = new ArrayList<String>();
	public ItemStack[] inventoryStacks;

	/** A list of upgrades that are prerequisites for other upgrades */
	private ArrayList<Integer> prereqUpgrades = new ArrayList<Integer>();

	/** A list of upgrades that require other upgrades to work */
	private ArrayList<Integer> prereqNeedingUpgrades = new ArrayList<Integer>();

	/** A binary integer used to determine what upgrades have been installed */
	private int upgrades;

	/** A list of the upgrades that can be used on this machine */
	protected ArrayList<Integer> validUpgrades = new ArrayList<Integer>();
	public ForgeDirection front;

	/** The index of the slot that upgrades are placed in */
	public int upgradeSlotIndex = 0;

	/** True if this machine can be accessed wirelessly */
	public boolean canNetwork;

	/** Maximum amount of joules this machine can store */
	protected double maxJoules = 500000D;

	/** Amount of joules stored in the machine currently */
	public double joules = 0D;

	/** Amount of joules this machine consumes per tick while working */
	protected double joulesUsedPerTick = 360D;

	/** Amount of ticks it takes for this machine to finish one of its processes */
	protected int ticksToProcess = 200;

	/** Amount of ticks this machine has been running its process for, when this reaches ticksToFinish it is done */
	public int processProgress;

	/** The size limit for one stack in this machine */
	protected int stackLimit = 64;

	public TileEntityMachine(int index) {
		this();
		upgradeSlotIndex = index;
	}

	public TileEntityMachine() {
		prereqUpgrades.add(SPEED1);
		prereqUpgrades.add(EFFICIENCY1);
		prereqUpgrades.add(CAPACITY1);
		prereqUpgrades.add(RANGE1);
		prereqUpgrades.add(ELECCAPACITY1);
		prereqNeedingUpgrades.add(SPEED2);
		prereqNeedingUpgrades.add(EFFICIENCY2);
		prereqNeedingUpgrades.add(CAPACITY2);
		prereqNeedingUpgrades.add(RANGE2);
		prereqNeedingUpgrades.add(ELECCAPACITY2);
		populateValidUpgrades();
	}

	@Override
	public void updateEntity() {
		if(inventoryStacks[upgradeSlotIndex] != null && isUpgradeValid(inventoryStacks[upgradeSlotIndex])) {
			upgrades |= inventoryStacks[upgradeSlotIndex].getItemDamage();
			inventoryStacks[upgradeSlotIndex] = null;
		}
		updateUpgrades();
		for(String playerName : playersUsing)
			PacketDispatcher.sendPacketToPlayer(PacketHandler.getTEJoulesPacket(this), (Player)FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(playerName));
		BlockMachine.updateBlockState(worldObj, xCoord, yCoord, zCoord);
	}

	public int getUpgrades() {
		return upgrades;
	}

	/** Drops the items in the block's inventory */
	public void dropItems() {
		for(int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if(stack != null) {
				float f1 = random.nextFloat() * 0.8F + 0.1F;
				float f2 = random.nextFloat() * 0.8F + 0.1F;
				float f3 = random.nextFloat() * 0.8F + 0.1F;
				while(stack.stackSize > 0) {
					int j = random.nextInt(21) + 10;
					if(j > stack.stackSize)
						j = stack.stackSize;
					stack.stackSize -= j;
					EntityItem entityitem = new EntityItem(worldObj, (float)xCoord + f1, (float)yCoord + f2, (float)zCoord + f3, new ItemStack(stack.itemID, j, stack.getItemDamage()));
					if(stack.hasTagCompound())
						entityitem.item.setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
					entityitem.motionX = random.nextGaussian() * 0.05F;
					entityitem.motionY = random.nextGaussian() * 0.25F;
					entityitem.motionZ = random.nextGaussian() * 0.05F;
					worldObj.spawnEntityInWorld(entityitem);
				}
			}
		}
	}

	/** Drops the upgrades that were used on the block as items */
	public void dropUpgrades() {
		for(int i = 0; i <= References.upgradeCount; i++) {
			int upg = (int)Math.pow(2D, i);
			if(hasUpgrade(upg)) {
				float f = random.nextFloat() * 0.8F + 0.1F;
				float f1 = random.nextFloat() * 0.8F + 0.1F;
				float f2 = random.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem = new EntityItem(worldObj, (float)xCoord + f, (float)yCoord + f1, (float)zCoord + f2, new ItemStack(InfiniteAlloys.upgrade, 1, upg));
				entityitem.motionX = random.nextGaussian() * 0.05F;
				entityitem.motionY = random.nextGaussian() * 0.25F;
				entityitem.motionZ = random.nextGaussian() * 0.05F;
				worldObj.spawnEntityInWorld(entityitem);
			}
		}
		upgrades = 0;
	}

	/** Determines if the given itemstack is a valid upgrade for the machine
	 * 
	 * @param upgrade
	 * @return true if valid */
	public boolean isUpgradeValid(ItemStack upgrade) {
		int damage = upgrade.getItemDamage();
		return upgrade.itemID == InfiniteAlloys.upgrade.shiftedIndex && (!hasPrereqUpgrade(upgrade) || hasUpgrade(damage >> 1)) && !hasUpgrade(damage) && validUpgrades.contains(damage);
	}

	/** Updates all values that are dependent on upgrades */
	protected abstract void updateUpgrades();

	/** Add the valid upgrades for each machine */
	protected abstract void populateValidUpgrades();

	/** Does the machine have the upgrade
	 * 
	 * @param upgrade
	 * @return true if the machine has the upgrade */
	public boolean hasUpgrade(int upgrade) {
		return (upgrades & upgrade) == upgrade;
	}

	/** Is the upgrade a prerequisite for another
	 * 
	 * @param upgrade
	 * @return true if it is a prereq */
	public boolean isPrereqUpgrade(ItemStack upgrade) {
		return prereqUpgrades.contains(upgrade.getItemDamage());
	}

	/** Does the upgrade require another to work?
	 * 
	 * @param upgrade
	 * @return true if it has a prereq */
	public boolean hasPrereqUpgrade(ItemStack upgrade) {
		return prereqNeedingUpgrades.contains(upgrade.getItemDamage());
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Get a scaled progress, used for the gui progress bar
	 * @param i Scale
	 * @return Scaled progress
	 */
	public int getProcessProgressScaled(int i) {
		return processProgress * i / ticksToProcess;
	}

	@SideOnly(Side.CLIENT)
	public int getJoulesScaled(int scale) {
		return (int)(joules * scale / maxJoules);
	}

	public boolean coordsEquals(int x2, int y2, int z2) {
		return xCoord == x2 && yCoord == y2 && zCoord == z2;
	}

	public static boolean isAlloyBook(ItemStack stack) {
		return stack.itemID == InfiniteAlloys.alloyBook.shiftedIndex && stack.hasTagCompound();
	}

	public static boolean isBook(ItemStack stack) {
		return stack.itemID == InfiniteAlloys.alloyBook.shiftedIndex || stack.itemID == Item.writableBook.shiftedIndex || stack.itemID == Item.writtenBook.shiftedIndex && stack.hasTagCompound();
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		processProgress = tagCompound.getInteger("ProcessProgress");
		upgrades = tagCompound.getShort("Upgrades");
		front = ForgeDirection.getOrientation(tagCompound.getByte("Orientation"));
		joules = tagCompound.getDouble("Joules");
		NBTTagList nbttaglist = tagCompound.getTagList("Items");
		inventoryStacks = new ItemStack[getSizeInventory()];
		for(int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttag = (NBTTagCompound)nbttaglist.tagAt(i);
			byte var5 = nbttag.getByte("Slot");
			if(var5 >= 0 && var5 < inventoryStacks.length)
				inventoryStacks[var5] = ItemStack.loadItemStackFromNBT(nbttag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("ProcessProgress", processProgress);
		tagCompound.setShort("Upgrades", (short)upgrades);
		tagCompound.setByte("Orientation", (byte)front.ordinal());
		tagCompound.setDouble("Joules", joules);
		NBTTagList nbttaglist = new NBTTagList();
		for(int i = 0; i < inventoryStacks.length; i++) {
			if(inventoryStacks[i] != null) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setByte("Slot", (byte)i);
				inventoryStacks[i].writeToNBT(nbt);
				nbttaglist.appendTag(nbt);
			}
		}
		tagCompound.setTag("Items", nbttaglist);
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {
		return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		return 0;
	}

	@Override
	public Packet getDescriptionPacket() {
		return PacketHandler.getTEPacketToClient(this);
	}

	public void handlePacketDataFromServer(int processProgress, byte orientation, int upgrades, double joules) {
		this.processProgress = processProgress;
		front = ForgeDirection.getOrientation(orientation);
		this.upgrades = upgrades;
		this.joules = joules;
	}

	@Override
	public int getInventoryStackLimit() {
		return stackLimit;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		if(inventoryStacks[slot] != null) {
			ItemStack stack;
			if(inventoryStacks[slot].stackSize <= amt) {
				stack = inventoryStacks[slot];
				inventoryStacks[slot] = null;
				return stack;
			}
			stack = inventoryStacks[slot].splitStack(amt);
			if(inventoryStacks[slot].stackSize == 0)
				inventoryStacks[slot] = null;
			return stack;
		}
		return null;
	}

	@Override
	public int getSizeInventory() {
		return inventoryStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventoryStacks[slot];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if(inventoryStacks[slot] != null) {
			ItemStack stack = inventoryStacks[slot];
			inventoryStacks[slot] = null;
			return stack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventoryStacks[slot] = stack;
		if(stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side) {
		joules = Math.min(joules + amps * voltage, maxJoules);
	}

	@Override
	public double wattRequest() {
		return maxJoules - joules;
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side) {
		return !side.equals(front);
	}

	@Override
	public void onDisable(int duration) {}

	@Override
	public boolean isDisabled() {
		return false;
	}

	@Override
	public boolean canConnect(ForgeDirection side) {
		return canReceiveFromSide(side);
	}

	@Override
	public double getVoltage() {
		return 120;
	}
}
