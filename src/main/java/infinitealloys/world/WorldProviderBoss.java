package infinitealloys.world;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;

public final class WorldProviderBoss extends WorldProvider {

  @Override
  public String getDimensionName() {
    return "InfiniteAlloys";
  }

  @Override
  public void registerWorldChunkManager() {
    worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.desert, 0F);
  }

  @Override
  public IChunkProvider createChunkGenerator() {
    return new ChunkProviderBoss(worldObj);
  }

  @Override
  public boolean canRespawnHere() {
    return false;
  }
}
