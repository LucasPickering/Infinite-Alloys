package infinitealloys.entity;

import infinitealloys.util.EnumAlloy;
import net.minecraft.world.World;

public class EntityBossBlaze extends EntityIABoss {

	public EntityBossBlaze(World world) {
		super(world, EnumAlloy.ALLOY3);
		setSize(2F, 8F);
	}
}
