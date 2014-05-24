package infinitealloys.network;

import infinitealloys.tile.IHost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import com.google.common.io.ByteArrayDataInput;
import cpw.mods.fml.common.network.Player;

public class PacketRequestSync implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int hostX = data.readInt();
		int hostY = data.readInt();
		int hostZ = data.readInt();
		TileEntity host = player.worldObj.getBlockTileEntity(hostX, hostY, hostZ);
		if(host instanceof IHost) {
			((IHost)host).syncAllClients((Player)player);
		}
	}

	public static Packet250CustomPayload getPacket(int x, int y, int z) {
		return PacketHandler.getPacket(PacketHandler.CLIENT, x, y, z);
	}
}
