package infinitealloys.item;

import infinitealloys.util.FuncHelper;
import infinitealloys.util.References;
import java.util.List;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemUpgrade extends ItemIA {

	public int requiredUpgrades;

	public ItemUpgrade(int id) {
		super(id);
		setHasSubtypes(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void func_94581_a(IconRegister iconRegister) {
		for(int i = 0; i < References.UPGRADE_COUNT; i++)
			Items.upgradeIcons[i] = iconRegister.func_94245_a("IAupgrade@" + i);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int damage) {
		return Items.upgradeIcons[damage];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		list.add(FuncHelper.getLoc("upgrade." + References.upgradeNames[itemstack.getItemDamage()] + ".name"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < References.UPGRADE_COUNT; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return "IAupgrade";
	}
}
