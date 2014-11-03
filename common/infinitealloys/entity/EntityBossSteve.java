package infinitealloys.entity;

import infinitealloys.util.EnumAlloy;
import net.minecraft.world.World;

public class EntityBossSteve extends EntityIABoss {

	public EntityBossSteve(World world) {
		super(world, EnumAlloy.ALLOY5);
		setSize(2F, 8F);
	}
}
