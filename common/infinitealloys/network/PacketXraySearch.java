package infinitealloys.network;

import infinitealloys.tile.TEEXray;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import com.google.common.io.ByteArrayDataInput;

public class PacketXraySearch implements PacketIA {

	public void execute(World world, ByteArrayDataInput data) {
		int x = data.readInt();
		short y = data.readShort();
		int z = data.readInt();
		((TEEXray)world.getBlockTileEntity(x, y, z)).shouldSearch = true;
	}

	public static Packet250CustomPayload getPacket(int x, short y, int z) {
		return PacketHandler.getPacket(PacketHandler.XRAY_SEARCH, x, y, z);
	}
}
