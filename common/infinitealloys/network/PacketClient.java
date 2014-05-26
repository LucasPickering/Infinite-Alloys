package infinitealloys.network;

import infinitealloys.tile.IHost;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

public class PacketClient implements IPacketIA {

	private boolean adding;
	private int dimensionID;
	private Point host;
	private Point client;

	public PacketClient() {}

	public PacketClient(boolean adding, int dimensionID, Point host, Point client) {
		this.adding = adding;
		this.dimensionID = dimensionID;
		this.host = host;
		this.client = client;
	}

	@Override
	public void readBytes(ByteBuf bytes) {
		adding = bytes.readBoolean();
		dimensionID = bytes.readInt();
		host = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
		client = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		ChannelHandler.writeObject(bytes, adding);
		ChannelHandler.writeObject(bytes, dimensionID);
		ChannelHandler.writeObject(bytes, host);
		ChannelHandler.writeObject(bytes, client);
	}

	@Override
	public void executeServer(EntityPlayer player) {
		TileEntity te = Funcs.getTileEntity(DimensionManager.getWorld(dimensionID), host);
		if(te instanceof IHost) {
			if(adding)
				((IHost)te).addClient(player, client, false);
			else
				((IHost)te).removeClient(client, false);
		}
	}

	@Override
	public void executeClient(EntityPlayer player) {
		if(dimensionID == player.dimension) {
			TileEntity te = Funcs.getTileEntity(player.worldObj, host);
			if(te instanceof IHost) {
				if(adding)
					((IHost)te).addClient(null, client, false);
				else
					((IHost)te).removeClient(client, false);
			}
		}
	}
}
