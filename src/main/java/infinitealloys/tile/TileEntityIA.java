package infinitealloys.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.Random;

import infinitealloys.block.BlockIA;
import infinitealloys.network.MessageTEToClient;
import infinitealloys.network.MessageTEToServer;
import infinitealloys.network.NetworkHandler;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point3;

/**
 * A base class for Tile Entities that need networking, NBT-data saving, and a specific orientation
 */
public abstract class TileEntityIA extends TileEntity {

  /**
   * A direction representing which way this block is facing.
   */
  public EnumFacing orientation = EnumFacing.EAST;

  /**
   * Called when the block is first placed to restore persistent data from before it was destroyed.
   * This can be data such as the stored RK in an ESU
   */
  public void loadNBTData(NBTTagCompound tagCompound) {
  }

  /**
   * An NBTTagCompound to be attached to the ItemStack that is dropped when the machine is
   * destroyed. This can be data such as the stored RK in an ESU.
   */
  protected NBTTagCompound getDropTagCompound() {
    return null;
  }

  /**
   * Called when the TE's block is destroyed. Ends network connections and drops items and upgrades
   */
  public void onBlockDestroyed() {
    // Drop block with stored NBT data
    ItemStack block = new ItemStack(BlockIA.machine, 1, blockMetadata);
    NBTTagCompound tagCompound = getDropTagCompound();
    if (tagCompound != null) {
      block.setTagCompound(tagCompound);
    }
    spawnItem(block);
  }

  /**
   * Spawn an EntityItem for an ItemStack
   */
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
    System.out.println("Before reading, orientation is " + orientation);
    orientation = EnumFacing.values()[tagCompound.getInteger("orientation")];
    System.out.println("After reading, orientation is " + orientation);
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    System.out.println("Writing orientation as " + orientation);
    tagCompound.setInteger("orientation", orientation.ordinal());
  }

  public void syncToServer() {
    Funcs.sendPacketToServer(new MessageTEToServer(this));
  }

  @Override
  public Packet getDescriptionPacket() {
    return NetworkHandler.simpleNetworkWrapper.getPacketFrom(new MessageTEToClient(this));
  }

  /**
   * A list of the data that gets sent from server to client over the network
   */
  public Object[] getSyncDataToClient() {
    return new Object[]{(byte) orientation.ordinal()};
  }

  /**
   * A list of the data that gets sent from client to server over the network
   */
  public Object[] getSyncDataToServer() {
    return null;
  }

  public void handlePacketDataFromServer(byte facingDir) {
    this.orientation = EnumFacing.values()[facingDir];
    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

  /**
   * Get the current (x, y, z) coordinates of this machine in the form of a {@link
   * infinitealloys.util.Point3 Point3}
   */
  public Point3 coords() {
    return new Point3(xCoord, yCoord, zCoord);
  }
}
