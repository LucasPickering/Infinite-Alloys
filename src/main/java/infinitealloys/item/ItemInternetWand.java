package infinitealloys.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;
import infinitealloys.util.MachineHelper;

public final class ItemInternetWand extends Item {

  @Override
  public boolean getShareTag() {
    return true;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
    if (itemstack.hasTagCompound()) {
      itemstack.getTagCompound().removeTag("CoordsCurrent");
    }
    player.openGui(InfiniteAlloys.instance, Consts.WAND_GUI_ID, world, (int) player.posX,
                   (int) player.posY, (int) player.posZ);
    return itemstack;
  }

  /**
   * Is the machine that is at (x, y, z) OK to be added to this wand?
   *
   * @param pos the machine's pos
   * @return true if there is space for the machine, the machine is a valid client, and it does not
   * already exist in the wand
   */
  public boolean isMachineValid(World world, ItemStack itemstack, BlockPos pos) {
    // If the item does not already have a tag compound, create a new one
    NBTTagCompound tagCompound = itemstack.getTagCompound();
    if (tagCompound == null) {
      itemstack.setTagCompound(tagCompound = new NBTTagCompound());
    }

    // If the wand is not full and the machine is a valid remote client
    if (!tagCompound.hasKey("Coords" + (Consts.WAND_SIZE - 1)) &&
        MachineHelper.isClient(world.getTileEntity(pos))) {
      for (int i = 0; i < Consts.WAND_SIZE; i++) { // Iterate over each coord that the wand contains
        if (tagCompound.hasKey("Coords" + i)) {
          // If the wand already contains this machine, return false
          final int[] a = tagCompound.getIntArray("Coords" + i);
          if (a[0] == world.provider.getDimensionId() &&
              a[1] == pos.getX() && a[2] == pos.getY() && a[3] == pos.getZ()) {
            return false;
          }
        }
      }
      return true; // If the machine is not already in the wand, then it is valid
    }

    return false;
  }

  /**
   * Checks if the machine at x, y, z, can be added to the wand at itemstack, then adds it if it can
   */
  public void addMachine(World world, ItemStack itemstack, BlockPos pos) {
    if (itemstack.getItem() == IAItems.internetWand && isMachineValid(world, itemstack,
                                                                      pos)) {
      final NBTTagCompound tagCompound = itemstack.getTagCompound();
      for (int i = 0; i < Consts.WAND_SIZE; i++) {
        if (!tagCompound.hasKey("Coords" + i)) {
          tagCompound.setIntArray("Coords" + i, new int[]{world.provider.getDimensionId(),
                                                          pos.getX(), pos.getY(), pos.getZ()});
          break;
        }
      }
    }
  }

  /**
   * Removes the machine with the given index from the wand
   */
  public void removeMachine(ItemStack itemStack, int index) {
    if (itemStack.getItem() == IAItems.internetWand && itemStack
        .hasTagCompound()) { // If this is a wand and has stored data
      final NBTTagCompound tagCompound = itemStack.getTagCompound();
      tagCompound.removeTag("Coords" + index); // Remove the tag at index
      for (int i = index + 1; i < Consts.WAND_SIZE;
           i++) { // Iterate over each coord below the one that was removed
        if (tagCompound.hasKey("Coords" + i)) { // If this key exists
          tagCompound.setIntArray("Coords" + (i - 1), tagCompound
              .getIntArray("Coords" + i)); // Move this coord up one spot to fill the hole
          tagCompound.removeTag("Coords" + i);
        } else {
          break; // If the tag doesn't exist, break because we have reached the end of the list
        }
      }
    }
  }
}
