package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import infinitealloys.util.EnumAlloy;

public class EntityBossZombie extends EntityIABoss {

  public EntityBossZombie(World world) {
    super(world, EnumAlloy.ALLOY0);
  }

  @Override
  public void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100);
  }
}
