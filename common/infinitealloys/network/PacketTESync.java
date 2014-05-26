package infinitealloys.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TEEAnalyzer;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

public class PacketTESync implements IPacketIA {

	private Point machine;
	private Object[] data;
	private ByteBuf bytes;

	public PacketTESync() {}

	public PacketTESync(TileEntityMachine tem) {
		this.machine = tem.coords();
		switch(FMLCommonHandler.instance().getSide()) {
			case CLIENT:
				data = tem.getSyncDataToServer();
				break;

			case SERVER:
				data = tem.getSyncDataToClient();
				break;
		}
	}

	@Override
	public void readBytes(ByteBuf bytes) {
		Point machine = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
		this.bytes = bytes;
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		ChannelHandler.writeObject(bytes, machine);
		ChannelHandler.writeObject(bytes, data);
	}

	@Override
	public void executeServer(EntityPlayer player) {
		TileEntity te = Funcs.getTileEntity(player.worldObj, machine);

		if(te instanceof TEEMetalForge) {
			int recipeAlloyID = bytes.readInt();
			((TEEMetalForge)te).handlePacketDataFromClient(recipeAlloyID);
		}

		else if(te instanceof TEEPasture) {
			byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
			for(int i = 0; i < mobActions.length; i++)
				mobActions[i] = bytes.readByte();
			((TEEPasture)te).handlePacketData(mobActions);
		}
	}

	@Override
	public void executeClient(EntityPlayer player) {
		TileEntity te = Funcs.getTileEntity(player.worldObj, machine);

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

					case MachineHelper.ANALYZER:
						int alloys = bytes.readInt();
						int targetAlloy = bytes.readInt();
						((TEEAnalyzer)te).handlePacketDataFromClient(alloys, targetAlloy);
						break;

					case MachineHelper.XRAY:
						((TEEXray)te).detectedBlocks.clear();
						for(int i = 0; i < bytes.readByte()/* Size */; i++)
							((TEEXray)te).detectedBlocks.add(new Point(bytes.readInt()/* X */, bytes.readShort()/* Y */, bytes.readInt()/* Z */));
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
	}
}
