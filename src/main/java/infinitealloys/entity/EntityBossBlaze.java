package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public class EntityBossBlaze extends EntityIABoss {

  public EntityBossBlaze(World world) {
    super(world, EnumBoss.BLAZE);
  }

  @Override
  public void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(250);
  }
}
