package infinitealloys.network;

import infinitealloys.core.NetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import com.google.common.io.ByteArrayDataInput;

public class PacketRemoveNetwork implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int networkID = data.readInt();
		NetworkManager.deleteNetwork(networkID);
	}

	public static Packet250CustomPayload getPacket(int networkID) {
		return PacketHandler.getPacket(PacketHandler.REMOVE_NETWORK, networkID);
	}
}
