package infinitealloys.network;

import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.tile.TEEEnergyStorage;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import com.google.common.io.ByteArrayDataInput;

public class PacketTEServerToClient implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int x = data.readInt();
		int y = data.readInt();
		int z = data.readInt();
		TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityMachine) {
			((TileEntityMachine)te).handlePacketDataFromServer(data.readByte()/* orientation */, data.readShort()/* upgrades */);

			if(te instanceof TileEntityElectric) {
				int processProgress = data.readInt();
				int energyHostX = data.readInt();
				int energyHostY = data.readInt();
				int energyHostZ = data.readInt();
				if(energyHostY < 0)
					((TileEntityElectric)te).handlePacketDataFromServer(processProgress, null);
				else
					((TileEntityElectric)te).handlePacketDataFromServer(processProgress, new Point(energyHostX, energyHostY, energyHostZ));

				switch(((TileEntityElectric)te).getID()) {
					case MachineHelper.METAL_FORGE:
						int recipeAlloyID = data.readInt();
						int analyzerHostX = data.readInt();
						int analyzerHostY = data.readInt();
						int analyzerHostZ = data.readInt();
						if(analyzerHostY < 0)
							((TEEMetalForge)te).handlePacketDataFromServer(recipeAlloyID, null);
						else
							((TEEMetalForge)te).handlePacketDataFromServer(recipeAlloyID, new Point(analyzerHostX, analyzerHostY, analyzerHostZ));
						break;

					case MachineHelper.ANALYZER:
						int alloys = data.readInt();
						int targetAlloy = data.readInt();
						((TEEAnalyzer)te).handlePacketDataFromClient(alloys, targetAlloy);
						break;

					case MachineHelper.XRAY:
						((TEEXray)te).detectedBlocks.clear();
						for(int i = 0; i < data.readByte()/* Size */; i++)
							((TEEXray)te).detectedBlocks.add(new Point(data.readInt()/* X */, data.readShort()/* Y */, data.readInt()/* Z */));
						break;

					case MachineHelper.PASTURE:
						byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
						for(int i = 0; i < mobActions.length; i++)
							mobActions[i] = data.readByte();
						((TEEPasture)te).handlePacketData(mobActions);
						break;

					case MachineHelper.ENERGY_STORAGE:
						int currentRK = data.readInt();
						int baseRKPerTick = data.readInt();
						((TEEEnergyStorage)te).handlePacketDataFromServer(currentRK, baseRKPerTick);
						break;
				}
			}
		}
	}

	public static Packet250CustomPayload getPacket(TileEntityMachine tem) {
		Object[] data = tem.getSyncDataToClient();
		if(data != null)
			return PacketHandler.getPacket(PacketHandler.TE_SERVER_TO_CLIENT, tem.coords(), data);
		return null;
	}
}
