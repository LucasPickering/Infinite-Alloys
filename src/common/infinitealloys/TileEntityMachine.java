package infinitealloys;

import java.util.Random;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;

public abstract class TileEntityMachine extends TileEntity {

	/**
	 * This block's current network ID
	 */
	public byte networkID;

	/**
	 * A binary integer used to determine what upgrades have been installed.
	 */
	public int upgrades;

	/**
	 * Byte corresponding to the block's orientation on placement. 0123 = SWNE
	 */
	public byte orientation;

	/**
	 * Determines if the current item is capable of upgrading the machine. If it
	 * is, it will upgrade.
	 * 
	 * @param inventoryPlayer
	 * @return Upgrade valid
	 */
	public boolean upgrade(InventoryPlayer inventoryPlayer) {
		ItemStack upgrade = inventoryPlayer.getCurrentItem();
		if(!isUpgradeValid(upgrade))
			return false;
		int damage = upgrade.getItemDamage();
		if((damage | upgrades) != upgrades && (((damage >> 1) | upgrades) == upgrades || damage == 1 || damage == 32 || damage == 256 || damage == 2048)) {
			upgrades |= damage;
			inventoryPlayer.decrStackSize(inventoryPlayer.currentItem, 1);
			return true;
		}
		return false;
	}

	/**
	 * Drops the upgrades that were used on the block as items, called when the
	 * block is broken
	 * 
	 * @param random
	 */
	public void dropUpgrades(Random random) {
		for(int i = 0; i <= IAValues.upgradeCount; i++) {
			int upg = (int)Math.pow(2D, (double)i);
			if((upg & upgrades) == upg) {
				float f = random.nextFloat() * 0.8F + 0.1F;
				float f1 = random.nextFloat() * 0.8F + 0.1F;
				float f2 = random.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem = new EntityItem(worldObj, (double)((float)xCoord + f), (double)((float)yCoord + f1), (double)((float)zCoord + f2), new ItemStack(InfiniteAlloys.upgrade, 1, upg));
				entityitem.motionX = (double)((float)random.nextGaussian() * 0.05F);
				entityitem.motionY = (double)((float)random.nextGaussian() * 0.05F + 0.2F);
				entityitem.motionZ = (double)((float)random.nextGaussian() * 0.05F);
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
	public abstract boolean isUpgradeValid(ItemStack upgrade);

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		networkID = nbttagcompound.getByte("NetworkID");
		upgrades = nbttagcompound.getShort("Upgrades");
		orientation = nbttagcompound.getByte("Orientation");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setByte("NetworkID", networkID);
		nbttagcompound.setShort("Upgrades", (short)upgrades);
		nbttagcompound.setByte("Orientation", (byte)orientation);
	}

	@Override
	public Packet getDescriptionPacket() {
		return CommonProxy.getPacket(this);
	}

	public void handlePacketData(byte orientation, byte networkID) {
		this.orientation = orientation;
		this.networkID = networkID;
	}
}
