package infinitealloys.network;

import infinitealloys.tile.TEEEnergyStorage;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
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

	public MessageTEToClient(TileEntityMachine tem) {
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
	public IMessage onMessage(MessageTEToClient message, MessageContext context) {
		TileEntity te = Funcs.getTileEntity(Minecraft.getMinecraft().theWorld, machine);

		if(te instanceof TileEntityMachine) {
			byte orientation = bytes.readByte();
			int upgrades = bytes.readInt();
			((TileEntityMachine)te).handlePacketDataFromServer(orientation, upgrades);

			if(te instanceof TileEntityElectric) {
				int processProgress = bytes.readInt();
				int energyHostX = bytes.readInt();
				int energyHostY = bytes.readInt();
				int energyHostZ = bytes.readInt();
				if(energyHostY < 0)
					((TileEntityElectric)te).handlePacketDataFromServer(processProgress, null);
				else
					((TileEntityElectric)te).handlePacketDataFromServer(processProgress, new Point(energyHostX, energyHostY, energyHostZ));

				switch(((TileEntityElectric)te).getID()) {
					case MachineHelper.METAL_FORGE:
						int recipeAlloyID = bytes.readInt();
						int analyzerHostX = bytes.readInt();
						int analyzerHostY = bytes.readInt();
						int analyzerHostZ = bytes.readInt();
						if(analyzerHostY < 0)
							((TEEMetalForge)te).handlePacketDataFromServer(recipeAlloyID, null);
						else
							((TEEMetalForge)te).handlePacketDataFromServer(recipeAlloyID, new Point(analyzerHostX, analyzerHostY, analyzerHostZ));
						break;

					case MachineHelper.XRAY:
						((TEEXray)te).detectedBlocks.clear();
						int detectedBlocksSize = bytes.readInt();
						for(int i = 0; i < detectedBlocksSize; i++)
							((TEEXray)te).detectedBlocks.add(new Point(bytes.readInt()/* X */, bytes.readInt()/* Y */, bytes.readInt()/* Z */));
						break;

					case MachineHelper.PASTURE:
						byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
						for(int i = 0; i < mobActions.length; i++)
							mobActions[i] = bytes.readByte();
						((TEEPasture)te).handlePacketData(mobActions);
						break;

					case MachineHelper.ENERGY_STORAGE:
						int currentRK = bytes.readInt();
						int baseRKPerTick = bytes.readInt();
						((TEEEnergyStorage)te).handlePacketDataFromServer(currentRK, baseRKPerTick);
						break;
				}
			}
		}

		return null;
	}
}
