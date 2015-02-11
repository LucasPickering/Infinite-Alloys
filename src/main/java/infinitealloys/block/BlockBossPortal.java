package infinitealloys.block;

import net.minecraft.block.BlockPortal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import infinitealloys.util.Consts;
import infinitealloys.world.TeleporterBoss;

public class BlockBossPortal extends BlockPortal {

  public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    if ((entity.ridingEntity == null) && (entity.riddenByEntity == null)
        && ((entity instanceof EntityPlayerMP))) {
      EntityPlayerMP player = (EntityPlayerMP) entity;
      MinecraftServer mServer = MinecraftServer.getServer();
      if (player.timeUntilPortal > 0) {
        player.timeUntilPortal = 10;
      } else if (player.dimension != Consts.dimensionId) {
        player.timeUntilPortal = 10;
        player.mcServer.getConfigurationManager()
            .transferPlayerToDimension(player, Consts.dimensionId,
                                       new TeleporterBoss(mServer.worldServerForDimension(
                                           Consts.dimensionId)));
      } else {
        player.timeUntilPortal = 10;
        player.mcServer.getConfigurationManager()
            .transferPlayerToDimension(player, 0,
                                       new TeleporterBoss(mServer.worldServerForDimension(1)));
      }
    }
  }
}
