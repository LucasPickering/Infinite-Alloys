package infinitealloys.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockIA extends Block {

	public BlockIA(int id, Material material) {
		super(id, material);
	}

	@Override
	public int damageDropped(int damage) {
		return damage;
	}
}
