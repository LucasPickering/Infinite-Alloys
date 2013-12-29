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
		player.openGui(InfiniteAlloys.instance, Consts.WAND_GUI, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return itemstack;
	}
}
