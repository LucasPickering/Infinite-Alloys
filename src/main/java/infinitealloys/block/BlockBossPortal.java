package infinitealloys.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import infinitealloys.util.Consts;
import infinitealloys.world.TeleporterBoss;

public class BlockBossPortal extends BlockPortal {

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + "portal");
  }

  @Override
  public void updateTick(World world, int x, int y, int z, Random random) {
  }

  @Override
  public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    if ((entity.ridingEntity == null) && (entity.riddenByEntity == null)
        && ((entity instanceof EntityPlayerMP))) {
      EntityPlayerMP player = (EntityPlayerMP) entity;
      MinecraftServer mServer = MinecraftServer.getServer();

      if (player.timeUntilPortal > 0) {
        player.timeUntilPortal = 10;
      } else if (world.provider.getDimensionName().equals("InfiniteAlloys")) {
        ChunkCoordinates spawnPoint = mServer.worldServerForDimension(0).getSpawnPoint();
        player.setPosition(spawnPoint.posX, spawnPoint.posY, spawnPoint.posZ);
        mServer.getConfigurationManager()
            .transferPlayerToDimension(player, 0,
                                       new TeleporterBoss(mServer.worldServerForDimension(0)));
      }
    }
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, Block neighorBlock) {
    byte b0 = 0;
    byte b1 = 1;

    if (world.getBlock(x - 1, y, z) == this || world.getBlock(x + 1, y, z) == this) {
      b0 = 1;
      b1 = 0;
    }

    int i1;

    for (i1 = y; world.getBlock(x, i1 - 1, z) == this; i1--) {
    }

    if (world.getBlock(x, i1 - 1, z) != Blocks.sandstone) {
      world.setBlockToAir(x, y, z);
    } else {
      int j1;

      for (j1 = 1; j1 < 4 && world.getBlock(x, i1 + j1, z) == this; ++j1) {
      }

      if (j1 == 3 && world.getBlock(x, i1 + j1, z) == Blocks.sandstone) {
        boolean flag = world.getBlock(x - 1, y, z) == this || world.getBlock(x + 1, y, z) == this;
        boolean flag1 = world.getBlock(x, y, z - 1) == this || world.getBlock(x, y, z + 1) == this;

        if (flag && flag1) {
          world.setBlockToAir(x, y, z);
        } else {
          if ((world.getBlock(x + b0, y, z + b1) != Blocks.sandstone
               || world.getBlock(x - b0, y, z - b1) != this) && (
                  world.getBlock(x - b0, y, z - b1) != Blocks.sandstone
                  || world.getBlock(x + b0, y, z + b1) != this)) {
            world.setBlockToAir(x, y, z);
          }
        }
      } else {
        world.setBlockToAir(x, y, z);
      }
    }
  }
}