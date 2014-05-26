package infinitealloys.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TEEXray;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;

public class PacketXraySearch implements IPacketIA {

	private Point xray;

	public PacketXraySearch() {}

	public PacketXraySearch(Point xray) {
		this.xray = xray;
	}

	@Override
	public void readBytes(ByteBuf bytes) {
		xray = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		ChannelHandler.writeObject(bytes, xray);
	}

	@Override
	public void executeServer(EntityPlayer player) {
		((TEEXray)player.worldObj.getTileEntity(xray.x, xray.y, xray.z)).shouldSearch = true;
	}

	@Override
	public void executeClient(EntityPlayer player) {}
}
