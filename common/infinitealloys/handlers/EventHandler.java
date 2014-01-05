package infinitealloys.handlers;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.WorldData;
import infinitealloys.network.PacketWorldData;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.Funcs;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

// KEEP IN MIND: RIGHTMOST DIGITS ARE THE LESSER METALS WHILE LEFTMOST DIGITS ARE THE FANTASTICAL METALS
public class EventHandler implements ICraftingHandler {

	private String world;

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
		// If it's the server, look for stored alloy data and if they exist, load them. If not, generate new data.
		if(Funcs.isServer()) {
			world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0).getChunkSaveLocation().getPath();
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(world + "/WorldData.dat")); // Try to load existing data
				InfiniteAlloys.instance.worldData = (WorldData)ois.readObject();
				System.out.println("Successfully loaded IA alloys");
			}catch(final IOException e) {
				if(InfiniteAlloys.instance.worldData == null) { // If the world has no data, i.e. this is a new world
					final Random random = new Random();
					final int[] validAlloys = new int[Consts.VALID_ALLOY_COUNT]; // An array to hold the generated alloys
					for(int i = 0; i < Consts.VALID_ALLOY_COUNT; i++) { // For each alloy that needs to be generated
						int alloy = 0; // An int to hold each digit that is generated
						for(int j = 0; j < Consts.METAL_COUNT; j++) { // For each metal, i.e. for each digit in the alloy
							final int min = Funcs.intAtPos(EnumAlloy.values()[i].min, Consts.ALLOY_RADIX, j); // Metal's min value in the alloy
							final int max = Funcs.intAtPos(EnumAlloy.values()[i].max, Consts.ALLOY_RADIX, j); // Metal's max value in the alloy
							// Randomly gen a value in [min, max] and add it to the alloy
							alloy += (min + (max == min ? 0 : random.nextInt(max - min + 1))) * Math.pow(Consts.ALLOY_RADIX, j);
						}

						validAlloys[i] = Funcs.reduceAlloy(alloy); // Add the new alloy to the array
						if(Loader.isModLoaded("mcp"))
							System.out.println("SPOILER ALERT! Alloy " + i + ": " + validAlloys[i]); // Debug line, only runs in MCP
					}

					InfiniteAlloys.instance.worldData = new WorldData(validAlloys); // Create a WorldData instance to save these alloys
					System.out.println("Successfully generated IA alloys");
				}
			}catch(final Exception e) {
				System.out.println("Error while deserializing Infinite Alloys world data");
				e.printStackTrace();
			}
			finally {
				try {
					if(ois != null)
						ois.close();
				}catch(final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@ForgeSubscribe
	public void onWorldUnload(Unload event) {
		// Clear the list of blocks to be outlines by the x-ray on unload. Alloys are not stored client-side, so return.
		if(Funcs.isClient()) {
			InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
			return;
		}

		// If there is stored alloy data for this world, serialize them to be saved and reloaded next session
		if(InfiniteAlloys.instance.worldData != null) {
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(new FileOutputStream(world + "/WorldData.dat"));
				oos.writeObject(InfiniteAlloys.instance.worldData);
			}catch(final Exception e) {
				System.out.println("Error while serializing Infinite Alloys world data");
				e.printStackTrace();
			}
			finally {
				try {
					if(oos != null)
						oos.close();
				}catch(final Exception e) {
					e.printStackTrace();
				}
			}
			InfiniteAlloys.instance.worldData = null;
		}
	}

	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent e) {
		if(e.entity instanceof EntityPlayer && Funcs.isServer())
			PacketDispatcher.sendPacketToPlayer(PacketWorldData.getPacket(), (Player)e.entity);
	}

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {}
}
