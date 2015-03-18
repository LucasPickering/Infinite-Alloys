package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public final class EntityBossZombie extends EntityIABoss {

  public EntityBossZombie(World world) {
    super(world, EnumBoss.ZOMBIE.alloy);
    setSize(2F, 8F);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100);
  }
}
