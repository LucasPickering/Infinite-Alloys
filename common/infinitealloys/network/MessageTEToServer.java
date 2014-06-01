package infinitealloys.network;

import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageTEToServer implements IMessage, IMessageHandler<MessageTEToServer, IMessage> {

	private Point machine;
	private Object[] data;
	private ByteBuf bytes;

	public MessageTEToServer() {}

	public MessageTEToServer(TileEntityMachine tem) {
		machine = tem.coords();

		if(tem.getWorldObj().isRemote)
			data = tem.getSyncDataToServer();
		else
			data = tem.getSyncDataToClient();
	}

	@Override
	public void fromBytes(ByteBuf bytes) {
		machine = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
		this.bytes = bytes;
	}

	@Override
	public void toBytes(ByteBuf bytes) {
		NetworkHandler.writeObject(bytes, machine);
		NetworkHandler.writeObject(bytes, data);
	}

	@Override
	public IMessage onMessage(MessageTEToServer message, MessageContext context) {
		machine = message.machine;
		bytes = message.bytes;

		TileEntity te = Funcs.getTileEntity(context.getServerHandler().playerEntity.worldObj, machine);

		if(te instanceof TileEntityMachine) {
			switch(((TileEntityMachine)te).getEnumMachine()) {
				case METAL_FORGE:
					int recipeAlloyID = bytes.readInt();
					((TEEMetalForge)te).handlePacketDataFromClient(recipeAlloyID);
					break;

				case ANALYZER:
					int alloys = bytes.readInt();
					int targetAlloy = bytes.readInt();
					((TEEAnalyzer)te).handlePacketDataFromClient(alloys, targetAlloy);
					break;

				case XRAY:
					boolean shouldSearch = bytes.readBoolean();
					((TEEXray)te).handlePacketDataFromClient(shouldSearch);
					break;

				case PASTURE:
					byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
					for(int i = 0; i < mobActions.length; i++)
						mobActions[i] = bytes.readByte();
					((TEEPasture)te).handlePacketData(mobActions);
					break;
			}
		}

		return null;
	}
}
