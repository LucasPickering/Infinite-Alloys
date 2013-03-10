package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.Point;
import infinitealloys.core.References;
import infinitealloys.handlers.PacketHandler;
import infinitealloys.item.Items;
import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TileEntityAnalyzer;
import infinitealloys.tile.TileEntityComputer;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.tile.TileEntityMetalForge;
import infinitealloys.tile.TileEntityPrinter;
import infinitealloys.tile.TileEntityXray;
import java.util.List;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.VectorHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class BlockMachine extends BlockContainer {

	public BlockMachine(int id) {
		super(id, Material.iron);
	}

	@Override
	public String getTextureFile() {
		return References.TEXTURE_PATH + "sprites.png";
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float f, float f1, float f2) {
		ItemStack currentItem = player.inventory.getCurrentItem();
		if(currentItem != null && currentItem.itemID == Items.gps.itemID && ((TileEntityMachine)world.getBlockTileEntity(x, y, z)).canNetwork) {
			if(player.isSneaking() && world.getBlockTileEntity(x, y, z) instanceof TileEntityComputer && currentItem.hasTagCompound()) {
				if(currentItem.hasTagCompound()) {
					NBTTagCompound tagCompound = currentItem.getTagCompound();
					for(int i = 0; i < References.gpsMaxCoords; i++) {
						if(!tagCompound.hasKey("coords" + i))
							continue;
						int[] coords = tagCompound.getIntArray("coords" + i);
						if(((TileEntityComputer)world.getBlockTileEntity(x, y, z)).addMachine(player, coords[0], coords[1], coords[2])) {
							if(world.isRemote)
								player.addChatMessage("Adding machine at " + coords[0] + ", " + coords[1] + ", " + coords[2]);
							tagCompound.removeTag("coords" + i);
						}
					}
				}
			}
			else {
				NBTTagCompound tagCompound = currentItem.hasTagCompound() ? currentItem.getTagCompound() : new NBTTagCompound();
				int size = 0;
				for(int i = 0; i < References.gpsMaxCoords; i++) {
					if(!tagCompound.hasKey("coords" + i)) {
						size = i;
						break;
					}
					int[] nbtCoords = tagCompound.getIntArray("coords" + i);
					if(nbtCoords[0] == x && nbtCoords[1] == y && nbtCoords[2] == z)
						return true;
				}
				if(size < References.gpsMaxCoords) {
					tagCompound.setIntArray("coords" + size, new int[] { x, y, z });
					currentItem.setTagCompound(tagCompound);
					if(world.isRemote)
						player.addChatMessage("Tracking machine at " + x + ", " + y + ", " + z);
				}
			}
			return true;
		}
		if(player.isSneaking())
			return false;
		openGui(world, player, (TileEntityMachine)world.getBlockTileEntity(x, y, z), false);
		return true;
	}

	public void openGui(World world, EntityPlayer player, TileEntityMachine tem, boolean fromComputer) {
		if(!fromComputer && FMLCommonHandler.instance().getEffectiveSide().isClient())
			TEHelper.controllers.remove(player.username);
		if(tem instanceof TileEntityComputer) {
			TEHelper.controllers.put(player.username, new Point(tem.xCoord, tem.yCoord, tem.zCoord));
			player.openGui(InfiniteAlloys.instance, 0, world, tem.xCoord, tem.yCoord, tem.zCoord);
		}
		else if(tem instanceof TileEntityMetalForge)
			player.openGui(InfiniteAlloys.instance, 1, world, tem.xCoord, tem.yCoord, tem.zCoord);
		else if(tem instanceof TileEntityAnalyzer)
			player.openGui(InfiniteAlloys.instance, 2, world, tem.xCoord, tem.yCoord, tem.zCoord);
		else if(tem instanceof TileEntityPrinter)
			player.openGui(InfiniteAlloys.instance, 3, world, tem.xCoord, tem.yCoord, tem.zCoord);
		else if(tem instanceof TileEntityXray)
			player.openGui(InfiniteAlloys.instance, 4, world, tem.xCoord, tem.yCoord, tem.zCoord);
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			tem.playersUsing.add(player.username);
			PacketDispatcher.sendPacketToPlayer(PacketHandler.getTEPacketToClient(tem), (Player)player);
		}
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		switch(metadata) {
			case 0:
				return new TileEntityComputer();
			case 1:
				return new TileEntityMetalForge();
			case 2:
				return new TileEntityAnalyzer();
			case 3:
				return new TileEntityPrinter();
			case 4:
				return new TileEntityXray();
		}
		return null;
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
		for(int i = 0; i < References.machineCount; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityliving, ItemStack itemstack) {
		TileEntityMachine tem = (TileEntityMachine)world.getBlockTileEntity(x, y, z);
		if(tem != null) {
			switch(MathHelper.floor_double(entityliving.rotationYaw * 4.0F / 360.0F + 0.5D) % 4) {
				case 0:
					tem.front = ForgeDirection.getOrientation(2);
				case 1:
					tem.front = ForgeDirection.getOrientation(5);
				case 2:
					tem.front = ForgeDirection.getOrientation(3);
				default:
					tem.front = ForgeDirection.getOrientation(4);
			}
			world.markBlockForUpdate(x, y, z);
		}
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		return blockIndexInTexture + metadata * 3 + (side < 2 ? 0 : side == 3 ? 1 : 2);
	}

	@Override
	public int getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
		side = VectorHelper.getOrientationFromSide(((TileEntityMachine)blockAccess.getBlockTileEntity(x, y, z)).front, ForgeDirection.getOrientation(side)).ordinal();
		return blockIndexInTexture + blockAccess.getBlockMetadata(x, y, z) * 3 + (side < 2 ? 0 : side == 3 ? 1 : 2);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntityMachine tem = (TileEntityMachine)world.getBlockTileEntity(x, y, z);
		if(tem != null) {
			tem.dropItems();
			tem.dropUpgrades();
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}
}
