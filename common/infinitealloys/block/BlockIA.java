package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockIA extends Block {

	public BlockIA(Material material) {
		super(material);
		setCreativeTab(InfiniteAlloys.tabIA);
	}

	@Override
	public int damageDropped(int damage) {
		return damage;
	}
}
