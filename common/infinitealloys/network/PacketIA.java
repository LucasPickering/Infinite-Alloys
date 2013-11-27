package infinitealloys.network;

import net.minecraft.world.World;
import com.google.common.io.ByteArrayDataInput;

public interface PacketIA {

	public void execute(World world, ByteArrayDataInput data);
}
