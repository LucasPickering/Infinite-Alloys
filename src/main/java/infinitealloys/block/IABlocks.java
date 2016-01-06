package infinitealloys.block;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

import infinitealloys.core.InfiniteAlloys;

public final class IABlocks {

  public static final Block ore = new BlockOre().setCreativeTab(InfiniteAlloys.tabIA).setHardness(3f);
  public static final Block machine =
      new BlockMachine().setCreativeTab(InfiniteAlloys.tabIA).setHardness(3f);

  public static IIcon oreForegroundIcon;
  public static IIcon oreBackgroundIcon;
}
