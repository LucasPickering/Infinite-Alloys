package infinitealloys.world;

import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public class ChunkProviderCreeper extends ChunkProviderBoss {

  public ChunkProviderCreeper(World world) {
    super(world, EnumBoss.ZOMBIE);
  }
}
