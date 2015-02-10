package infinitealloys.world;

import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public class ChunkProviderBlaze extends ChunkProviderBoss {

  public ChunkProviderBlaze(World world) {
    super(world, EnumBoss.ZOMBIE);
  }
}
