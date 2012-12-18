package infinitealloys.block;

import infinitealloys.References;
import java.util.List;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;

public class BlockOre extends BlockIA {

	public BlockOre(int id, int texture) {
		super(id, texture, Material.rock);
	}

	@Override
	public void getSubBlocks(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < References.metalCount; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@SideOnly(Side.CLIENT)
	public int getRenderColor(int metadata) {
		if(metadata < References.metalCount)
			return References.metalColors[metadata];
		return 0xffffff;
	}

	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		if(metadata < References.metalCount)
			return References.metalColors[metadata];
		return 0xffffff;
	}
}
