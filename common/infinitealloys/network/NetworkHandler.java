package infinitealloys.network;

import infinitealloys.util.Consts;
import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkHandler {

	public static final SimpleNetworkWrapper simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Consts.MOD_ID);

	public static void init() {
		simpleNetworkWrapper.registerMessage(MessageNetworkEditToClient.class, MessageNetworkEditToClient.class, 0, Side.CLIENT);
		simpleNetworkWrapper.registerMessage(MessageNetworkEditToServer.class, MessageNetworkEditToServer.class, 1, Side.SERVER);
		simpleNetworkWrapper.registerMessage(MessageOpenGui.class, MessageOpenGui.class, 2, Side.CLIENT);
		simpleNetworkWrapper.registerMessage(MessageTEToClient.class, MessageTEToClient.class, 3, Side.CLIENT);
		simpleNetworkWrapper.registerMessage(MessageTEToServer.class, MessageTEToServer.class, 4, Side.SERVER);
		simpleNetworkWrapper.registerMessage(MessageWand.class, MessageWand.class, 5, Side.SERVER);
		simpleNetworkWrapper.registerMessage(MessageValidAlloys.class, MessageValidAlloys.class, 6, Side.CLIENT);
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