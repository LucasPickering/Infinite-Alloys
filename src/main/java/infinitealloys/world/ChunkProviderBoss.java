package infinitealloys.world;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.ArrayList;
import java.util.List;

import infinitealloys.util.EnumBoss;

/**
 * An {@link IChunkProvider} for dimensions that contain an IA boss. These dimensions have static
 * terrain (no generation) that is defined by a JSON file.
 */
public class ChunkProviderBoss implements IChunkProvider {

  private World worldObj;
  private final EnumBoss bossType;
  private final Block[] cachedBlockIDs = new Block[256];
  private final byte[] cachedBlockMetadata = new byte[256];

  public ChunkProviderBoss(World world, EnumBoss bossType) {
    this.worldObj = world;
    this.bossType = bossType;
  }

  /**
   * Load or generate the chunk at the chunk location specified.
   */
  @Override
  public Chunk loadChunk(int x, int z) {
    return provideChunk(x, z);
  }

  /**
   * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all
   * the blocks for the specified chunk from the map seed and chunk seed
   */
  @Override
  public Chunk provideChunk(int x, int z) {
    Chunk chunk = new Chunk(this.worldObj, x, z);
    int l;

    for (int k = 0; k < this.cachedBlockIDs.length; ++k) {
      Block block = this.cachedBlockIDs[k];

      if (block != null) {
        l = k >> 4;
        ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[l];

        if (extendedblockstorage == null) {
          extendedblockstorage = new ExtendedBlockStorage(k, !this.worldObj.provider.hasNoSky);
          chunk.getBlockStorageArray()[l] = extendedblockstorage;
        }

        for (int i1 = 0; i1 < 16; ++i1) {
          for (int j1 = 0; j1 < 16; ++j1) {
            extendedblockstorage.func_150818_a(i1, k & 15, j1, block);
            extendedblockstorage.setExtBlockMetadata(i1, k & 15, j1, this.cachedBlockMetadata[k]);
          }
        }
      }
    }

    chunk.generateSkylightMap();
    BiomeGenBase[] abiomegenbase =
        worldObj.getWorldChunkManager().loadBlockGeneratorData(null, x * 16, z * 16, 16, 16);
    byte[] abyte = chunk.getBiomeArray();

    for (l = 0; l < abyte.length; ++l) {
      abyte[l] = (byte) abiomegenbase[l].biomeID;
    }

    chunk.generateSkylightMap();
    return chunk;
  }

  /**
   * Checks to see if a chunk exists at x, z
   */
  @Override
  public boolean chunkExists(int x, int z) {
    return true;
  }

  /**
   * Populates chunk with ores etc.
   */
  @Override
  public void populate(IChunkProvider chunkProvider, int x, int z) {
    int k = x * 16;
    int l = z * 16;
    BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(k + 16, l + 16);
  }

  /**
   * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up
   * to two chunks.
   *
   * @return true if all chunks have been saved, false otherwise
   */
  @Override
  public boolean saveChunks(boolean saveAll, IProgressUpdate progressUpdate) {
    return true;
  }

  /**
   * Save extra data not associated with any Chunk.  Not saved during autosave, only during world
   * unload.  Currently unimplemented.
   */
  @Override
  public void saveExtraData() {
  }

  /**
   * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such
   * chunk.
   */
  @Override
  public boolean unloadQueuedChunks() {
    return false;
  }

  /**
   * Returns if the IChunkProvider supports saving.
   */
  @Override
  public boolean canSave() {
    return true;
  }

  /**
   * Converts the instance data to a readable string.
   */
  @Override
  public String makeString() {
    return bossType.name + "LevelSource";
  }

  /**
   * Returns a list of creatures of the specified type that can spawn at the given location.
   */
  @Override
  public List getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
    return new ArrayList();
  }

  @Override
  public ChunkPosition func_147416_a(World world, String p_147416_2_, int p_147416_3_,
                                     int p_147416_4_, int p_147416_5_) {
    return null;
  }

  @Override
  public int getLoadedChunkCount() {
    return 0;
  }

  @Override
  public void recreateStructures(int x, int z) {
  }
}