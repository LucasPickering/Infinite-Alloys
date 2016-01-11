package infinitealloys.block;

import net.minecraft.block.Block;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;

public final class IABlocks {

  public static final Block ore =
      new BlockOre().setCreativeTab(InfiniteAlloys.creativeTab).setHardness(3f);
  public static final Block[] machines = new Block[Consts.MACHINE_COUNT];

}
