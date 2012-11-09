package infinitealloys;

import infinitealloys.handlers.PacketHandler;
import java.util.Random;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public abstract class TileEntityMachine extends TileEntity implements ISidedInventory {

	public ItemStack[] inventoryStacks;

	/**
	 * A binary integer used to determine what upgrades have been installed
	 */
	public int upgrades;

	/**
	 * Byte corresponding to the block's orientation on placement. 0123 = SWNE
	 */
	public byte orientation = 2;

	/**
	 * The index of the slot that upgrades are placed in
	 */
	public int upgradeSlotIndex = 0;

	/**
	 * True if this machine can be accessed wirelessly
	 */
	public boolean canNetwork;

	public TileEntityMachine(int index) {
		upgradeSlotIndex = index;
	}

	public TileEntityMachine() {}

	@Override
	public void updateEntity() {
		updateUpgrades();
		if(inventoryStacks[upgradeSlotIndex] != null && isUpgradeValid(inventoryStacks[upgradeSlotIndex])) {
			upgrade(inventoryStacks[upgradeSlotIndex]);
			inventoryStacks[upgradeSlotIndex] = null;
		}
		BlockMachine.updateBlockState(worldObj, xCoord, yCoord, zCoord);
	}

	/**
	 * Determines if the current item is capable of upgrading the machine and
	 * upgrades if it is
	 * 
	 * @param inventoryPlayer
	 * @return Upgrade valid
	 */
	public void upgrade(ItemStack upgrade) {
		if(isUpgradeValid(upgrade))
			upgrades |= upgrade.getItemDamage();
	}

	/**
	 * Drops the upgrades that were used on the block as items, called when the
	 * block is broken
	 * 
	 * @param random
	 */
	public void dropUpgrades(Random random) {
		for(int i = 0; i <= References.upgradeCount; i++) {
			int upg = (int)Math.pow(2D, i);
			if((upg & upgrades) == upg) {
				float f = random.nextFloat() * 0.8F + 0.1F;
				float f1 = random.nextFloat() * 0.8F + 0.1F;
				float f2 = random.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem = new EntityItem(worldObj, xCoord + f, yCoord + f1, zCoord + f2, new ItemStack(InfiniteAlloys.upgrade, 1, upg));
				entityitem.motionX = (float)random.nextGaussian() * 0.05F;
				entityitem.motionY = (float)random.nextGaussian() * 0.05F + 0.2F;
				entityitem.motionZ = (float)random.nextGaussian() * 0.05F;
				worldObj.spawnEntityInWorld(entityitem);
			}
		}
	}

	/**
	 * Determines if the given itemstack is a valid upgrade for the machine
	 * 
	 * @param upgrade
	 * @return true if valid
	 */
	public boolean isUpgradeValid(ItemStack upgrade) {
		int damage = upgrade.getItemDamage();
		return upgrade.itemID == InfiniteAlloys.upgrade.shiftedIndex && (!hasPrereqUpgrade(upgrade) || ((damage >> 1) | upgrades) == upgrades) && (damage | upgrades) != upgrades;
	}

	/**
	 * Updates all values that are dependent on upgrades
	 */
	protected abstract void updateUpgrades();

	/**
	 * Is the upgrade a prerequisite for another
	 * 
	 * @param upgrade
	 * @return true if it is a prereq
	 */
	public boolean isPrereqUpgrade(ItemStack upgrade) {
		int damage = upgrade.getItemDamage();
		return upgrade.itemID == InfiniteAlloys.upgrade.shiftedIndex & (damage == 1 || damage == 4 || damage == 16 || damage == 64);
	}

	/**
	 * Does the upgrade require another to work?
	 * 
	 * @param upgrade
	 * @return true if it has a prereq
	 */
	public boolean hasPrereqUpgrade(ItemStack upgrade) {
		int damage = upgrade.getItemDamage();
		return upgrade.itemID == InfiniteAlloys.upgrade.shiftedIndex && (damage == 2 || damage == 8 || damage == 32 || damage == 128);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		upgrades = tagCompound.getShort("Upgrades");
		orientation = tagCompound.getByte("Orientation");
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
		tagCompound.setByte("Orientation", orientation);
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
		return PacketHandler.getTEPacketToClient(this);
	}

	public void handlePacketDataFromServer(byte orientation, int upgrades) {
		this.orientation = orientation;
		this.upgrades = upgrades;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
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
}
