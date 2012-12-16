package infinitealloys.handlers;

import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.block.Blocks;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenHandler implements IWorldGenerator {

	private int[] heights = { 60, 55, 50, 45, 40, 35, 30, 25 };
	private int[] rarities = { 10, 9, 8, 7, 6, 5, 4, 3 };

	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		for(int i = 0; i < References.metalCount; i++) {
			if(!InfiniteAlloys.spawnOres[i])
				continue;
			for(int j = 0; j < rarities[i]; j++) {
				int x = chunkX * 16 + random.nextInt(16);
				int y = random.nextInt(heights[i]);
				int z = chunkZ * 16 + random.nextInt(16);
				new WorldGenMinable(Blocks.ore.blockID, i, 10).generate(world, random, x, y, z);
			}
		}
	}
}
