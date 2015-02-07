package infinitealloys.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;

public class ItemSummoner extends ItemIA {

  public ItemSummoner() {
    super();
    setMaxStackSize(1);
  }

  @Override
  public boolean getShareTag() {
    return true;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
    player.openGui(InfiniteAlloys.instance, Consts.SUMMONER_GUI_ID, world, (int) player.posX,
                   (int) player.posY, (int) player.posZ);
    return itemStack;
  }

  /**
   * Add XP to summoner of the given {@link ItemStack}. If {@code itemStack} does not already have
   * an {@link NBTTagCompound}, create one, then add the XP.
   *
   * @param itemStack the {@link ItemStack} to receive the XP
   * @param xp        the amount of xp to be added
   */
  public void addXP(ItemStack itemStack, int xp) {
    NBTTagCompound tagCompound;
    if (itemStack.hasTagCompound()) {
      tagCompound = itemStack.getTagCompound();
    } else {
      tagCompound = new NBTTagCompound();
    }

    int currentXP = 0;
    if (tagCompound.hasKey("storedXP")) {
      currentXP = tagCompound.getInteger("storedXP");
    }
    tagCompound.setInteger("storedXP", currentXP + xp);
    itemStack.setTagCompound(tagCompound);
  }
}
