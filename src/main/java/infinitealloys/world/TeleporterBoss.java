package infinitealloys.world;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import infinitealloys.block.IABlocks;

public class TeleporterBoss extends Teleporter {

  private final WorldServer worldServer;

  private final int spawnX = 8;
  private final int spawnY = 10;
  private final int spawnZ = 8;

  public TeleporterBoss(WorldServer worldServer) {
    super(worldServer);
    this.worldServer = worldServer;
  }

  /**
   * {@inheritDoc}
   *
   * @param entity the entity being teleported
   * @param x      x value similar to the entity's, DO NOT USE
   * @param y      y value similar to the entity's, DO NOT USE
   * @param z      z value similar to the entity's, DO NOT USE
   * @param yaw    yaw similar to the entity's, DO NOT USE
   */
  @Override
  public void placeInPortal(Entity entity, double x, double y, double z, float yaw) {
    makePortal(entity);
    entity.setLocationAndAngles(spawnX, spawnY, spawnZ + 1.5D, 0F, 0F);
    entity.motionX = entity.motionY = entity.motionZ = 0D;
  }

  /**
   * Build a portal at the dimension's spawn. Also replaces any blocks that have been modified.
   *
   * @param entity the entity that is being teleported
   * @return true
   */
  @Override
  public boolean makePortal(Entity entity) {
    for (int x = -2; x < 2; x++) {
      for (int y = 0; y < 5; y++) {
        boolean border = x == -2 || x == 1 || y == 0 || y == 4;
        worldServer.setBlock(spawnX + x, spawnY + y, spawnZ,
                             border ? Blocks.sandstone : IABlocks.portal, 0, 2);
      }
    }
    worldServer.notifyBlockOfNeighborChange(spawnX, spawnY, spawnZ, IABlocks.portal);
    return true;
  }
}
