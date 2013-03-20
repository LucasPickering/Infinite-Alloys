package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
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
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void func_94332_a(IconRegister iconRegister) {
		Blocks.oreForegroundIcon = iconRegister.func_94245_a(Consts.TEXTURE_PREFIX + "ore_foreground");
		Blocks.oreBackgroundIcon = iconRegister.func_94245_a(Consts.TEXTURE_PREFIX + "ore_background");
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int side, int metadata) {
		// Not actually used as metadata, see GfxHandler for ISBRH code
		if(metadata == 0)
			return Blocks.oreBackgroundIcon;
		return Blocks.oreForegroundIcon;
	}

	@Override
	public int getRenderType() {
		return InfiniteAlloys.instance.proxy.gfxHandler.renderID;
	}
}
