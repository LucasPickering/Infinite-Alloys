package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.handlers.PacketHandler;
import infinitealloys.item.Items;
import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TEUComputer;
import infinitealloys.tile.TEUEnergyStorage;
import infinitealloys.tile.TileEntityUpgradable;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import java.util.List;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMachine extends BlockContainer {

	public BlockMachine(int id) {
		super(id, Material.iron);
		setCreativeTab(InfiniteAlloys.tabIA);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		for(int i = 0; i < Consts.TEU_COUNT; i++) {
			Blocks.machineIcons[i][0] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + TEHelper.TEU_NAMES[i] + "_top");
			Blocks.machineIcons[i][1] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + TEHelper.TEU_NAMES[i] + "_front");
			Blocks.machineIcons[i][2] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + TEHelper.TEU_NAMES[i] + "_side");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
		int type = side <= Consts.BOTTOM ? 0 : side == ((TileEntityUpgradable)blockAccess.getBlockTileEntity(x, y, z)).front ? 1 : 2;
		return Blocks.machineIcons[blockAccess.getBlockMetadata(x, y, z)][type];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata) {
		return Blocks.machineIcons[metadata][side <= Consts.BOTTOM ? 0 : side == Consts.SOUTH ? 1 : 2];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float f, float f1, float f2) {
		ItemStack heldItem = player.inventory.getCurrentItem();
		// Is the player holding an internet wand?
		if(heldItem != null && heldItem.itemID == Items.internetWand.itemID) {
			// Is this block a computer?
			if(world.getBlockTileEntity(x, y, z) instanceof TEUComputer) {
				// Does the internet wand have stored data?
				if(heldItem.hasTagCompound()) {
					// If so, add all coordinates saved by the wand into the computer
					NBTTagCompound tagCompound = heldItem.getTagCompound();
					for(int i = 0; i < Consts.WAND_MAX_COORDS; i++) {
						if(!tagCompound.hasKey("coords" + i))
							continue;
						int[] coords = tagCompound.getIntArray("coords" + i);
						if(((TEUComputer)world.getBlockTileEntity(x, y, z)).addTEU(player, coords[0], coords[1], coords[2])) {
							if(world.isRemote)
								player.addChatMessage("Adding machine at " + coords[0] + ", " + coords[1] + ", " + coords[2]);
							tagCompound.removeTag("coords" + i);
						}
					}
				}
			}

			// Is this block an RK storage unit?
			else if(world.getBlockTileEntity(x, y, z) instanceof TEUEnergyStorage) {
				// Does the internet wand have stored data?
				if(heldItem.hasTagCompound()) {
					// If so, add all coordinates saved by the wand into the unit
					NBTTagCompound tagCompound = heldItem.getTagCompound();
					for(int i = 0; i < Consts.WAND_MAX_COORDS; i++) {
						if(!tagCompound.hasKey("coords" + i))
							continue;
						int[] coords = tagCompound.getIntArray("coords" + i);
						if(((TEUEnergyStorage)world.getBlockTileEntity(x, y, z)).addMachine(player, coords[0], coords[1], coords[2])) {
							if(world.isRemote)
								player.addChatMessage("Adding machine at " + coords[0] + ", " + coords[1] + ", " + coords[2]);
							tagCompound.removeTag("coords" + i);
						}
					}
				}
			}

			// If it's not a computer, but it is capable of networking, add its data to the wand
			else if(((TileEntityUpgradable)world.getBlockTileEntity(x, y, z)).hasUpgrade(TEHelper.WIRELESS)) {
				NBTTagCompound tagCompound = heldItem.hasTagCompound() ? heldItem.getTagCompound() : new NBTTagCompound();
				int size = 0;
				for(int i = 0; i < Consts.WAND_MAX_COORDS; i++) {
					if(!tagCompound.hasKey("coords" + i)) {
						size = i;
						break;
					}
					int[] nbtCoords = tagCompound.getIntArray("coords" + i);
					if(nbtCoords[0] == x && nbtCoords[1] == y && nbtCoords[2] == z)
						return true;
				}
				if(size < Consts.WAND_MAX_COORDS) {
					tagCompound.setIntArray("coords" + size, new int[] { x, y, z });
					heldItem.setTagCompound(tagCompound);
					if(world.isRemote)
						player.addChatMessage("Tracking machine at " + x + ", " + y + ", " + z);
				}
			}
			return true;
		}
		openGui(world, player, (TileEntityUpgradable)world.getBlockTileEntity(x, y, z), false);
		return true;
	}

	public void openGui(World world, EntityPlayer player, TileEntityUpgradable teu, boolean fromComputer) {
		if(!fromComputer && Funcs.isClient())
			TEHelper.controllers.remove(player.username);
		if(teu instanceof TEUComputer)
			TEHelper.controllers.put(player.username, new Point(teu.xCoord, teu.yCoord, teu.zCoord));
		player.openGui(InfiniteAlloys.instance, teu.getID(), world, teu.xCoord, teu.yCoord, teu.zCoord);
		if(Funcs.isServer()) {
			teu.playersUsing.add(player.username);
			PacketDispatcher.sendPacketToPlayer(PacketHandler.getTEPacketToClient(teu), (Player)player);
		}
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		try {
			return (TileEntity)TEHelper.TEU_CLASSES[metadata].newInstance();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}

	public static void updateBlockState(World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te != null) {
			te.validate();
			world.setBlockTileEntity(x, y, z, te);
		}
	}

	@Override
	public void getSubBlocks(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < Consts.TEU_COUNT; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemstack) {
		TileEntityUpgradable teu = (TileEntityUpgradable)world.getBlockTileEntity(x, y, z);
		if(teu != null) {
			teu.front = Funcs.yawToNumSide(MathHelper.floor_float(entityLiving.rotationYaw / 90F - 1.5F) & 3);
			world.markBlockForUpdate(x, y, z);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntityUpgradable teu = (TileEntityUpgradable)world.getBlockTileEntity(x, y, z);
		if(teu != null) {
			teu.dropItems();
			teu.dropUpgrades();
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}
}
