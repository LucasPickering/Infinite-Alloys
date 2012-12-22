package infinitealloys.item;

import infinitealloys.References;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGPS extends ItemIA {

	public ItemGPS(int id, int texture) {
		super(id, texture);
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		for(int i = 0; i < References.gpsMaxCoords; i++) {
			if(itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("coords" + i)) {
				int[] coords = itemstack.getTagCompound().getIntArray("coords" + i);
				list.add(coords[0] + ", " + coords[1] + ", " + coords[2]);
			}
		}
	}
}
