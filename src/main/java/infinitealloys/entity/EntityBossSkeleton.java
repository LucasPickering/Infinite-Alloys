package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import infinitealloys.util.EnumAlloy;

public class EntityBossSkeleton extends EntityIABoss {

  public EntityBossSkeleton(World world) {
    super(world, EnumAlloy.ALLOY1);
  }

  @Override
  public void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(150);
  }
}
