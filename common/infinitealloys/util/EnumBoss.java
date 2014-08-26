package infinitealloys.util;

import infinitealloys.client.render.entity.RenderBossBat;
import infinitealloys.client.render.entity.RenderBossBlaze;
import infinitealloys.client.render.entity.RenderBossCreeper;
import infinitealloys.client.render.entity.RenderBossSkeleton;
import infinitealloys.client.render.entity.RenderBossSteve;
import infinitealloys.client.render.entity.RenderBossZombie;
import infinitealloys.entity.EntityBossZombie;
import infinitealloys.entity.EntityBossSkeleton;
import infinitealloys.entity.EntityBossCreeper;
import infinitealloys.entity.EntityBossBlaze;
import infinitealloys.entity.EntityBossBat;
import infinitealloys.entity.EntityBossSteve;
import infinitealloys.entity.EntityIABoss;
import net.minecraft.client.renderer.entity.RenderLiving;

public enum EnumBoss {

	ZOMBIE("zombie", EntityBossZombie.class, RenderBossZombie.class), SKELETON("skeleton", EntityBossSkeleton.class, RenderBossSkeleton.class), CREEPER("creeper", EntityBossCreeper.class, RenderBossCreeper.class),
	BLAZE("blaze", EntityBossBlaze.class, RenderBossBlaze.class), BAT("bat", EntityBossBat.class, RenderBossBat.class), STEVE("steve", EntityBossSteve.class, RenderBossSteve.class);

	private final String name;
	private final Class<? extends EntityIABoss> entityClass;
	private final Class<? extends RenderLiving> renderClass;

	private EnumBoss(String name, Class<? extends EntityIABoss> entityClass, Class<? extends RenderLiving> renderClass) {
		this.name = name;
		this.entityClass = entityClass;
		this.renderClass = renderClass;
	}

	public String getName() {
		return name;
	}

	public Class<? extends EntityIABoss> getEntityClass() {
		return entityClass;
	}

	public Class<? extends RenderLiving> getRenderClass() {
		return renderClass;
	}
}
