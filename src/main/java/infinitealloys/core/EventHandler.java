package infinitealloys.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import infinitealloys.network.MessageValidAlloys;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;

public final class EventHandler {

  private final String fileName = "InfiniteAlloys.dat";
  private String worldDir;

  @SubscribeEvent
  public void onWorldLoad(Load event) {
    if (!event.world.isRemote) {
      if (event.world.provider.getDimensionId() == 0) {
        worldDir = DimensionManager.getWorld(0).getChunkSaveLocation().getPath();
        try {
          NBTTagCompound nbtTagCompound =
              CompressedStreamTools.readCompressed(new FileInputStream(worldDir + "/" + fileName));
          InfiniteAlloys.instance.loadAlloyData(nbtTagCompound); // Load the valid alloys
        } catch (FileNotFoundException e) {
          // There is no saved data, probably because this is a new world. Generate new alloy data.
          InfiniteAlloys.instance.generateAlloyData();
        } catch (Exception e) {
          // There was another error. Generate new alloy data, and print stack trace.
          InfiniteAlloys.instance.generateAlloyData();
          e.printStackTrace();
        }
      }
    } else {
      // Clear the list of blocks to be outlines by the x-ray on unload. This is only run client-side
      InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
    }
  }

  @SubscribeEvent
  public void onWorldSave(Save event) {
    if (!event.world.isRemote && event.world.provider.getDimensionId() == 0) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      InfiniteAlloys.instance.saveAlloyData(nbtTagCompound); // Add the alloy data
      try {
        CompressedStreamTools.writeCompressed(nbtTagCompound, new FileOutputStream(
            worldDir + "/" + fileName)); // Write the NBT data to a file
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @SubscribeEvent
  public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    if (!event.world.isRemote && event.entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) event.entity;
      Funcs.sendPacketToPlayer(new MessageValidAlloys(InfiniteAlloys.instance.getValidAlloys()),
                               player);
      if (!MachineHelper.playersToSync.contains(player.getDisplayNameString())) {
        MachineHelper.playersToSync.add(player.getDisplayNameString());
      }
    }
  }

  @SubscribeEvent
  public void onBlockBreak(BlockEvent.BreakEvent event) {
    if (event.world.provider.getDimensionName().equals("InfiniteAlloys")
        && !event.getPlayer().capabilities.isCreativeMode) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public void onBlockPlace(BlockEvent.PlaceEvent event) {
    if (event.world.provider.getDimensionName().equals("InfiniteAlloys")
        && !event.player.capabilities.isCreativeMode) {
      event.setCanceled(true);
    }
  }
}
