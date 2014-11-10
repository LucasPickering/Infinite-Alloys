package infinitealloys.item;

import infinitealloys.util.Consts;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMulti extends ItemIA {

	private final IIcon[] icons = new IIcon[Consts.MULTI_ITEM_NAMES.length];

	public ItemMulti() {
		super();
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		for(int i = 0; i < Consts.MULTI_ITEM_NAMES.length; i++)
			icons[i] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + Consts.MULTI_ITEM_NAMES[i]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		return icons[damage];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < Consts.MULTI_ITEM_NAMES.length; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if(itemstack.getItemDamage() < Consts.MULTI_ITEM_NAMES.length)
			return "item.ia" + Consts.MULTI_ITEM_NAMES[itemstack.getItemDamage()];
		return super.getUnlocalizedName(itemstack);
	}
}
