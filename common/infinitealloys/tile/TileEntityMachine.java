package infinitealloys.tile;

import infinitealloys.block.BlockMachine;
import infinitealloys.handlers.PacketHandler;
import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IConnector;
import universalelectricity.core.block.IElectricityStorage;
import universalelectricity.core.block.IVoltage;
import universalelectricity.core.electricity.ElectricityNetworkHelper;
import universalelectricity.core.electricity.IElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityMachine extends TileEntity implements IInventory, IElectricityStorage, IVoltage, IConnector {

	public Random random = new Random();
	public ArrayList<String> playersUsing = new ArrayList<String>();
	public ItemStack[] inventoryStacks;

	/** A binary integer used to determine what upgrades have been installed */
	private int upgrades;

	/** A list of the upgrades that can be used on this machine */
	protected ArrayList<Integer> validUpgrades = new ArrayList<Integer>();
	public int front;

	/** The index of the slot that upgrades are placed in */
	public int upgradeSlotIndex = 0;

	/** True if this machine can be accessed wirelessly */
	public boolean canNetwork;

	/** Maximum amount of joules this machine can store */
	public int maxJoules = 500000;

	/** Amount of joules stored in the machine currently */
	public int joules = 0;

	/** Joules gained this tick, for the GUI */
	public int joulesGained = 0;

	/** Amount of joules this machine consumes per tick while working */
	protected int joulesUsedPerTick = 360;

	/** Amount of ticks it takes for this machine to finish one of its processes */
	public int ticksToProcess = 200;

	/** Amount of ticks this machine has been running its process for, when this reaches ticksToFinish it is done */
	public int processProgress;

	/** The size limit for one stack in this machine */
	protected int stackLimit = 64;

	public TileEntityMachine(int index) {
		this();
		upgradeSlotIndex = index;
	}

	public TileEntityMachine() {
		populateValidUpgrades();
		updateUpgrades();
	}

	@Override
	public void updateEntity() {
		if(inventoryStacks[upgradeSlotIndex] != null && isUpgradeValid(inventoryStacks[upgradeSlotIndex])) {
			upgrades |= (int)Math.pow(2, inventoryStacks[upgradeSlotIndex].getItemDamage());
			inventoryStacks[upgradeSlotIndex] = null;
			updateUpgrades();
		}
		if(!worldObj.isRemote) {
			EnumSet<ForgeDirection> inputDirections = EnumSet.allOf(ForgeDirection.class);
			inputDirections.remove(front);
			for(ForgeDirection inputDirection : inputDirections) {
				TileEntity inputTile = VectorHelper.getConnectorFromSide(worldObj, new Vector3(this), inputDirection);
				IElectricityNetwork network = ElectricityNetworkHelper.getNetworkFromTileEntity(inputTile, inputDirection);
				if(network != null) {
					if(joules < maxJoules) {
						network.startRequesting(this, TEHelper.AMPS_PER_TICK, getVoltage());
						joules += (joulesGained = (int)Math
								.max(Math.min(network.consumeElectricity(this).getWatts(), TEHelper.AMPS_PER_TICK * getVoltage()), 0));
					}
					else
						network.stopRequesting(this);
				}
			}
		}
		if(shouldProcess() && ++processProgress >= ticksToProcess) {
			processProgress = 0;
			finishProcessing();
			onInventoryChanged();
		}
		joules -= getJoulesUsed();
		for(String playerName : playersUsing)
			PacketDispatcher.sendPacketToPlayer(PacketHandler.getTEJoulesPacket(this), (Player)FMLCommonHandler.instance().getSidedDelegate().getServer()
					.getConfigurationManager().getPlayerForUsername(playerName));
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
					EntityItem item = new EntityItem(worldObj, xCoord + f1, yCoord + f2, zCoord + f3, new ItemStack(stack.itemID, j, stack.getItemDamage()));
					if(stack.hasTagCompound())
						item.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
					item.motionX = random.nextGaussian() * 0.05F;
					item.motionY = random.nextGaussian() * 0.25F;
					item.motionZ = random.nextGaussian() * 0.05F;
					worldObj.spawnEntityInWorld(item);
				}
			}
		}
	}

	/** Drops the upgrades that were used on the block as items */
	public void dropUpgrades() {
		for(int i = 0; i <= Consts.UPGRADE_COUNT; i++) {
			if(hasUpgrade((int)Math.pow(2D, i))) {
				float f = random.nextFloat() * 0.8F + 0.1F;
				float f1 = random.nextFloat() * 0.8F + 0.1F;
				float f2 = random.nextFloat() * 0.8F + 0.1F;
				EntityItem item = new EntityItem(worldObj, xCoord + f, yCoord + f1, zCoord + f2, new ItemStack(Items.upgrade, 1, i));
				item.motionX = random.nextGaussian() * 0.05F;
				item.motionY = random.nextGaussian() * 0.25F;
				item.motionZ = random.nextGaussian() * 0.05F;
				worldObj.spawnEntityInWorld(item);
			}
		}
		upgrades = 0;
	}

	/** Determines if the given itemstack is a valid upgrade for the machine
	 * 
	 * @param upgrade
	 * @return true if valid */
	public boolean isUpgradeValid(ItemStack upgrade) {
		int upg = (int)Math.pow(2, upgrade.getItemDamage());
		return upgrade.itemID == Items.upgrade.itemID && (!TEHelper.hasPrereqUpgrade(upg) || hasUpgrade(upg >> 1)) && !hasUpgrade(upg)
				&& validUpgrades.contains(upg);
	}

	/** Should the process tick be increased? */
	protected abstract boolean shouldProcess();

	/** Called when processProgress reeaches ticksToProgress */
	protected abstract void finishProcessing();

	/** Actual amount of joules used per tick, after certain calculations and conditions */
	public abstract int getJoulesUsed();

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

	@SideOnly(Side.CLIENT)
	public int getProcessProgressScaled(int scale) {
		return processProgress * scale / ticksToProcess;
	}

	@SideOnly(Side.CLIENT)
	public int getJoulesScaled(int scale) {
		if(maxJoules > 0)
			return (int)(joules * scale / maxJoules);
		return -1;
	}

	public boolean coordsEquals(int x2, int y2, int z2) {
		return xCoord == x2 && yCoord == y2 && zCoord == z2;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		processProgress = tagCompound.getInteger("ProcessProgress");
		upgrades = tagCompound.getShort("Upgrades");
		front = tagCompound.getByte("Orientation");
		joules = tagCompound.getInteger("Joules");
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
		tagCompound.setByte("Orientation", (byte)front);
		tagCompound.setInteger("Joules", joules);
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
	public boolean isStackValidForSlot(int slot, ItemStack itemstack) {
		return slot == upgradeSlotIndex && isUpgradeValid(itemstack);
	}

	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public Packet getDescriptionPacket() {
		return PacketHandler.getTEPacketToClient(this);
	}

	public void handlePacketDataFromServer(int processProgress, byte orientation, int upgrades, int joules) {
		this.processProgress = processProgress;
		front = orientation;
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
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public double getVoltage() {
		return 120;
	}

	@Override
	public double getJoules() {
		return joules;
	}

	@Override
	public void setJoules(double joules) {
		this.joules = (int)joules;
	}

	@Override
	public double getMaxJoules() {
		return maxJoules;
	}

	@Override
	public boolean canConnect(ForgeDirection side) {
		return maxJoules > 0 && Funcs.fdToNumSide(side) != front;
	}
}
