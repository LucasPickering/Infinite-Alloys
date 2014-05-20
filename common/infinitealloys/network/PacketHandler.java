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
	public static final byte WAND = 5;
	public static final byte ADD_CLIENT = 6;
	public static final byte REMOVE_CLIENT = 7;
	private static final Class[] packetClasses = { PacketValidAlloys.class, PacketTEServerToClient.class, PacketTEClientToServer.class, PacketOpenGui.class,
		PacketXraySearch.class, PacketWand.class, PacketRemoveClient.class, PacketAddClient.class, PacketRemoveClient.class };

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		final ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
		final byte packetIndex = data.readByte();
		try {
			((PacketIA)packetClasses[packetIndex].newInstance()).execute((EntityPlayer)player, data);
		}catch(final Exception e) {
			e.printStackTrace();
		}
	}

	public static Packet250CustomPayload getPacket(byte id, Object... data) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(id);
			for(Object datum : data)
				writeObject(dos, datum);
		}catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}

	private static void writeObject(DataOutputStream dos, Object o) throws IOException {
		if(o instanceof Byte)
			dos.writeByte((Byte)o);

		else if(o instanceof Short)
			dos.writeShort((Short)o);

		else if(o instanceof Integer)
			dos.writeInt((Integer)o);

		else if(o instanceof Double)
			dos.writeDouble((Double)o);

		else if(o instanceof Boolean)
			dos.writeBoolean((Boolean)o);

		else if(o instanceof Object[])
			for(final Object o2 : (Object[])o)
				writeObject(dos, o2);

		else if(o instanceof byte[])
			for(final byte b : (byte[])o)
				writeObject(dos, b);

		else if(o instanceof short[])
			for(final short s : (short[])o)
				writeObject(dos, s);

		else if(o instanceof int[])
			for(final int i : (int[])o)
				writeObject(dos, i);

		else
			System.out.println("Infinite Alloys: Unknown type " + o.getClass().getName() + " for object " + o);
	}
}
