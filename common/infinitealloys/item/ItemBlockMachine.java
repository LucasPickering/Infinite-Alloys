package infinitealloys.item;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMachine extends ItemBlock {

	public ItemBlockMachine(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if(itemstack.getItemDamage() < Consts.MACHINE_COUNT)
			return "tile.IA" + EnumMachine.values()[itemstack.getItemDamage()].getName();
		return super.getUnlocalizedName(itemstack);
	}
}
