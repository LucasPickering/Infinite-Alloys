package infinitealloys.network;

import infinitealloys.tile.IHost;
import infinitealloys.util.Funcs;
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
		Point host = new Point(data.readInt(), data.readInt(), data.readInt());
		Point client = new Point(data.readInt(), data.readInt(), data.readInt());
		if(player.worldObj.isRemote) {
			TileEntity te = Funcs.getBlockTileEntity(player.worldObj, host);
			if(te instanceof IHost) {
				if(adding)
					((IHost)te).addClient(null, client, false);
				else
					((IHost)te).removeClient(client, false);
			}
		}
		else {
			TileEntity te = Funcs.getBlockTileEntity(DimensionManager.getWorld(dimensionID), host);
			if(te instanceof IHost) {
				if(adding)
					((IHost)te).addClient(player, client, false);
				else
					((IHost)te).removeClient(client, false);
			}
		}
	}

	public static Packet250CustomPayload getPacket(boolean adding, int dimensionID, Point host, Point client) {
		return PacketHandler.getPacket(PacketHandler.CLIENT, adding, dimensionID, host.x, host.y, host.z, client.x, client.y, client.z);
	}
}
