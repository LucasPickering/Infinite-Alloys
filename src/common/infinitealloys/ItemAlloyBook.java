package infinitealloys;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemAlloyBook extends ItemIA {

	public ItemAlloyBook(int id, int texture) {
		super(id, texture);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		player.openGui(InfiniteAlloys.instance, References.machineCount, world, 0, 0, 0);
		return itemstack;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}
}
