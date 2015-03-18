package infinitealloys.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public final class TeleporterBoss extends Teleporter {

  public TeleporterBoss(WorldServer worldServer) {
    super(worldServer);
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
    entity.motionX = entity.motionY = entity.motionZ = 0D;
  }
}
