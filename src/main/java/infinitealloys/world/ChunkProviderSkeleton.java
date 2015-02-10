package infinitealloys.world;

import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public class ChunkProviderSkeleton extends ChunkProviderBoss {

  public ChunkProviderSkeleton(World world) {
    super(world, EnumBoss.ZOMBIE);
  }
}
