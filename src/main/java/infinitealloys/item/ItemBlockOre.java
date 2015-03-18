package infinitealloys.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

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
  public String getUnlocalizedName(ItemStack itemstack) {
    if (itemstack.getItemDamage() < Consts.METAL_COUNT) {
      return "tile." + EnumMetal.values()[itemstack.getItemDamage()].name + "Ore";
    }
    return super.getUnlocalizedName(itemstack);
  }
}
