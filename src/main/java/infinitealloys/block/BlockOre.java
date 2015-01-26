package infinitealloys.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;

public class BlockOre extends Block {

  public BlockOre() {
    super(Material.rock);
  }

  @Override
  public void getSubBlocks(Item item, CreativeTabs creativetabs, List list) {
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      list.add(new ItemStack(item, 1, i));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iconRegister) {
    IABlocks.oreForegroundIcon =
        iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "ore_foreground");
    IABlocks.oreBackgroundIcon =
        iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "ore_background");
  }

  @Override
  public IIcon getIcon(int side, int metadata) {
    return IABlocks.oreBackgroundIcon;
  }

  @Override
  public int getRenderType() {
    return InfiniteAlloys.proxy.gfxHandler.renderID;
  }

  @Override
  public int damageDropped(int damage) {
    return damage;
  }
}
