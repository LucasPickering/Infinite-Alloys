package infinitealloys.block;

import infinitealloys.core.References;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

public class BlockIA extends Block {

	public BlockIA(int id, Material material) {
		super(id, material);
	}

	@Override
	public String getTextureFile() {
		return References.TEXTURE_PATH + "sprites.png";
	}

	@Override
	public int damageDropped(int damage) {
		return damage;
	}
}
