package infinitealloys.world;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumBoss;

public class WorldProviderBoss extends WorldProvider {

  private final EnumBoss bossType;

  public WorldProviderBoss() {
    this.bossType = EnumBoss.ZOMBIE;
  }

  public WorldProviderBoss(EnumBoss bossType) {
    this.bossType = bossType;
  }

  @Override
  public String getDimensionName() {
    return bossType.name;
  }

  @Override
  public void registerWorldChunkManager() {
    worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.desert, 0F);
    dimensionId = Consts.dimensionId;
  }

  @Override
  public IChunkProvider createChunkGenerator() {
    return bossType.getNewChunkProvider(worldObj);
  }
}
