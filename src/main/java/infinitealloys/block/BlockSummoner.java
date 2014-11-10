package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.MachineHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSummoner extends Block {

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
		player.openGui(InfiniteAlloys.instance, Consts.SUMMONER_GUI_ID, world, x, y, y);
		return true;
	}
}
