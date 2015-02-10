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

import java.util.List;

import infinitealloys.util.EnumBoss;

/**
 * An {@link IChunkProvider} for dimensions that contain an IA boss. These dimensions have static
 * terrain (no generation) that is defined by a JSON file.
 */
public abstract class ChunkProviderBoss implements IChunkProvider {

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
   * the
   * blocks for the
   * specified chunk from the map seed and chunk seed
   */
  public Chunk provideChunk(int p_73154_1_, int p_73154_2_) {
    Chunk chunk = new Chunk(this.worldObj, p_73154_1_, p_73154_2_);
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
    BiomeGenBase[]
        abiomegenbase =
        this.worldObj.getWorldChunkManager()
            .loadBlockGeneratorData((BiomeGenBase[]) null, p_73154_1_ * 16, p_73154_2_ * 16, 16,
                                    16);
    byte[] abyte = chunk.getBiomeArray();

    for (l = 0; l < abyte.length; ++l) {
      abyte[l] = (byte) abiomegenbase[l].biomeID;
    }

    chunk.generateSkylightMap();
    return chunk;
  }

  /**
   * Checks to see if a chunk exists at x, y
   */
  public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
    return true;
  }

  /**
   * Populates chunk with ores etc etc
   */
  public void populate(IChunkProvider chunkProvider, int p_73153_2_, int p_73153_3_) {
    int k = p_73153_2_ * 16;
    int l = p_73153_3_ * 16;
    BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(k + 16, l + 16);
  }

  /**
   * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up
   * to
   * two chunks.
   * Return true if all chunks have been saved.
   */
  public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
    return true;
  }

  /**
   * Save extra data not associated with any Chunk.  Not saved during autosave, only during world
   * unload.  Currently
   * unimplemented.
   */
  public void saveExtraData() {
  }

  /**
   * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such
   * chunk.
   */
  public boolean unloadQueuedChunks() {
    return false;
  }

  /**
   * Returns if the IChunkProvider supports saving.
   */
  public boolean canSave() {
    return true;
  }

  /**
   * Converts the instance data to a readable string.
   */
  public String makeString() {
    return bossType.name + "LevelSource";
  }

  /**
   * Returns a list of creatures of the specified type that can spawn at the given location.
   */
  public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_,
                                   int p_73155_4_) {
    BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(p_73155_2_, p_73155_4_);
    return biomegenbase.getSpawnableList(p_73155_1_);
  }

  public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_,
                                     int p_147416_4_, int p_147416_5_) {
    return null;
  }

  public int getLoadedChunkCount() {
    return 0;
  }

  public void recreateStructures(int x, int z) {
  }
}