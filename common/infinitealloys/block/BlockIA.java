package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockIA extends Block {

	public BlockIA(int id, Material material) {
		super(id, material);
		setCreativeTab(InfiniteAlloys.tabIA);
	}

	@Override
	public int damageDropped(int damage) {
		return damage;
	}
}
