package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public class EntityBossSteve extends EntityIABoss {

  public EntityBossSteve(World world) {
    super(world, EnumBoss.STEVE.alloy);
    setSize(2F, 8F);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(350);
  }
}
