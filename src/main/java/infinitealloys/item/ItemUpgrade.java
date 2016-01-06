package infinitealloys.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import infinitealloys.util.EnumUpgrade;

public class ItemUpgrade extends Item {

  public final EnumUpgrade upgradeType;

  public ItemUpgrade(EnumUpgrade upgradeType) {
    super();
    this.upgradeType = upgradeType;
    setUnlocalizedName(upgradeType.name);
    setHasSubtypes(true);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
    for (int i = 0; i < upgradeType.tiers; i++) {
      list.add(new ItemStack(item, 1, i));
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack itemstack) {
    if (itemstack.getItemDamage() < upgradeType.tiers) {
      return "item." + upgradeType.name + (itemstack.getItemDamage() + 1);
    }
    return getUnlocalizedName();
  }
}
