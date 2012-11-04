package infinitealloys.handlers;

import infinitealloys.IAWorldData;
import infinitealloys.References;
import infinitealloys.InfiniteAlloys;
import java.util.Random;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenMinable;
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
				new WorldGenMinable(InfiniteAlloys.ore.blockID, i, 10).generate(world, random, x, y, z);
			}
		}
		int[] validAlloys = new int[References.validAlloyCount];
		for(int i = 0; i < References.validAlloyCount; i++)
			validAlloys[i] = References.validAlloyMins[i] + random.nextInt(References.validAlloyMaxes[i] - References.validAlloyMins[i]);
		InfiniteAlloys.instance.worldData = new IAWorldData(validAlloys);
	}
}
