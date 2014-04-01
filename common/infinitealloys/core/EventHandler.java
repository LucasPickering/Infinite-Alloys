package infinitealloys.core;

import infinitealloys.network.PacketValidAlloys;
import infinitealloys.util.Funcs;
import infinitealloys.util.NetworkManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

// KEEP IN MIND: RIGHTMOST DIGITS ARE THE LESSER METALS WHILE LEFTMOST DIGITS ARE THE FANTASTICAL METALS
public class EventHandler implements ICraftingHandler {

	private final String fileName = "IAWorldData.dat";
	private String world;

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
		if(Funcs.isServer()) {
			File file = new File(world + "/" + fileName);
			if(file.exists()) {
				NBTTagCompound nbtTagCompound = new NBTTagCompound();
				try {
					nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file));
				}catch(final Exception e) {
					e.printStackTrace();
				}
				InfiniteAlloys.instance.loadAlloyData(nbtTagCompound);
				NetworkManager.loadData(nbtTagCompound);
			}
			else
				InfiniteAlloys.instance.generateAlloyData();
		}
	}

	@ForgeSubscribe
	public void onWorldUnload(Unload event) {
		if(Funcs.isServer()) {
			NBTTagCompound nbtTagCompound = new NBTTagCompound(); // An NBTTagCompound for the info to be stored in
			InfiniteAlloys.instance.saveAlloyData(nbtTagCompound); // Add the alloy data to the NBTTagCompound
			NetworkManager.saveData(nbtTagCompound); // Add the network data to the NBTTagCompound

			try {
				CompressedStreamTools.writeCompressed(nbtTagCompound, new FileOutputStream(new File(world + "/" + fileName))); // Write the NBT data to a file
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		else
			InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear(); // Clear the list of blocks to be outlines by the x-ray on unload. This is only run client-side

	}

	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent e) {
		if(e.entity instanceof EntityPlayer && Funcs.isServer())
			PacketDispatcher.sendPacketToPlayer(PacketValidAlloys.getPacket(), (Player)e.entity);
	}

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {}
}
