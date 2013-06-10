package infinitealloys.item;

import infinitealloys.util.Consts;
import java.util.List;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGPS extends ItemIA {

	public ItemGPS(int id) {
		super(id);
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "gps");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		for(int i = 0; i < Consts.GPS_MAX_COORDS; i++) {
			if(itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("coords" + i)) {
				int[] coords = itemstack.getTagCompound().getIntArray("coords" + i);
				list.add(coords[0] + ", " + coords[1] + ", " + coords[2]);
			}
		}
	}
}
