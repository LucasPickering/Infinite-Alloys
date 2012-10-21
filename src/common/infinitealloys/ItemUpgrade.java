package infinitealloys;

import java.util.List;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class ItemUpgrade extends ItemIA {

	public int requiredUpgrades;

	public ItemUpgrade(int id, int texture) {
		super(id, texture);
		maxStackSize = 1;
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "iaUpgrade";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconIndex(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		return (int)InfiniteAlloys.logn(2, stack.getItemDamage()) + 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, List list) {
		list.add(IAValues.upgradeNames[(int)InfiniteAlloys.logn(2, itemstack.getItemDamage())]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < IAValues.upgradeCount; i++)
			list.add(new ItemStack(id, 1, (int)Math.pow(2D, i)));
	}
}
