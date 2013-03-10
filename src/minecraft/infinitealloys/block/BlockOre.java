package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.References;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockOre extends BlockIA {

	public BlockOre(int id) {
		super(id, Material.rock);
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
	@SideOnly(Side.CLIENT)
	public Icon getBlockTextureFromSideAndMetadata(int side, int metadata) {
		if(metadata < References.metalCount)
			return field_94336_cN;
		return field_94336_cN + 1;
	}
}
