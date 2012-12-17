package infinitealloys.block;

import infinitealloys.References;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
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

	public int getRenderColor(int metadata) {
		if(metadata < References.metalCount)
			return References.metalColors[metadata];
		return 0xffffff;
	}
}
