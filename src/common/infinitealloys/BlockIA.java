package infinitealloys;

import net.minecraft.src.Block;
import net.minecraft.src.Material;

public class BlockIA extends Block {

	public BlockIA(int id, int texture, Material material) {
		super(id, texture, material);
	}

	@Override
	public String getTextureFile() {
		return References.TEXTURE_PATH + "blocks.png";
	}

	@Override
	public int damageDropped(int damage) {
		return damage;
	}
}
