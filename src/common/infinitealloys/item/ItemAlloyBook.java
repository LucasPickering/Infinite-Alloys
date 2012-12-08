package infinitealloys.item;

import java.util.List;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class ItemAlloyBook extends ItemIA {

	public ItemAlloyBook(int id, int texture) {
		super(id, texture);
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		NBTTagCompound tagCompound = itemstack.getTagCompound();
		if(tagCompound != null)// && tagCompound.hasKey("title"))
			for(int alloy : tagCompound.getIntArray("alloys"))
				list.add(Integer.toString(alloy));
	}
}
