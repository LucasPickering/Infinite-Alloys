package infinitealloys.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;

import infinitealloys.core.InfiniteAlloys;

public class BlockIA extends Block {

  public static Block ore;
  public static Block machine;

  public static IIcon oreForegroundIcon;
  public static IIcon oreBackgroundIcon;

  public BlockIA(Material material) {
    super(material);
    setCreativeTab(InfiniteAlloys.tabIA);
  }
}
