package infinitealloys.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import infinitealloys.util.Consts;

public class ItemBlockOre extends ItemBlock {

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
      return "tile.ia" + Consts.METAL_NAMES[itemstack.getItemDamage()] + "Ore";
    }
    return super.getUnlocalizedName(itemstack);
  }
}
