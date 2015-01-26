package infinitealloys.entity;

import infinitealloys.util.EnumAlloy;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityBossBlaze extends EntityIABoss {

	public EntityBossBlaze(World world) {
		super(world, EnumAlloy.ALLOY3);
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(250);
	}
}
