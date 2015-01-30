package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public class EntityBossSkeleton extends EntityIABoss {

  public EntityBossSkeleton(World world) {
    super(world, EnumBoss.SKELETON.alloy);
    setSize(2F, 8F);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(150);
  }
}
