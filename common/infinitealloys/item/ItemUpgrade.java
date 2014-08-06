package infinitealloys.item;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumUpgradeType;
import infinitealloys.util.Funcs;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemUpgrade extends ItemIA {

	public int requiredUpgrades;

	public ItemUpgrade() {
		super();
		setHasSubtypes(true);
		setUnlocalizedName("IAupgrade");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		IAItems.upgradeBackground = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "upgradecomponent");
		for(int i = 0; i < Consts.UPGRADE_COUNT; i++)
			IAItems.upgradeIcons[i] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + EnumUpgradeType.getType(i).getName() + EnumUpgradeType.getTier(i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int damage, int renderPass) {
		if(renderPass == 1)
			return IAItems.upgradeIcons[damage];
		return IAItems.upgradeBackground;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		list.add(Funcs.getLoc("upgrade." + EnumUpgradeType.getType(itemstack.getItemDamage()).getName() + ".name", "/ ", "upgrade." + EnumUpgradeType.getTier(itemstack.getItemDamage())));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < Consts.UPGRADE_COUNT; i++)
			list.add(new ItemStack(item, 1, i));
	}
}
