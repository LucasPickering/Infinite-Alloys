package infinitealloys.network;

import infinitealloys.core.NetworkManager;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import com.google.common.io.ByteArrayDataInput;

public class PacketCreateNetwork implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int networkID = data.readInt();
		byte type = data.readByte();
		int dimensionID = data.readInt();
		int hostX = data.readInt();
		int hostY = data.readShort();
		int hostZ = data.readInt();
		NetworkManager.createNetwork(networkID, type, dimensionID, new Point(hostX, hostY, hostZ));
	}

	public static Packet250CustomPayload getPacket(int networkID, byte type, int dimensionID, Point host) {
		return PacketHandler.getPacket(PacketHandler.CREATE_NETWORK, networkID, type, dimensionID, host.x, (short)host.y, host.z);
	}
}
