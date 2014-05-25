package infinitealloys.network;

import infinitealloys.tile.TEEXray;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import com.google.common.io.ByteArrayDataInput;

public class PacketXraySearch implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int x = data.readInt();
		int y = data.readInt();
		int z = data.readInt();
		((TEEXray)player.worldObj.getBlockTileEntity(x, y, z)).shouldSearch = true;
	}

	public static Packet250CustomPayload getPacket(Point xray) {
		return PacketHandler.getPacket(PacketHandler.XRAY_SEARCH, xray);
	}
}
