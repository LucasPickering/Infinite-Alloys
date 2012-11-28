package infinitealloys.handlers;

import infinitealloys.IAWorldData;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.FMLCommonHandler;

public class EventHandler {

	private String world;

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
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
					int radix = References.alloyRadix;
					int metalCount = References.metalCount;
					byte[] alloyDigits = new byte[metalCount];
					for(int j = 0; j < metalCount; j++) {
						int min = InfiniteAlloys.intAtPosRadix(radix, metalCount, References.validAlloyMins[i], j);
						int max = InfiniteAlloys.intAtPosRadix(radix, metalCount, References.validAlloyMaxes[i], j);
						alloyDigits[j] = (byte)(min + (max == min ? 0 : random.nextInt(max - min)));
					}
					String alloy = "";
					for(int digit : alloyDigits)
						alloy = alloy + digit;
					validAlloys[i] = new Integer(alloy);
					System.out.println("Alloy " + i + ": " + validAlloys[i]);
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
		System.out.println(">>>>>>" + InfiniteAlloys.instance.worldData);
	}

	@ForgeSubscribe
	public void onWorldUnload(Unload event) {
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
}
