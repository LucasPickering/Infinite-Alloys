package infinitealloys;

import java.util.List;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;
import net.minecraft.src.ItemStack;

public class BlockIA extends Block {

	public BlockIA(int id, int texture, Material material) {
		super(id, texture, material);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public String getTextureFile() {
		return IAValues.BLOCKS_PNG;
	}

	@Override
	public void getSubBlocks(int id, CreativeTabs creativetabs, List list) {
		list.add(new ItemStack(Block.blocksList[id]));
	}
}
