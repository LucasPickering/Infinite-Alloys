package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.References;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class BlockOre extends BlockIA {

	public BlockOre(int id, int texture) {
		super(id, texture, Material.rock);
	}

	@Override
	public void getSubBlocks(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < References.metalCount; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	public int getRenderType() {
		return InfiniteAlloys.instance.proxy.gfxHandler.renderID;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		if(metadata < References.metalCount)
			return blockIndexInTexture;
		return blockIndexInTexture + 1;
	}
}
