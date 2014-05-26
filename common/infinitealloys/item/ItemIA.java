package infinitealloys.item;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemIA extends Item {

	private String textureName;

	public ItemIA() {
		super();
		setCreativeTab(InfiniteAlloys.tabIA);
	}

	public ItemIA(String textureName) {
		this();
		this.textureName = textureName;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + textureName);
	}
}
