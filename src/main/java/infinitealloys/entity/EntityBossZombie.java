package infinitealloys.entity;

import infinitealloys.util.EnumAlloy;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityBossZombie extends EntityIABoss {

	public EntityBossZombie(World world) {
		super(world, EnumAlloy.ALLOY0);
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100);
	}
}
