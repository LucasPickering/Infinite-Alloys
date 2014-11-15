package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.tile.TEIASummoner;
import infinitealloys.util.Consts;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSummoner extends BlockContainer {

	public BlockSummoner() {
		super(Material.rock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		IABlocks.summonerTopIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "summoner_top");
		IABlocks.summonerSideIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "summoner_side");
		IABlocks.summonerBottomIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "summoner_bottom");
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		switch(side) {
			case Consts.TOP:
				return IABlocks.summonerTopIcon;
			case Consts.BOTTOM:
				return IABlocks.summonerBottomIcon;
			default:
				return IABlocks.summonerSideIcon;
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float f, float f1, float f2) {
		player.openGui(InfiniteAlloys.instance, Consts.SUMMONER_GUI_ID, world, x, y, z);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TEIASummoner();
	}
}
