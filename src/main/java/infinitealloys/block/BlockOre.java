package infinitealloys.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumMetal;

public final class BlockOre extends Block {

  private static final PropertyEnum METAL_PROP = PropertyEnum.create("metal", EnumMetal.class);

  public BlockOre() {
    super(Material.rock);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void getSubBlocks(Item item, CreativeTabs creativetabs, List list) {
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      list.add(new ItemStack(item, 1, i));
    }
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, METAL_PROP);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(METAL_PROP, EnumMetal.byMetadata(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return ((EnumMetal) state.getValue(METAL_PROP)).ordinal();
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
    if (renderPass == 1) {
      return EnumMetal.byMetadata(getMetaFromState(world.getBlockState(pos))).color;
    }
    return 0xffffff;
  }
}
