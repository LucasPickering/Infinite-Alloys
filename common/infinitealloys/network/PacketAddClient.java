package infinitealloys.network;

import infinitealloys.tile.IHost;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import com.google.common.io.ByteArrayDataInput;

public class PacketAddClient implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int dimensionID = data.readInt();
		int hostX = data.readInt();
		int hostY = data.readShort();
		int hostZ = data.readInt();
		Point client = new Point(data.readInt(), data.readShort(), data.readInt());
		if(Funcs.isClient()) {
			TileEntity host = player.worldObj.getBlockTileEntity(hostX, hostY, hostZ);
			if(host instanceof IHost)
				((IHost)host).addClient(player, client, false);
		}
		else {
			TileEntity host = DimensionManager.getWorld(dimensionID).getBlockTileEntity(hostX, hostY, hostZ);
			if(host instanceof IHost)
				((IHost)host).addClient(player, client, false);
		}
	}

	public static Packet250CustomPayload getPacket(int dimensionID, Point host, Point client) {
		return PacketHandler.getPacket(PacketHandler.ADD_CLIENT, dimensionID, host.x, (short)host.y, host.z, client.x, (short)client.y, client.z);
	}
}
