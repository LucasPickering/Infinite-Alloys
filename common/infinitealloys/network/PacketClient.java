package infinitealloys.network;

import infinitealloys.tile.IHost;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import com.google.common.io.ByteArrayDataInput;

public class PacketClient implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		boolean adding = data.readBoolean();
		int dimensionID = data.readInt();
		int hostX = data.readInt();
		int hostY = data.readInt();
		int hostZ = data.readInt();
		Point client = new Point(data.readInt(), data.readInt(), data.readInt());
		if(player.worldObj.isRemote) {
			TileEntity host = player.worldObj.getBlockTileEntity(hostX, hostY, hostZ);
			if(host instanceof IHost) {
				if(adding)
					((IHost)host).addClient(player, client, false);
				else
					((IHost)host).removeClient(client, false);
			}
		}
		else {
			TileEntity host = DimensionManager.getWorld(dimensionID).getBlockTileEntity(hostX, hostY, hostZ);
			if(host instanceof IHost) {
				if(adding)
					((IHost)host).addClient(player, client, false);
				else
					((IHost)host).removeClient(client, false);
			}
		}
	}

	public static Packet250CustomPayload getPacket(boolean adding, int dimensionID, Point host, Point client) {
		return PacketHandler.getPacket(PacketHandler.CLIENT, adding, dimensionID, host, client);
	}
}
