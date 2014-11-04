package infinitealloys.entity;

import infinitealloys.util.EnumAlloy;
import net.minecraft.world.World;

public class EntityBossCreeper extends EntityIABoss {

	public EntityBossCreeper(World world) {
		super(world, EnumAlloy.ALLOY2);
		setSize(2F, 8F);
	}
}
