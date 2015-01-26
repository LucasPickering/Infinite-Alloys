package infinitealloys.core;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import infinitealloys.block.IABlocks;
import infinitealloys.util.Consts;

public class WorldGenHandler implements IWorldGenerator {

  private final int[] spawnChance = {1, 1, 1, 1, 2, 2, 3, 3};
  private final int[] heights = {60, 55, 50, 45, 40, 35, 30, 25};
  private final int[] rarities = {8, 7, 6, 5, 4, 3, 2, 1};
  private final int[] groupSizes = {10, 9, 8, 7, 6, 5, 4, 3};

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world,
		       IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      if (!InfiniteAlloys.spawnOres[i] || random.nextInt(spawnChance[i]) == 0) {
	continue;
      }
      for (int j = 0; j < rarities[i]; j++) {
        final int x = chunkX * 16 + random.nextInt(16);
        final int y = random.nextInt(heights[i]);
        final int z = chunkZ * 16 + random.nextInt(16);
        new WorldGenMinable(IABlocks.ore, i, groupSizes[i], Blocks.stone)
            .generate(world, random, x, y, z);
      }
    }
  }
}
