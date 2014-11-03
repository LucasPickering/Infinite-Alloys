package infinitealloys.util;

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
import net.minecraft.client.model.ModelBase;

public enum EnumBoss {

	ZOMBIE("zombie", EntityBossZombie.class, ModelBossZombie.class),
	SKELETON("skeleton", EntityBossSkeleton.class, ModelBossSkeleton.class),
	CREEPER("creeper", EntityBossCreeper.class, ModelBossCreeper.class),
	BLAZE("blaze", EntityBossBlaze.class, ModelBossBlaze.class),
	BAT("bat", EntityBossBat.class, ModelBossBat.class),
	STEVE("steve", EntityBossSteve.class, ModelBossSteve.class);

	public final String name;
	public final Class<? extends EntityIABoss> entityClass;
	public final Class<? extends ModelBase> modelClass;

	private EnumBoss(String name, Class<? extends EntityIABoss> entityClass, Class<? extends ModelBase> modelClass) {
		this.name = name;
		this.entityClass = entityClass;
		this.modelClass = modelClass;
	}
}
