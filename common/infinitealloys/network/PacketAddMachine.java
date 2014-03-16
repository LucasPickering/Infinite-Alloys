package infinitealloys.network;

import infinitealloys.tile.IHost;
import infinitealloys.util.MachineHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import com.google.common.io.ByteArrayDataInput;

public class PacketAddMachine implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		final int hostX = data.readInt();
		final short hostY = data.readShort();
		final int hostZ = data.readInt();
		final int clientX = data.readInt();
		final short clientY = data.readShort();
		final int clientZ = data.readInt();
		if(MachineHelper.isHost(player.worldObj, hostX, hostY, hostZ))
			((IHost)player.worldObj.getBlockTileEntity(hostX, hostY, hostZ)).addMachine(player, clientX, clientY, clientZ);
	}

	public static Packet250CustomPayload getPacket(int hostX, int hostY, int hostZ, int clientX, int clientY, int clientZ) {
		return PacketHandler.getPacket(PacketHandler.ADD_MACHINE, hostX, (short)hostY, hostZ, clientX, (short)clientY, clientZ);
	}
}
