package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.network.PacketRequestSync;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
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
		for(int i = 0; i < Consts.MACHINE_COUNT; i++) {
			Blocks.machineIcons[i][0] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + MachineHelper.MACHINE_NAMES[i] + "_top");
			Blocks.machineIcons[i][1] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + MachineHelper.MACHINE_NAMES[i] + "_bottom");
			Blocks.machineIcons[i][2] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + MachineHelper.MACHINE_NAMES[i] + "_front");
			Blocks.machineIcons[i][3] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + MachineHelper.MACHINE_NAMES[i] + "_side");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
		side = side <= Consts.TOP ? side : side == ((TileEntityMachine)blockAccess.getBlockTileEntity(x, y, z)).front ? Consts.SOUTH : Consts.NORTH;
		return getIcon(side, blockAccess.getBlockMetadata(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata) {
		switch(side) {
			case Consts.TOP:
				return Blocks.machineIcons[metadata][0];

			case Consts.BOTTOM:
				return Blocks.machineIcons[metadata][1];

			case Consts.SOUTH:
				return Blocks.machineIcons[metadata][2];

			default:
				return Blocks.machineIcons[metadata][3];
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float f, float f1, float f2) {
		ItemStack heldItem = player.inventory.getCurrentItem();
		TileEntityMachine tem = (TileEntityMachine)world.getBlockTileEntity(x, y, z);
		// Is the player holding an internet wand?
		if(heldItem != null && heldItem.getItem() instanceof ItemInternetWand && (MachineHelper.isClient(tem) || tem instanceof IHost)) {

			// Put the coords of this block in a temp tag in the wand so the wand's GUI can access it
			if(!heldItem.hasTagCompound())
				heldItem.setTagCompound(new NBTTagCompound());
			heldItem.getTagCompound().setIntArray("CoordsCurrent", new int[] { world.provider.dimensionId, x, y, z });

			// Open the GUI for the wand to let the player decide what they want to do with this block
			player.openGui(InfiniteAlloys.instance, Consts.WAND_GUI, world, (int)player.posX, (int)player.posY, (int)player.posZ);
			return true;
		}
		if(tem instanceof IHost) {
			if(world.isRemote)
				PacketDispatcher.sendPacketToServer(PacketRequestSync.getPacket(x, y, z));
			else
				((IHost)tem).syncAllClients((Player)player);
		}
		openGui(world, player, tem, false);
		return true;
	}

	public void openGui(World world, EntityPlayer player, TileEntityMachine tem, boolean fromComputer) {
		if(!fromComputer && Funcs.isClient())
			MachineHelper.controllers.remove(player.username);
		if(tem instanceof TEMComputer)
			MachineHelper.controllers.put(player.username, new Point(tem.xCoord, tem.yCoord, tem.zCoord));
		player.openGui(InfiniteAlloys.instance, tem.getID(), world, tem.xCoord, tem.yCoord, tem.zCoord);
		if(Funcs.isServer()) {
			tem.playersUsing.add(player.username);
			world.markBlockForUpdate(tem.xCoord, tem.yCoord, tem.zCoord);
		}
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		try {
			return (TileEntity)MachineHelper.MACHINE_CLASSES[metadata].newInstance();
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
		for(int i = 0; i < Consts.MACHINE_COUNT; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemstack) {
		TileEntityMachine tem = (TileEntityMachine)world.getBlockTileEntity(x, y, z);
		if(tem != null) {
			tem.front = Funcs.yawToNumSide(MathHelper.floor_float(entityLiving.rotationYaw / 90F - 1.5F) & 3);
			world.markBlockForUpdate(x, y, z);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int blockID, int metadata) {
		TileEntityMachine tem = (TileEntityMachine)world.getBlockTileEntity(x, y, z);
		if(tem != null) {
			tem.dropItems();
			tem.dropUpgrades();
			if(tem instanceof IHost)
				((IHost)tem).deleteNetwork();
		}
		super.breakBlock(world, x, y, z, blockID, metadata);
	}
}
