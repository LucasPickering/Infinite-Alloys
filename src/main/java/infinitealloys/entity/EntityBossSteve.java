package infinitealloys.entity;

import infinitealloys.util.EnumAlloy;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityBossSteve extends EntityIABoss {

	public EntityBossSteve(World world) {
		super(world, EnumAlloy.ALLOY5);
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(350);
	}
}
