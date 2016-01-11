package infinitealloys.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import infinitealloys.util.EnumMachine;
import infinitealloys.util.Funcs;

public final class ItemBlockMachine extends ItemBlock {

  public ItemBlockMachine(Block block) {
    super(block);
  }

  @Override
  @SideOnly(Side.CLIENT)
  @SuppressWarnings("unchecked")
  public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
    // If the item has stored data, display it
    if (itemstack.hasTagCompound()) {
      for (String field : EnumMachine.values()[itemstack.getItemDamage()].persistentFields) {
        list.add(Funcs.getLoc("machine.fields." + field) + ": " + Funcs
            .abbreviateNum(itemstack.getTagCompound().getInteger(field)));
      }
    }
  }
}
