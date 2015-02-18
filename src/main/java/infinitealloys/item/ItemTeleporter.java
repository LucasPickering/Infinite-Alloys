package infinitealloys.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import infinitealloys.util.Point3;
import infinitealloys.world.TeleporterBoss;
import infinitealloys.world.WorldProviderBoss;

public class ItemTeleporter extends ItemIA {

  @Override
  public boolean getShareTag() {
    return true;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    if (entityPlayer.ridingEntity == null && entityPlayer.riddenByEntity == null
        && entityPlayer instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) entityPlayer;
      int destinationDimension;
      Point3 destinationCoords;

      if (!itemStack.hasTagCompound()) {
        createDimension(itemStack);
      }

      NBTTagCompound tagCompound = itemStack.getTagCompound();
      final int bossDimensionId = tagCompound.getInteger("dimensionId");
      if (player.dimension == bossDimensionId) {
        if (tagCompound.hasKey("returnCoords")) {
          int[] destination = tagCompound.getIntArray("returnCoords");
          destinationDimension = destination[0];
          destinationCoords = new Point3(destination[1], destination[2], destination[3]);
        } else {
          destinationDimension = 0;
          ChunkCoordinates spawnPoint =
              MinecraftServer.getServer().worldServerForDimension(0).getSpawnPoint();
          destinationCoords = new Point3(spawnPoint.posX, spawnPoint.posY, spawnPoint.posZ);
        }
      } else {
        tagCompound.setIntArray("returnCoords", new int[]{player.dimension,
                                                          MathHelper.floor_double(player.posX),
                                                          MathHelper.floor_double(player.posY),
                                                          MathHelper.floor_double(player.posZ)});
        destinationDimension = bossDimensionId;
        destinationCoords = new Point3(8, 10, 8);
      }

      player.setPosition(destinationCoords.x, destinationCoords.y, destinationCoords.z);
      MinecraftServer mServer = MinecraftServer.getServer();
      mServer.getConfigurationManager()
          .transferPlayerToDimension(player,
                                     destinationDimension,
                                     new TeleporterBoss(
                                         mServer.worldServerForDimension(destinationDimension)));
    }
    return itemStack;
  }

  /**
   * Create a new dimension for the teleporter with the given {@link ItemStack}.
   *
   * @param itemStack the teleporter attached to the dimension
   */
  private void createDimension(ItemStack itemStack) {
    final int dimensionId = DimensionManager.getNextFreeDimId();
    DimensionManager.registerProviderType(dimensionId, WorldProviderBoss.class, false);
    DimensionManager.registerDimension(dimensionId, dimensionId);
    NBTTagCompound tagCompound = new NBTTagCompound();
    itemStack.setTagCompound(tagCompound);
    tagCompound.setInteger("dimensionId", dimensionId);
  }
}
