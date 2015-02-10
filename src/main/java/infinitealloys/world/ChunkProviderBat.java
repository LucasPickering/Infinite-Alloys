package infinitealloys.world;

import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public class ChunkProviderBat extends ChunkProviderBoss {

  public ChunkProviderBat(World world) {
    super(world, EnumBoss.ZOMBIE);
  }
}
