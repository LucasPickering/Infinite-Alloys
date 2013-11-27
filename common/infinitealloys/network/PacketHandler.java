package infinitealloys.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	public static final byte WORLD_DATA = 0;
	public static final byte TE_SERVER_TO_CLIENT = 1;
	public static final byte TE_CLIENT_TO_SERVER = 2;
	public static final byte OPEN_GUI = 3;
	public static final byte XRAY_SEARCH = 4;
	private static final Class[] packetClasses = { PacketWorldData.class, PacketTEServerToClient.class, PacketTEClientToServer.class, PacketOpenGui.class, PacketXraySearch.class };

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
		byte packetIndex = data.readByte();
		try {
			((PacketIA)packetClasses[packetIndex].newInstance()).execute(((EntityPlayer)player).worldObj, data);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static Packet250CustomPayload getPacket(int id, Object... data) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(id);
			for(Object datum : data) {
				if(datum instanceof Byte)
					dos.writeByte((Byte)datum);

				else if(datum instanceof byte[])
					for(byte datum2 : (byte[])datum)
						dos.writeByte(datum2);

				else if(datum instanceof Short)
					dos.writeShort((Short)datum);

				else if(datum instanceof short[])
					for(short datum2 : (short[])datum)
						dos.writeShort(datum2);

				else if(datum instanceof Integer)
					dos.writeInt((Integer)datum);

				else if(datum instanceof int[])
					for(int datum2 : (int[])datum)
						dos.writeInt(datum2);

				else if(datum instanceof Double)
					dos.writeDouble((Double)datum);

				else if(datum instanceof Boolean)
					dos.writeBoolean((Boolean)datum);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}
}
