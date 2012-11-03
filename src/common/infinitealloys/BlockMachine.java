package infinitealloys;

import infinitealloys.client.ClientProxy;
import infinitealloys.handlers.PacketHandler;
import java.util.List;
import java.util.Random;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
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
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float f, float f1, float f2) {
		ItemStack currentItem = player.inventory.getCurrentItem();
		if(currentItem != null && currentItem.itemID == InfiniteAlloys.gps.shiftedIndex && ((TileEntityMachine)world.getBlockTileEntity(x, y, z)).canNetwork) {
			if(player.isSneaking() && world.getBlockTileEntity(x, y, z) instanceof TileEntityComputer) {
				if(currentItem.hasTagCompound()) {
					NBTTagCompound tagCompound = currentItem.getTagCompound();
					for(int i = 0; i < References.gpsMaxCoords; i++) {
						if(!tagCompound.hasKey("coords" + i))
							continue;
						int[] coords = tagCompound.getIntArray("coords" + i);
						if(((TileEntityComputer)world.getBlockTileEntity(x, y, z)).addMachine(player, coords[0], coords[1], coords[2])) {
							if(world.isRemote)
								player.addChatMessage("Adding machine at " + coords[0] + ", " + coords[1] + ", " + coords[2]);
							tagCompound.func_82580_o("coords" + i);
						}
					}
				}
			}
			else {
				NBTTagCompound tagCompound = currentItem.hasTagCompound() ? currentItem.getTagCompound() : new NBTTagCompound();
				int size = 0;
				for(int i = 0; i < References.gpsMaxCoords; i++)
					if(!tagCompound.hasKey("coords" + i))
						size = i;
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
		TileEntityMachine tem = (TileEntityMachine)world.getBlockTileEntity(x, y, z);
		if(tem instanceof TileEntityComputer)
			player.openGui(InfiniteAlloys.instance, 0, world, x, y, z);
		else if(tem instanceof TileEntityMetalForge) {
			if(!world.isRemote)
				((TileEntityMetalForge)tem).numUsingPlayers++;
			player.openGui(InfiniteAlloys.instance, 1, world, x, y, z);
		}
		else if(tem instanceof TileEntityAnalyzer)
			player.openGui(InfiniteAlloys.instance, 2, world, x, y, z);
		PacketDispatcher.sendPacketToAllPlayers(PacketHandler.getTEPacketToClient(tem));
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		switch(metadata) {
			case 0:
				return new TileEntityComputer();
			case 1:
				return new TileEntityMetalForge();
			case 2:
				return new TileEntityAnalyzer();
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
			tem.orientation = (byte)(4 - (MathHelper.floor_double(entityliving.rotationYaw / 90F + 0.5D)));
			world.markBlockNeedsUpdate(x, y, z);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		minX = 0D;
		minY = 0D;
		minZ = 0D;
		maxX = 1D;
		maxY = 1D;
		maxZ = 1D;
		switch(world.getBlockMetadata(x, y, z)) {
			case 0:
				minX = 0.0625D;
				minZ = 0.0625D;
				maxX = 0.9375D;
				maxY = 0.75D;
				maxZ = 0.9374D;
				break;
			case 2:
				maxY = 0.5D;
				break;
		}
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public int damageDropped(int damage) {
		return damage;
	}
}
