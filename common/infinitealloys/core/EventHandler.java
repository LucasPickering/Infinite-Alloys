package infinitealloys.core;

import infinitealloys.network.PacketValidAlloys;
import infinitealloys.util.Funcs;
import infinitealloys.util.NetworkManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class EventHandler implements ICraftingHandler {

	private final String fileName = "InfiniteAlloys.dat";
	private String worldDir;

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
		if(Funcs.isServer()) {
			worldDir = DimensionManager.getWorld(0).getChunkSaveLocation().getPath();
			try {
				NBTTagCompound nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(worldDir + "/" + fileName));
				InfiniteAlloys.instance.loadAlloyData(nbtTagCompound);
				NetworkManager.loadData(nbtTagCompound, event.world);
			}catch(FileNotFoundException e) {
				InfiniteAlloys.instance.generateAlloyData();
			}catch(Exception e) {
				InfiniteAlloys.instance.generateAlloyData();
				e.printStackTrace();
			}
		}
		else
			InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear(); // Clear the list of blocks to be outlines by the x-ray on unload. This is only run client-side
	}

	@ForgeSubscribe
	public void onWorldSave(Save event) {
		if(Funcs.isServer()) {
			NBTTagCompound nbtTagCompound = new NBTTagCompound(); // An NBTTagCompound for the info to be stored in
			InfiniteAlloys.instance.saveAlloyData(nbtTagCompound); // Add the alloy data to the NBTTagCompound
			NetworkManager.saveData(nbtTagCompound); // Add the network data to the NBTTagCompound

			try {
				CompressedStreamTools.writeCompressed(nbtTagCompound, new FileOutputStream(worldDir + "/" + fileName)); // Write the NBT data to a file
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent e) {
		if(Funcs.isServer() && e.entity instanceof EntityPlayer) {
			PacketDispatcher.sendPacketToPlayer(PacketValidAlloys.getPacket(), (Player)e.entity);
			NetworkManager.syncAllNetworks((Player)e.entity);
		}
	}

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {}
}
