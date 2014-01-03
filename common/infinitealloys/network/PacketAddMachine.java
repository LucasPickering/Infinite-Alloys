package infinitealloys.network;

import infinitealloys.tile.TileEntityHost;
import infinitealloys.util.MachineHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import com.google.common.io.ByteArrayDataInput;

public class PacketAddMachine implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int hostX = data.readInt();
		short hostY = data.readShort();
		int hostZ = data.readInt();
		int clientX = data.readInt();
		short clientY = data.readShort();
		int clientZ = data.readInt();
		if(MachineHelper.isHost(player.worldObj, hostX, hostY, hostZ))
			((TileEntityHost)player.worldObj.getBlockTileEntity(hostX, hostY, hostZ)).addMachine(player, clientX, clientY, clientZ);
	}

	public static Packet250CustomPayload getPacket(int hostX, int hostY, int hostZ, int clientX, int clientY, int clientZ) {
		return PacketHandler.getPacket(PacketHandler.ADD_MACHINE, hostX, (short)hostY, hostZ, clientX, (short)clientY, clientZ);
	}
}
