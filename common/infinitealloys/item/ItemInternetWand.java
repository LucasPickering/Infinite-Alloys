package infinitealloys.item;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInternetWand extends ItemIA {

	public ItemInternetWand(int id) {
		super(id, "internetwand");
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		player.openGui(InfiniteAlloys.instance, Consts.WAND_GUI_NORMAL, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return itemstack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		for(int i = 0; i < Consts.WAND_MAX_COORDS; i++) {
			if(itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("coords" + i)) {
				int[] coords = itemstack.getTagCompound().getIntArray("coords" + i);
				list.add(coords[0] + ", " + coords[1] + ", " + coords[2]);
			}
		}
	}
}
