package infinitealloys.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.util.Consts;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemIA extends Item {

	public ItemIA(int id) {
		super(id);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return "IAitem" + itemstack.itemID + "@" + itemstack.getItemDamage();
	}
}
