package infinitealloys.network;

import infinitealloys.tile.TEEXray;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import com.google.common.io.ByteArrayDataInput;

public class PacketXraySearch implements PacketIA {

	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int x = data.readInt();
		short y = data.readShort();
		int z = data.readInt();
		((TEEXray)player.worldObj.getBlockTileEntity(x, y, z)).shouldSearch = true;
	}

	public static Packet250CustomPayload getPacket(int x, short y, int z) {
		return PacketHandler.getPacket(PacketHandler.XRAY_SEARCH, x, y, z);
	}
}
