package infinitealloys.tile;

import infinitealloys.block.IABlocks;
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
import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

/** A base, abstract class for Tile Entities, with methods for networking and NBT-data saving */
public abstract class TileEntityIA extends TileEntity {

	/** A number from 0-5 to represent which side of this block gets the front texture */
	public byte orientation;

	/** Called when the block is first placed to restore persistent data from before it was destroyed. This can be data such as the stored RK in an ESU */
	public void loadNBTData(NBTTagCompound tagCompound) {}

	/** An NBTTagCompound to be attached to the ItemStack that is dropped when the machine is destroyed. This can be data such as the stored RK in an ESU. */
	protected NBTTagCompound getDropTagCompound() {
		return null;
	}

	/** Called when the TE's block is destroyed. Ends network connections and drops items and upgrades */
	public void onBlockDestroyed() {
		// Drop block with stored NBT data
		ItemStack block = new ItemStack(IABlocks.machine, 1, blockMetadata);
		NBTTagCompound tagCompound = getDropTagCompound();
		if(tagCompound != null)
			block.setTagCompound(tagCompound);
		spawnItem(block);
	}

	/** Spawn an EntityItem for an ItemStack */
	protected void spawnItem(ItemStack itemstack) {
		Random random = new Random();
		float f = random.nextFloat() * 0.8F + 0.1F;
		float f1 = random.nextFloat() * 0.8F + 0.1F;
		float f2 = random.nextFloat() * 0.8F + 0.1F;
		EntityItem item = new EntityItem(worldObj, xCoord + f, yCoord + f1, zCoord + f2, itemstack);
		item.motionX = random.nextGaussian() * 0.05F;
		item.motionY = random.nextGaussian() * 0.25F;
		item.motionZ = random.nextGaussian() * 0.05F;
		worldObj.spawnEntityInWorld(item);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		orientation = tagCompound.getByte("orientation");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setByte("orientation", orientation);
	}

	public void syncToServer() {
		Funcs.sendPacketToServer(new MessageTEToServer(this));
	}

	@Override
	public Packet getDescriptionPacket() {
		return NetworkHandler.simpleNetworkWrapper.getPacketFrom(new MessageTEToClient(this));
	}

	/** A list of the data that gets sent from server to client over the network */
	public Object[] getSyncDataToClient() {
		return new Object[] { orientation };
	}

	/** A list of the data that gets sent from client to server over the network */
	public Object[] getSyncDataToServer() {
		return null;
	}

	public void handlePacketDataFromServer(byte orientation) {
		this.orientation = orientation;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	/** Get the current (x, y, z) coordinates of this machine in the form of a {@link infinitealloys.util.Point Point} */
	public Point coords() {
		return new Point(xCoord, yCoord, zCoord);
	}
}