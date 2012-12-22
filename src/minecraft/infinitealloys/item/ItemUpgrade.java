package infinitealloys.item;

import infinitealloys.InfiniteAlloys;
import infinitealloys.References;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemUpgrade extends ItemIA {

	public int requiredUpgrades;

	public ItemUpgrade(int id, int texture) {
		super(id, texture);
		setHasSubtypes(true);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "IAupgrade";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconFromDamage(int damage) {
		return iconIndex + (int)InfiniteAlloys.logn(2, damage);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		list.add(InfiniteAlloys.getLoc("upgrade." + References.upgradeNames[(int)InfiniteAlloys.logn(2, itemstack.getItemDamage())] + ".name"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < References.upgradeCount; i++)
			list.add(new ItemStack(id, 1, (int)Math.pow(2D, i)));
	}
}
