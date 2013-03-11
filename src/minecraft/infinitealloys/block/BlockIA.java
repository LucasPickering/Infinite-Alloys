package infinitealloys.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.util.References;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockIA extends Block {

	public BlockIA(int id, Material material) {
		super(id, material);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTextureFile() {
		return References.TEXTURE_PATH + "sprites.png";
	}

	@Override
	public int damageDropped(int damage) {
		return damage;
	}
}
