package infinitealloys.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import com.google.common.io.ByteArrayDataInput;

public interface IPacketIA {

	public void readBytes(ByteBuf bytes);

	public void writeBytes(ByteBuf bytes);

	public void executeServer(EntityPlayer player);

	public void executeClient(EntityPlayer player);
}
