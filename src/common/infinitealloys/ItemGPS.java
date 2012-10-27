package infinitealloys;

import java.util.List;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

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
		if(itemstack.hasTagCompound()) {
			int[] coords = itemstack.getTagCompound().getIntArray("coords");
			list.add(coords[0] + ", " + coords[1] + ", " + coords[2]);
		}
	}
}
