package infinitealloys;

import infinitealloys.client.ClientProxy;
import infinitealloys.handlers.PacketHandler;
import java.util.List;
import java.util.Random;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockMachine extends BlockContainer {

	private Random random = new Random();

	public BlockMachine(int id, int texture) {
		super(id, texture, Material.iron);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public String getTextureFile() {
		return IAValues.BLOCKS_PNG;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int i, float f, float f1, float f2) {
		if(entityplayer.isSneaking()) return false;
		TileEntityMachine tem = (TileEntityMachine)world.getBlockTileEntity(x, y, z);
		PacketDispatcher.sendPacketToAllPlayers(PacketHandler.getPacketToClient(tem));
		if(tem instanceof TileEntityComputer)
			entityplayer.openGui(InfiniteAlloys.instance, 0, world, x, y, z);
		else if(tem instanceof TileEntityMetalForge)
			entityplayer.openGui(InfiniteAlloys.instance, 1, world, x, y, z);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		switch(metadata) {
			case 0:
				return new TileEntityComputer(0);
			case 1:
				return new TileEntityMetalForge(0);
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
		for(int i = 0; i < IAValues.machineCount; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int metadata) {
		TileEntityMachine tem = (TileEntityMachine)world.getBlockTileEntity(x, y, z);
		if(tem != null) {
			tem.dropUpgrades(random);
			for(int i = 0; i < tem.getSizeInventory(); i++) {
				ItemStack itemstack = tem.getStackInSlot(i);
				if(itemstack != null) {
					float f = random.nextFloat() * 0.8F + 0.1F;
					float f1 = random.nextFloat() * 0.8F + 0.1F;
					float f2 = random.nextFloat() * 0.8F + 0.1F;
					while(itemstack.stackSize > 0) {
						int j = random.nextInt(21) + 10;
						if(j > itemstack.stackSize)
							j = itemstack.stackSize;
						itemstack.stackSize -= j;
						EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.itemID, j, itemstack.getItemDamage()));
						if(itemstack.hasTagCompound())
							entityitem.item.setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
						entityitem.motionX = (float)random.nextGaussian() * 0.05F;
						entityitem.motionY = (float)random.nextGaussian() * 0.05F + 0.2F;
						entityitem.motionZ = (float)random.nextGaussian() * 0.05F;
						world.spawnEntityInWorld(entityitem);
					}
				}
			}
		}
		super.breakBlock(world, x, y, z, id, metadata);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return ClientProxy.renderId;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityliving) {
		TileEntityMachine tem = (TileEntityMachine)world.getBlockTileEntity(x, y, z);
		if(tem != null) {
			tem.orientation = (byte)(MathHelper.floor_double(entityliving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3);
			world.markBlockNeedsUpdate(x, y, z);
		}
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		minX = 0;
		minY = 0;
		minZ = 0;
		maxX = 1;
		maxY = 1;
		maxZ = 1;
		switch(world.getBlockMetadata(x, y, z)) {
			case 0:
				minX = 0.0625D;
				minZ = 0.0625D;
				maxX = 0.9375D;
				maxY = 0.75D;
				maxZ = 0.9374D;
				break;
		}
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return getSelectedBoundingBoxFromPool(world, x, y, z);
	}
}
