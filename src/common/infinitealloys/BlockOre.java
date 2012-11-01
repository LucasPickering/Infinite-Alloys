package infinitealloys;

import java.util.List;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;

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
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		return metadata;
	}
}
