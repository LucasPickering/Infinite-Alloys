package infinitealloys.handlers;

import infinitealloys.IAWorldData;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class EventHandler {

	private String world;

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0).getChunkSaveLocation().getPath();
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(world + "/IAWorldData.dat");
			ois = new ObjectInputStream(fis);
			InfiniteAlloys.instance.worldData = (IAWorldData)ois.readObject();
		}
		catch(IOException e) {
			if(InfiniteAlloys.instance.worldData == null) {
				Random random = new Random();
				int[] validAlloys = new int[References.validAlloyCount];
				for(int i = 0; i < References.validAlloyCount; i++) {
					int metalCount = References.metalCount;
					byte[] alloyDigits = new byte[metalCount];
					for(int j = 0; j < metalCount; j++) {
						int min = InfiniteAlloys.intAtPos(References.alloyRadix, metalCount, References.validAlloyMins[i], j);
						int max = InfiniteAlloys.intAtPos(References.alloyRadix, metalCount, References.validAlloyMaxes[i], j);
						alloyDigits[j] = (byte)(min + (max == min ? 0 : random.nextInt(max - min)));
					}
					String alloy = "";
					for(int digit : alloyDigits)
						alloy = alloy + digit;
					validAlloys[i] = new Integer(alloy);
					System.out.println("SPOILER ALERT! Alloy " + i + ": " + validAlloys[i]);
				}
				InfiniteAlloys.instance.worldData = new IAWorldData(validAlloys);
			}
		}
		catch(Exception e) {
			System.out.println("Error while deserializing Infinite Alloys world data");
			e.printStackTrace();
		}
		finally {
			try {
				ois.close();
				fis.close();
			}
			catch(Exception e) {}
		}
	}

	@ForgeSubscribe
	public void onWorldUnload(Unload event) {
		if(InfiniteAlloys.instance.worldData == null || FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(world + "/IAWorldData.dat");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(InfiniteAlloys.instance.worldData);
		}
		catch(Exception e) {
			System.out.println("Error while serializing Infinite Alloys world data");
			e.printStackTrace();
		}
		finally {
			try {
				oos.close();
				fos.close();
			}
			catch(Exception e) {}
		}
		InfiniteAlloys.instance.worldData = null;
	}

	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent e) {
		if(FMLCommonHandler.instance().getEffectiveSide().isClient() || !(e.entity instanceof EntityPlayer))
			return;
		PacketDispatcher.sendPacketToPlayer(PacketHandler.getWorldDataPacket(), (Player)e.entity);
	}
}
