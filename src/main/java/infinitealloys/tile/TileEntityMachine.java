package infinitealloys.tile;

import infinitealloys.item.IAItems;
import infinitealloys.item.ItemUpgrade;
import infinitealloys.network.MessageTEToClient;
import infinitealloys.network.MessageTEToServer;
import infinitealloys.network.NetworkHandler;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import org.apache.commons.lang3.ArrayUtils;

/** A base, abstract class for Tile Entities that can receive upgrades.
 * TileEntityElectric blocks are a sub-type of this. Often referred to as TEMs or machines.
 *
 * @see TileEntityElectric */
public abstract class TileEntityMachine extends TileEntityIA implements IInventory {

	/** The stacks that make up the inventory of this TE */
	public ItemStack[] inventoryStacks;

	/** A list of the upgrades that can be used on this machine */
	protected final ArrayList<ItemUpgrade> validUpgradeTypes = new ArrayList<ItemUpgrade>();

	/** Each element in the array corresponds to an upgrade type, and represents how many tiers in the type have been unlocked */
	private int[] upgrades = new int[Consts.UPGRADE_TYPE_COUNT];

	/** The index of the slot that upgrades are placed in */
	public int upgradeSlotIndex = 0;

	/** The size limit for one stack in this machine */
	protected int stackLimit = 64;

	/** The coordinates of the computer that is controlling this machine */
	public Point computerHost;

	/** @param inventoryLength The amount of total slots in the inventory */
	public TileEntityMachine(int inventoryLength) {
		this();
		inventoryStacks = new ItemStack[inventoryLength];
		upgradeSlotIndex = inventoryLength - 1;
	}

	public TileEntityMachine() {
		super();
		populateValidUpgrades();
		updateUpgrades();
	}

	/** Get the {@link infinitealloys.util.EnumMachine EnumMachine} that corresponds to this machine */
	public abstract EnumMachine getEnumMachine();

	@Override
	public void updateEntity() {
		// Check for upgrades in the upgrade inventory slot. If there is one, remove it from the slot and add it to the machine.
		if(inventoryStacks[upgradeSlotIndex] != null && isUpgradeValid(inventoryStacks[upgradeSlotIndex])) {
			upgrades[((ItemUpgrade)inventoryStacks[upgradeSlotIndex].getItem()).upgradeType]++; // Increment the element in the upgrades array that corresponds to
			inventoryStacks[upgradeSlotIndex] = null;
			updateUpgrades();
		}
	}

	public void connectToComputerNetwork(Point host) {
		if(computerHost != null)
			((TEMComputer)worldObj.getTileEntity(computerHost.x, computerHost.y, computerHost.z)).removeClient(coords(), false);
		computerHost = host;
	}

	public void disconnectFromComputerNetwork() {
		computerHost = null;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	/** Called when the TE's block is destroyed. Ends network connections and drops items and upgrades */
	public void onBlockDestroyed() {
		super.onBlockDestroyed();

		// Drop items in inventory
		for(int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if(stack != null)
				spawnItem(stack);
		}

		// Drop upgrades
		for(int i = 0; i < upgrades.length; i++)
			for(int j = 0; j < upgrades[i]; j++)
				spawnItem(new ItemStack(IAItems.upgrades[i], 1, j));
		Arrays.fill(upgrades, 0);

		if(computerHost != null)
			((IHost)Funcs.getTileEntity(worldObj, computerHost)).removeClient(coords(), true);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		upgrades = tagCompound.getIntArray("upgrades");
		NBTTagList nbttaglist = tagCompound.getTagList("Items", 10);
		for(int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttag = nbttaglist.getCompoundTagAt(i);
			byte slot = nbttag.getByte("Slot");
			if(slot >= 0 && slot < inventoryStacks.length)
				inventoryStacks[slot] = ItemStack.loadItemStackFromNBT(nbttag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setIntArray("upgrades", upgrades);
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

	/** A list of the data that gets sent from server to client over the network */
	public Object[] getSyncDataToClient() {
		return ArrayUtils.addAll(super.getSyncDataToClient(), new Object[] { upgrades });
	}

	public void handlePacketDataFromServer(int[] upgrades) {
		this.upgrades = upgrades;
	}

	@Override
	public String getInventoryName() {
		return getEnumMachine().name;
	}

	@Override
	public final boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return slot == upgradeSlotIndex && isUpgradeValid(itemstack) || slot < upgradeSlotIndex && getEnumMachine().stackValidForSlot(slot, itemstack);
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
			final ItemStack stack = inventoryStacks[slot];
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
		onInventoryChanged();
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	public void onInventoryChanged() {}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	/** Called from {@link infinitealloys.block.BlockMachine#onNeighborChange} when an adjacent TileEntity changes
	 *
	 * @param x the x-coord of the block the changed (not this block)
	 * @param y the y-coord of the block the changed (not this block)
	 * @param z the z-coord of the block the changed (not this block) */
	public void onNeighborChange(int x, int y, int z) {}

	protected abstract void updateUpgrades();

	protected abstract void populateValidUpgrades();

	/** Determines if the given itemstack is a valid upgrade for the machine. Criteria: Does this machine take this type of upgrade? Does this machine already
	 * have this upgrade? Does this upgrade have a prerequisite upgrade and if so, does this machine already have that upgrade?
	 *
	 * @param ItemStack for upgrade item with a binary upgrade damage value (see {@link infinitealloys.util.MachineHelper TEHelper} for upgrade numbers)
	 * @return true if valid */
	public final boolean isUpgradeValid(ItemStack itemstack) {
		if(validUpgradeTypes.contains(itemstack.getItem())) {
			int upgradeType = ((ItemUpgrade)itemstack.getItem()).upgradeType;
			int upgradeTier = itemstack.getItemDamage() + 1;
			return upgrades[upgradeType] + 1 == upgradeTier && !hasUpgrade(upgradeType, upgradeTier);
		}
		return false;
	}

	/** Does the machine have the specified type and tier of upgrade
	 *
	 * @param upgradeType Int representing type of upgrade, see {@link infinitealloys.util.Consts}
	 * @param tier Tier of the upgrade, e.g. 2 for Speed II or 3 for Capacity III
	 * @return true if the machine has the upgrade */
	public boolean hasUpgrade(int upgradeType, int tier) {
		return upgrades[upgradeType] >= tier;
	}

	/** Get the tier of the specified upgrade type for this machine, e.g. if this machine has received Speed I and II and the upgrade type is speed, this will return 2 */
	public int getUpgradeTier(int upgradeType) {
		return upgrades[upgradeType];
	}
}
