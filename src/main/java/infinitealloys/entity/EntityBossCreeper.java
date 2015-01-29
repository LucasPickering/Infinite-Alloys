package infinitealloys.entity;

import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public class EntityBossCreeper extends EntityIABoss {

  public EntityBossCreeper(World world) {
    super(world, EnumBoss.CREEPER);
  }
}
