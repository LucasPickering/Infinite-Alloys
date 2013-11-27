package infinitealloys.tile;

import infinitealloys.block.BlockMachine;
import infinitealloys.item.Items;
import infinitealloys.network.PacketTEServerToClient;
import infinitealloys.util.Consts;
import infinitealloys.util.MachineHelper;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;

/** A base, abstract class for Tile Entities that can receive upgrades. TileEntityElectric blocks are a sub-type of this. Often referred to as TEMs or machines.
 * 
 * @see TileEntityElectric */
public abstract class TileEntityMachine extends TileEntity implements IInventory {

	/** The stacks that make up the inventory of this TE */
	public ItemStack[] inventoryStacks;

	/** A list of names of the players who are currently using this machine */
	public final ArrayList<String> playersUsing = new ArrayList<String>();

	/** A list of the upgrades that can be used on this machine */
	protected final ArrayList<Integer> validUpgrades = new ArrayList<Integer>();

	/** A number from 0-5 to represent which side of this block gets the front texture */
	public byte front;

	/** A binary integer used to determine what upgrades have been installed */
	private int upgrades;

	/** A binary integer containing upgrades that the machine starts with, e.g. the computer starts with the Wireless upgrade */
	protected int startingUpgrades;

	/** The index of the slot that upgrades are placed in */
	public int upgradeSlotIndex = 0;

	/** The size limit for one stack in this machine */
	protected int stackLimit = 64;

	public TileEntityMachine(int inventoryLength) {
		this();
		inventoryStacks = new ItemStack[inventoryLength];
		upgradeSlotIndex = inventoryLength - 1;
	}

	public TileEntityMachine() {
		populateValidUpgrades();
		updateUpgrades();
	}

	/** Get the integer from {@link infinitealloys.util.MachineHelper MachineHelper} that corresponds to this machine */
	public abstract int getID();

	@Override
	public void updateEntity() {

		// Check for upgrades in the upgrade inventory slot. If there is one, remove it from the slot and add it to the machine.
		if(inventoryStacks[upgradeSlotIndex] != null && isUpgradeValid(inventoryStacks[upgradeSlotIndex])) {
			upgrades |= (int)Math.pow(2, inventoryStacks[upgradeSlotIndex].getItemDamage());
			inventoryStacks[upgradeSlotIndex] = null;
			updateUpgrades();
		}

		BlockMachine.updateBlockState(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		upgrades = tagCompound.getShort("Upgrades");
		front = tagCompound.getByte("Orientation");
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
		tagCompound.setShort("Upgrades", (short)upgrades);
		tagCompound.setByte("Orientation", front);
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
	public Packet getDescriptionPacket() {
		return PacketTEServerToClient.getPacket(this);
	}

	/** A list of the data that gets sent from server to client over the network */
	public Object[] getSyncDataToClient() {
		return new Object[] { front, upgrades };
	}

	/** A list of the data that gets sent from client to server over the network */
	public Object[] getSyncDataToServer() {
		return null;
	}

	public void handlePacketDataFromServer(byte orientation, int upgrades) {
		front = orientation;
		this.upgrades = upgrades;
	}

	/** Get a string containing this machine's coordinates */
	public String getCoords() {
		return xCoord + ", " + yCoord + ", " + zCoord;
	}

	public boolean coordsEquals(int x2, int y2, int z2) {
		return xCoord == x2 && yCoord == y2 && zCoord == z2;
	}

	@Override
	public String getInvName() {
		return MachineHelper.MACHINE_NAMES[getID()];
	}

	@Override
	public final boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return (slot == upgradeSlotIndex && isUpgradeValid(itemstack)) || (slot < upgradeSlotIndex && MachineHelper.stackValidForSlot(getID(), slot, itemstack));
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
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

	/** Drops the items in the block's inventory */
	public void dropItems() {
		Random random = new Random();
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

	public final int getUpgrades() {
		return upgrades;
	}

	protected abstract void updateUpgrades();

	protected abstract void populateValidUpgrades();

	/** Drops the applied upgrades as items */
	public final void dropUpgrades() {
		Random random = new Random();
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

	/** Determines if the given itemstack is a valid upgrade for the machine. Criteria: Does this machine take this type of upgrade? Does this machine already
	 * have this upgrade? Does this upgrade have a prerequisite upgrade and if so, does this machine already have that upgrade?
	 * 
	 * @param ItemStack for upgrade item with a binary upgrade damage value (see {@link infinitealloys.util.MachineHelper TEHelper} for upgrade numbers)
	 * @return true if valid */
	public final boolean isUpgradeValid(ItemStack upgrade) {
		int upg = (int)Math.pow(2, upgrade.getItemDamage());
		return upgrade.itemID == Items.upgrade.itemID && (!MachineHelper.hasPrereqUpgrade(upg) || hasUpgrade(upg >> 1)) && !hasUpgrade(upg) && validUpgrades.contains(upg);
	}

	/** Does the machine have the upgrade
	 * 
	 * @param binary upgrade damage value (see {@link infinitealloys.util.MachineHelper TEHelper} for upgrade numbers)
	 * @return true if the machine has the upgrade */
	public boolean hasUpgrade(int upgrade) {
		return (upgrades & upgrade) == upgrade;
	}
}
