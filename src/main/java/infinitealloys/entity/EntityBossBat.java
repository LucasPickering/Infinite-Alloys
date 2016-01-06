package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public final class EntityBossBat extends EntityIABoss {

  public EntityBossBat(World world) {
    super(world, EnumBoss.BAT.alloy);
    setSize(2.5F, 3.6F);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(300);
  }
}
