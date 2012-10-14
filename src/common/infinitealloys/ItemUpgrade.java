package infinitealloys;

import java.util.List;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class ItemUpgrade extends ItemIA {

	public int requiredUpgrades;

	public ItemUpgrade(int id, int texture) {
		super(id, texture);
		maxStackSize = 1;
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "IA Upgrade";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconIndex(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		return (int)InfiniteAlloys.logn(2, stack.getItemDamage()) + 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, List list) {
		switch(itemstack.getItemDamage()) {
			case 1:
				list.add("Speed I");
				break;
			case 2:
				list.add("Speed II");
				break;
			case 4:
				list.add("Efficiency I");
				break;
			case 8:
				list.add("Efficiency II");
				break;
			case 16:
				list.add("Pressure I");
				break;
			case 32:
				list.add("Acidity I");
				break;
			case 64:
				list.add("Heat II");
				break;
			case 128:
				list.add("Pressure II");
				break;
			case 256:
				list.add("Acidity II");
				break;
			case 512:
				list.add("Connections I");
				break;
			case 1024:
				list.add("Connections II");
				break;
			case 2048:
				list.add("Range I");
				break;
			case 4096:
				list.add("Range II");
				break;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < IAValues.upgradeCount; i++)
			list.add(new ItemStack(id, 1, (int)Math.pow(2D, i)));
	}
}
