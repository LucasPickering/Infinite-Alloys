package infinitealloys.util;

import net.minecraft.client.model.ModelBase;
import net.minecraft.world.World;

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
import infinitealloys.world.ChunkProviderBat;
import infinitealloys.world.ChunkProviderBlaze;
import infinitealloys.world.ChunkProviderBoss;
import infinitealloys.world.ChunkProviderCreeper;
import infinitealloys.world.ChunkProviderSkeleton;
import infinitealloys.world.ChunkProviderSteve;
import infinitealloys.world.ChunkProviderZombie;

public enum EnumBoss {

  ZOMBIE("zombie", EntityBossZombie.class, new ModelBossZombie(), EnumAlloy.ALLOY0, 20,
         ChunkProviderZombie.class),
  SKELETON("skeleton", EntityBossSkeleton.class, new ModelBossSkeleton(), EnumAlloy.ALLOY1, 40,
           ChunkProviderSkeleton.class),
  CREEPER("creeper", EntityBossCreeper.class, new ModelBossCreeper(), EnumAlloy.ALLOY2, 60,
          ChunkProviderCreeper.class),
  BLAZE("blaze", EntityBossBlaze.class, new ModelBossBlaze(), EnumAlloy.ALLOY3, 80,
        ChunkProviderBlaze.class),
  BAT("bat", EntityBossBat.class, new ModelBossBat(), EnumAlloy.ALLOY4, 100,
      ChunkProviderBat.class),
  STEVE("steve", EntityBossSteve.class, new ModelBossSteve(), EnumAlloy.ALLOY5, 120,
        ChunkProviderSteve.class);

  public final String name;
  public final Class<? extends EntityIABoss> entityClass;
  public final ModelBase model;
  public final EnumAlloy alloy;
  public final int portalXP;
  private final Class<? extends ChunkProviderBoss> chunkProviderClass;

  /**
   * Constructs a new EnumBoss object
   * h
   *
   * @param name               the unlocalized name of the boss
   * @param entityClass        the class associated with this boss's entity
   * @param model              an instance of this boss's model
   * @param alloy              the alloy type that this boss drops
   * @param portalXP           the XP needed to enter the portal to this boss's dimension
   * @param chunkProviderClass the class for this boss's {@link ChunkProviderBoss} sub-class
   */
  private EnumBoss(String name, Class<? extends EntityIABoss> entityClass, ModelBase model,
                   EnumAlloy alloy, int portalXP,
                   Class<? extends ChunkProviderBoss> chunkProviderClass) {
    this.name = name;
    this.entityClass = entityClass;
    this.model = model;
    this.portalXP = portalXP;
    this.alloy = alloy;
    this.chunkProviderClass = chunkProviderClass;
  }

  /**
   * Create a new instance of this boss's {@link ChunkProviderBoss} sub-class.
   *
   * @param world the world that the chunk provider will be a part of
   * @return a new instance of {@link #chunkProviderClass}, or null if there is an exception
   */
  public ChunkProviderBoss getNewChunkProvider(World world) {
    try {
      return chunkProviderClass.getConstructor(World.class).newInstance(world);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
