package infinitealloys.entity;

import infinitealloys.util.EnumAlloy;
import net.minecraft.world.World;

public class EntityBossSkeleton extends EntityIABoss {

	public EntityBossSkeleton(World world) {
		super(world, EnumAlloy.ALLOY1);
		setSize(2F, 8F);
	}
}
