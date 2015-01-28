package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public class EntityBossZombie extends EntityIABoss {

  public EntityBossZombie(World world) {
    super(world, EnumBoss.ZOMBIE);
  }

  @Override
  public void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100);
  }
}
