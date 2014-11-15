package infinitealloys.util;

import infinitealloys.client.model.entity.*;
import infinitealloys.entity.*;
import net.minecraft.client.model.ModelBase;

public enum EnumBoss {

	ZOMBIE("zombie", EntityBossZombie.class, ModelBossZombie.class, 20),
	SKELETON("skeleton", EntityBossSkeleton.class, ModelBossSkeleton.class, 40),
	CREEPER("creeper", EntityBossCreeper.class, ModelBossCreeper.class, 60),
	BLAZE("blaze", EntityBossBlaze.class, ModelBossBlaze.class, 80),
	BAT("bat", EntityBossBat.class, ModelBossBat.class, 100),
	STEVE("steve", EntityBossSteve.class, ModelBossSteve.class, 120);

	public final String name;
	public final Class<? extends EntityIABoss> entityClass;
	public final Class<? extends ModelBase> modelClass;
	/** Amount of total XP it takes to unlock this boss. This is total XP from zero, NOT from the last boss. */
	public final int unlockXP;

	private EnumBoss(String name, Class<? extends EntityIABoss> entityClass, Class<? extends ModelBase> modelClass, int unlockXP) {
		this.name = name;
		this.entityClass = entityClass;
		this.modelClass = modelClass;
		this.unlockXP = unlockXP;
	}
}
