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

  ZOMBIE("zombie", EntityBossZombie.class, new ModelBossZombie(), EnumAlloy.ALLOY0, 20, 100),
  SKELETON("skeleton", EntityBossSkeleton.class, new ModelBossSkeleton(), EnumAlloy.ALLOY1, 40,
           150),
  CREEPER("creeper", EntityBossCreeper.class, new ModelBossCreeper(), EnumAlloy.ALLOY2, 60, 200),
  BLAZE("blaze", EntityBossBlaze.class, new ModelBossBlaze(), EnumAlloy.ALLOY3, 80, 250),
  BAT("bat", EntityBossBat.class, new ModelBossBat(), EnumAlloy.ALLOY4, 100, 300),
  STEVE("steve", EntityBossSteve.class, new ModelBossSteve(), EnumAlloy.ALLOY5, 120, 350);

  public final String name;
  public final Class<? extends EntityIABoss> entityClass;
  public final ModelBase model;
  public final EnumAlloy alloy;
  public final int unlockXP;
  public final int health;

  /**
   * Constructs a new EnumBoss object
   *
   * @param name        the unlocalized name of the boss
   * @param entityClass the class associated with this boss's entity
   * @param model       an instance of this boss's model
   * @param alloy       the alloy type that this boss drops
   * @param unlockXP    the total XP needed to unlock this boss. This is XP from 0, NOT from the
   *                    last boss
   * @param health      the amount of health that this boss has
   */
  private EnumBoss(String name, Class<? extends EntityIABoss> entityClass,
                   ModelBase model, EnumAlloy alloy, int unlockXP,
                   int health) {
    this.name = name;
    this.entityClass = entityClass;
    this.model = model;
    this.unlockXP = unlockXP;
    this.alloy = alloy;
    this.health = health;
  }
}
