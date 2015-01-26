package infinitealloys.entity;

import infinitealloys.util.EnumAlloy;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityBossCreeper extends EntityIABoss {

	public EntityBossCreeper(World world) {
		super(world, EnumAlloy.ALLOY2);
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200);
	}
}
