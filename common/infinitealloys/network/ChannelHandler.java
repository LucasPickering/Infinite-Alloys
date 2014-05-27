package infinitealloys.network;

import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<IPacketIA> {

	private final Class[] packetClasses = { PacketValidAlloys.class, PacketTESync.class, PacketOpenGui.class, PacketWand.class, PacketClient.class, };

	public ChannelHandler() {
		for(int i = 0; i < packetClasses.length; i++)
			addDiscriminator(i, packetClasses[i]);
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, IPacketIA packet, ByteBuf data) throws Exception {
		packet.writeBytes(data);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, IPacketIA packet) {
		packet.readBytes(data);
		switch(FMLCommonHandler.instance().getEffectiveSide()) {
			case CLIENT:
				packet.executeClient(Minecraft.getMinecraft().thePlayer);
				break;
			case SERVER:
				INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
				packet.executeServer(((NetHandlerPlayServer)netHandler).playerEntity);
				break;
		}
	}

	public static void writeObject(ByteBuf bytes, Object o) {
		if(o == null)
			return;
		else if(o instanceof Point) {
			writeObject(bytes, ((Point)o).x);
			writeObject(bytes, ((Point)o).y);
			writeObject(bytes, ((Point)o).z);
		}
		else if(o instanceof Byte)
			bytes.writeByte((Byte)o);

		else if(o instanceof Short)
			bytes.writeShort((Short)o);

		else if(o instanceof Integer)
			bytes.writeInt((Integer)o);

		else if(o instanceof Double)
			bytes.writeDouble((Double)o);

		else if(o instanceof Boolean)
			bytes.writeBoolean((Boolean)o);

		else if(o instanceof Object[])
			for(final Object o2 : (Object[])o)
				writeObject(bytes, o2);

		else if(o instanceof byte[])
			for(final byte b : (byte[])o)
				writeObject(bytes, b);

		else if(o instanceof short[])
			for(final short s : (short[])o)
				writeObject(bytes, s);

		else if(o instanceof int[])
			for(final int i : (int[])o)
				writeObject(bytes, i);

		else
			System.out.println("Infinite Alloys: Unknown type " + o.getClass().getName() + " for object " + o);
	}
}