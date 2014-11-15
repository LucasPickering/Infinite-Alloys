package infinitealloys.network;

import infinitealloys.tile.*;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageTEToClient implements IMessage, IMessageHandler<MessageTEToClient, IMessage> {

	private Point tePoint;
	private Object[] data;
	private ByteBuf bytes;

	public MessageTEToClient() {}

	public MessageTEToClient(TileEntityIA teia) {
		tePoint = teia.coords();
		data = teia.getSyncDataToClient();
	}

	@Override
	public void fromBytes(ByteBuf bytes) {
		tePoint = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
		this.bytes = bytes;
	}

	@Override
	public void toBytes(ByteBuf bytes) {
		NetworkHandler.writeObject(bytes, tePoint);
		NetworkHandler.writeObject(bytes, data);
	}

	@Override
	public IMessage onMessage(MessageTEToClient message, MessageContext context) {
		tePoint = message.tePoint;
		bytes = message.bytes;

		TileEntity te = Funcs.getTileEntity(Minecraft.getMinecraft().theWorld, tePoint);

		if(te instanceof TileEntityIA) {
			byte orientation = bytes.readByte();
			((TileEntityIA)te).handlePacketDataFromServer(orientation);

			if(te instanceof TileEntityMachine) {
				int[] upgrades = new int[Consts.UPGRADE_TYPE_COUNT];
				for(int i = 0; i < upgrades.length; i++)
					upgrades[i] = bytes.readInt();
				((TileEntityMachine)te).handlePacketDataFromServer(upgrades);

				if(te instanceof TileEntityElectric) {
					int processProgress = bytes.readInt();
					((TileEntityElectric)te).handlePacketDataFromServer(processProgress);

					switch(((TileEntityElectric)te).getEnumMachine()) {
						case METAL_FORGE:
							byte recipeAlloyID = bytes.readByte();
							((TEEMetalForge)te).handlePacketDataFromServer(recipeAlloyID);
							break;

						case XRAY:
							int detectedBlocksSize = bytes.readInt();
							Point[] detectedBlocks = new Point[detectedBlocksSize];
							for(int i = 0; i < detectedBlocksSize; i++)
								detectedBlocks[i] = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
							((TEEXray)te).handlePacketDataFromServer(detectedBlocks);
							break;

						case PASTURE:
							byte[] mobActions = new byte[TEEPasture.mobClasses.length];
							for(int i = 0; i < mobActions.length; i++)
								mobActions[i] = bytes.readByte();
							((TEEPasture)te).handlePacketData(mobActions);
							break;

						case ENERGY_STORAGE:
							int currentRK = bytes.readInt();
							int baseRKPerTick = bytes.readInt();
							((TEEEnergyStorage)te).handlePacketDataFromServer(currentRK, baseRKPerTick);
							break;
					}
				}
			}
			else if(te instanceof TEIASummoner) {
				int storedXP = bytes.readInt();
				((TEIASummoner)te).handlePacketData(storedXP);
			}
		}

		return null;
	}
}
