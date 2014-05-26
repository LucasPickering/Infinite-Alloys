package infinitealloys.item;

import infinitealloys.block.IABlocks;
import infinitealloys.util.Consts;
import infinitealloys.util.MachineHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMachine extends ItemBlock {

	public ItemBlockMachine() {
		super(IABlocks.machine);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if(itemstack.getItemDamage() < Consts.MACHINE_COUNT)
			return "tile.IA" + MachineHelper.MACHINE_NAMES[itemstack.getItemDamage()];
		return super.getUnlocalizedName(itemstack);
	}
}
