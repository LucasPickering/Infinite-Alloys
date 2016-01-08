package infinitealloys.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumMetal;

public final class ItemBlockOre extends ItemBlock {

  public ItemBlockOre(Block block) {
    super(block);
    setHasSubtypes(true);
  }

  @Override
  public int getMetadata(int i) {
    return i;
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    if (stack.getItemDamage() < Consts.METAL_COUNT) {
      return "tile." + EnumMetal.values()[stack.getItemDamage()].name + "Ore";
    }
    return "tile.ore";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack stack, int renderPass) {
    if (renderPass == 1) {
      return EnumMetal.byMetadata(stack.getItemDamage()).color;
    }
    return 0xffffff;
  }
}
