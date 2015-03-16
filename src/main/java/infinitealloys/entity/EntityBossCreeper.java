package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public final class EntityBossCreeper extends EntityIABoss {

  public EntityBossCreeper(World world) {
    super(world, EnumBoss.CREEPER.alloy);
    setSize(2F, 7F);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200);
  }
}
