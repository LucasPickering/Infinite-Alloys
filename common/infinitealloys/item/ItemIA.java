package infinitealloys.item;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemIA extends Item {

	private String textureName;

	public ItemIA(int id) {
		super(id);
		setCreativeTab(InfiniteAlloys.tabIA);
		setUnlocalizedName("IAitem" + itemID);
	}

	public ItemIA(int id, String textureName) {
		this(id);
		this.textureName = textureName;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + textureName);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName() + "@" + itemstack.getItemDamage();
	}
}
