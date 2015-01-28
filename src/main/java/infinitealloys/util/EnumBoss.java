package infinitealloys.util;

import net.minecraft.client.model.ModelBase;

import infinitealloys.client.model.entity.ModelBossBat;
import infinitealloys.client.model.entity.ModelBossBlaze;
import infinitealloys.client.model.entity.ModelBossCreeper;
import infinitealloys.client.model.entity.ModelBossSkeleton;
import infinitealloys.client.model.entity.ModelBossSteve;
import infinitealloys.client.model.entity.ModelBossZombie;
import infinitealloys.entity.EntityBossBat;
import infinitealloys.entity.EntityBossBlaze;
import infinitealloys.entity.EntityBossCreeper;
import infinitealloys.entity.EntityBossSkeleton;
import infinitealloys.entity.EntityBossSteve;
import infinitealloys.entity.EntityBossZombie;
import infinitealloys.entity.EntityIABoss;

public enum EnumBoss {

  ZOMBIE("zombie", EntityBossZombie.class, ModelBossZombie.class, 20, EnumAlloy.ALLOY0),
  SKELETON("skeleton", EntityBossSkeleton.class, ModelBossSkeleton.class, 40, EnumAlloy.ALLOY1),
  CREEPER("creeper", EntityBossCreeper.class, ModelBossCreeper.class, 60, EnumAlloy.ALLOY2),
  BLAZE("blaze", EntityBossBlaze.class, ModelBossBlaze.class, 80, EnumAlloy.ALLOY3),
  BAT("bat", EntityBossBat.class, ModelBossBat.class, 100, EnumAlloy.ALLOY4),
  STEVE("steve", EntityBossSteve.class, ModelBossSteve.class, 120, EnumAlloy.ALLOY5);

  public final String name;
  public final Class<? extends EntityIABoss> entityClass;
  public final Class<? extends ModelBase> modelClass;
  /**
   * Amount of total XP it takes to unlock this boss. This is total XP from zero, NOT from the last
   * boss.
   */
  public final int unlockXP;

  /**
   * The alloy that this boss drops.
   */
  public final EnumAlloy alloy;

  private EnumBoss(String name, Class<? extends EntityIABoss> entityClass,
                   Class<? extends ModelBase> modelClass, int unlockXP, EnumAlloy alloy) {
    this.name = name;
    this.entityClass = entityClass;
    this.modelClass = modelClass;
    this.unlockXP = unlockXP;
    this.alloy = alloy;
  }
}
