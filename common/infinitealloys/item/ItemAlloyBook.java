package infinitealloys.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAlloyBook extends ItemIA {

	public ItemAlloyBook(int id) {
		super(id, "alloybook");
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		NBTTagCompound tagCompound = itemstack.getTagCompound();
		if(tagCompound != null) // && tagCompound.hasKey("title"))
			for(int alloy : tagCompound.getIntArray("alloys"))
				list.add(Integer.toString(alloy));
	}
}
