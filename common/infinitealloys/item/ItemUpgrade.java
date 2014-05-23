package infinitealloys.item;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;
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
		setUnlocalizedName("IAupgrade");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		Items.upgradeBackground = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradecomponent");
		for(int i = 0; i < Consts.UPGRADE_COUNT; i++)
			Items.upgradeIcons[i] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + EnumUpgrade.values()[i].getName());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamageForRenderPass(int damage, int renderPass) {
		if(renderPass == 1)
			return Items.upgradeIcons[damage];
		return Items.upgradeBackground;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		list.add(Funcs.getLoc("upgrade." + EnumUpgrade.values()[itemstack.getItemDamage()].getName() + ".name"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < Consts.UPGRADE_COUNT; i++)
			list.add(new ItemStack(id, 1, i));
	}
}
