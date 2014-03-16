package infinitealloys.network;

import net.minecraft.entity.player.EntityPlayer;
import com.google.common.io.ByteArrayDataInput;

public interface PacketIA {

	public void execute(EntityPlayer player, ByteArrayDataInput data);
}
