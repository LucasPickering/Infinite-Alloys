package infinitealloys.item;

import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import java.util.List;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class ItemUpgrade extends ItemIA {

	public int requiredUpgrades;

	public ItemUpgrade(int id, int texture) {
		super(id, texture);
		setHasSubtypes(true);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "iaUpgrade";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconFromDamage(int damage) {
		return (int)InfiniteAlloys.logn(2, damage) + 129;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		list.add(InfiniteAlloys.getStringLocalization("upgrade." + References.upgradeNames[(int)InfiniteAlloys.logn(2, itemstack.getItemDamage())] + ".name"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < References.upgradeCount; i++)
			list.add(new ItemStack(id, 1, (int)Math.pow(2D, i)));
	}
}
