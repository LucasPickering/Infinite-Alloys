package infinitealloys.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.MachineHelper;

public class BlockMachine extends BlockIA implements ITileEntityProvider {

  public BlockMachine() {
    super(Material.iron);
  }

  @Override
  public int getRenderType() {
    return InfiniteAlloys.proxy.gfxHandler.renderID;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing,
                                  float f, float f1, float f2) {
    ItemStack heldItem = player.inventory.getCurrentItem();
    TileEntityMachine tem = (TileEntityMachine) world.getTileEntity(x, y, z);

    // Sync the network data for each host TE in this world if it has not already been done for this player
    if (!world.isRemote && MachineHelper.playersToSync.contains(player.getDisplayName())) {
      for (Object te : world.loadedTileEntityList) {
        if (te instanceof IHost) {
          ((IHost) te).syncAllClients(player);
        }
      }
      MachineHelper.playersToSync.remove(player.getDisplayName());
    }

    // Is the player holding a network wand?
    if (heldItem != null && heldItem.getItem() instanceof ItemInternetWand && (
        MachineHelper.isClient(tem) || tem instanceof IHost)) {

      // Put the coords of this block in a temp tag in the wand so the wand's GUI can access it
      if (!heldItem.hasTagCompound()) {
        heldItem.setTagCompound(new NBTTagCompound());
      }
      heldItem.getTagCompound()
          .setIntArray("CoordsCurrent", new int[]{world.provider.dimensionId, x, y, z});

      // Open the GUI for the wand to let the player decide what they want to do with this block
      player.openGui(InfiniteAlloys.instance, Consts.WAND_GUI_ID, world, (int) player.posX,
                     (int) player.posY, (int) player.posZ);
      return true;
    }

    openGui(world, player, tem);
    return true;
  }

  public void openGui(World world, EntityPlayer player, TileEntityMachine tem) {
    if (!world.isRemote) {
      world.markBlockForUpdate(tem.xCoord, tem.yCoord, tem.zCoord);
    }
    player.openGui(InfiniteAlloys.instance, tem.getEnumMachine().ordinal(), world, tem.xCoord,
                   tem.yCoord, tem.zCoord);
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    try {
      return (TileEntity) EnumMachine.values()[metadata].temClass.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean onBlockEventReceived(World world, int x, int y, int z, int i, int j) {
    super.onBlockEventReceived(world, x, y, z, i, j);
    TileEntity tileentity = world.getTileEntity(x, y, z);
    return tileentity != null && tileentity.receiveClientEvent(i, j);
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
    TileEntityMachine tem = (TileEntityMachine) world.getTileEntity(x, y, z);
    if (tem != null) {
      tem.onBlockDestroyed();
    }
    super.breakBlock(world, x, y, z, block, metadata);
    world.removeTileEntity(x, y, z);
  }

  @Override
  public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY,
                               int tileZ) {
    ((TileEntityMachine) world.getTileEntity(x, y, z)).onNeighborChange(tileX, tileY, tileZ);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving,
                              ItemStack itemstack) {
    TileEntityMachine tem = (TileEntityMachine) world.getTileEntity(x, y, z);
    if (tem != null) {
      tem.orientation = ForgeDirection.getOrientation(MathHelper.floor_float(entityLiving.rotationYaw / 90F - 1.5F) & 3);
      if (itemstack.hasTagCompound()) {
        tem.loadNBTData(itemstack.getTagCompound());
      }
    }
  }

  @Override
  public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
                                       int metadata, int fortune) {
    return new ArrayList<ItemStack>();
  }

  @Override
  public void getSubBlocks(Item item, CreativeTabs creativetabs, List list) {
    for (int i = 0; i < Consts.MACHINE_COUNT; i++) {
      list.add(new ItemStack(item, 1, i));
    }
  }
}
