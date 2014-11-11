package infinitealloys.network;

import infinitealloys.tile.TEEEnergyStorage;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityIA;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.tile.TileEntitySummoner;
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

	private Point machine;
	private Object[] data;
	private ByteBuf bytes;

	public MessageTEToClient() {}

	public MessageTEToClient(TileEntityIA teia) {
		machine = teia.coords();

		if(teia.getWorldObj().isRemote)
			data = teia.getSyncDataToServer();
		else
			data = teia.getSyncDataToClient();
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
	public IMessage onMessage(MessageTEToClient message, MessageContext context) {
		machine = message.machine;
		bytes = message.bytes;

		TileEntity te = Funcs.getTileEntity(Minecraft.getMinecraft().theWorld, machine);

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
			else if(te instanceof TileEntitySummoner) {
				int storedXP = bytes.readInt();
				((TileEntitySummoner)te).handlePacketData(storedXP);
			}
		}

		return null;
	}
}
