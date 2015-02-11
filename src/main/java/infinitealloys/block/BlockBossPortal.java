package infinitealloys.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.Random;

import infinitealloys.util.Consts;
import infinitealloys.world.TeleporterBoss;

public class BlockBossPortal extends BlockPortal {


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
      } else if (player.dimension != Consts.dimensionId) {
        player.timeUntilPortal = 10;

        player.mcServer.getConfigurationManager()
            .transferPlayerToDimension(player, Consts.dimensionId, new TeleporterBoss(
                mServer.worldServerForDimension(Consts.dimensionId)));
      } else {
        player.timeUntilPortal = 10;
        player.mcServer.getConfigurationManager()
            .transferPlayerToDimension(player, 0,
                                       new TeleporterBoss(mServer.worldServerForDimension(1)));
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

  public boolean tryToCreatePortal(World world, int x, int y, int z) {
    byte b0 = 0;
    byte b1 = 0;

    if (world.getBlock(x - 1, y, z) == Blocks.sandstone
        || world.getBlock(x + 1, y, z) == Blocks.sandstone) {
      b0 = 1;
    }

    if (world.getBlock(x, y, z - 1) == Blocks.sandstone
        || world.getBlock(x, y, z + 1) == Blocks.sandstone) {
      b1 = 1;
    }

    if (b0 == b1) {
      return false;
    } else {
      if (world.isAirBlock(x - b0, y, z - b1)) {
        x -= b0;
        z -= b1;
      }

      int l;
      int i1;

      for (l = -1; l <= 2; ++l) {
        for (i1 = -1; i1 <= 3; ++i1) {
          boolean flag = l == -1 || l == 2 || i1 == -1 || i1 == 3;

          if (l != -1 && l != 2 || i1 != -1 && i1 != 3) {
            Block block = world.getBlock(x + b0 * l, y + i1, z + b1 * l);
            boolean isAirBlock = world.isAirBlock(x + b0 * l, y + i1, z + b1 * l);

            if (flag) {
              if (block != Blocks.sandstone) {
                return false;
              }
            } else if (!isAirBlock && block != Blocks.fire) {
              return false;
            }
          }
        }
      }

      for (l = 0; l < 2; ++l) {
        for (i1 = 0; i1 < 3; ++i1) {
          world.setBlock(x + b0 * l, y + i1, z + b1 * l, IABlocks.portal, 0, 2);
        }
      }

      return true;
    }
  }
}
