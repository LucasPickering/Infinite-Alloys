package infinitealloys.handlers;

import infinitealloys.InfiniteAlloys;
import infinitealloys.IAWorldData;
import infinitealloys.References;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class EventHandler {

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
		try {
			FileInputStream fis = new FileInputStream("./saves/" + event.world.getWorldInfo().getWorldName() + "/IAWorldData.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			InfiniteAlloys.instance.worldData = (IAWorldData)ois.readObject();
			ois.close();
			fis.close();
		}
		catch(IOException e) {
			if(InfiniteAlloys.instance.worldData == null) {
				Random random = new Random();
				int[] validAlloys = new int[References.validAlloyCount];
				for(int i = 0; i < References.validAlloyCount; i++)
					System.out.println(validAlloys[i] = References.validAlloyMins[i] + random.nextInt(References.validAlloyMaxes[i] - References.validAlloyMins[i]));
				InfiniteAlloys.instance.worldData = new IAWorldData(validAlloys);
			}
		}
		catch(ClassNotFoundException e) {
			System.out.println("Error while deserializing Infinite Alloys world data");
		}
	}

	@ForgeSubscribe
	public void onWorldUnload(Unload event) {
		try {
			FileOutputStream fos = new FileOutputStream("./saves/" + event.world.getWorldInfo().getWorldName() + "/IAWorldData.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(InfiniteAlloys.instance.worldData);
			oos.close();
			fos.close();
		}
		catch(IOException e) {
			System.out.println("Error while serializing Infinite Alloys world data");
		}
	}
}
