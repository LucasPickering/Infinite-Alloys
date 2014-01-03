package infinitealloys.network;

import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TEHComputer;
import infinitealloys.tile.TEHEnergyStorage;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import com.google.common.io.ByteArrayDataInput;

public class PacketTEServerToClient implements PacketIA {

	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int x = data.readInt();
		short y = data.readShort();
		int z = data.readInt();
		TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityMachine) {
			byte orientation = data.readByte();
			int upgrades = data.readInt();
			((TileEntityMachine)te).handlePacketDataFromServer(orientation, upgrades);
			if(te instanceof TEHComputer) {
				TEHComputer tec = (TEHComputer)te;
				tec.connectedMachines.clear();
				byte size = data.readByte();
				for(int i = 0; i < size; i++)
					tec.connectedMachines.add(new Point(data.readInt(), data.readShort(), data.readInt()));
			}
			else if(te instanceof TEHEnergyStorage) {
				TEHEnergyStorage tees = ((TEHEnergyStorage)te);
				int currentRK = data.readInt();
				tees.handlePacketDataFromServer(currentRK);
				tees.connectedMachines.clear();
				byte size = data.readByte();
				for(int i = 0; i < size; i++) {
					int machX = data.readInt();
					short machY = data.readShort();
					int machZ = data.readInt();
					tees.connectedMachines.add(new Point(machX, machY, machZ));
					((TileEntityElectric)player.worldObj.getBlockTileEntity(machX, machY, machZ)).energyStorage = tees;

				}
			}
			else if(te instanceof TileEntityElectric) {
				int processProgress = data.readInt();
				((TileEntityElectric)te).handlePacketDataFromServer(processProgress);
				if(te instanceof TEEMetalForge) {
					byte[] recipeAmts = new byte[Consts.METAL_COUNT];
					for(int i = 0; i < recipeAmts.length; i++)
						recipeAmts[i] = data.readByte();
					((TEEMetalForge)te).handlePacketDataFromClient(recipeAmts);
				}
				if(te instanceof TEEAnalyzer) {
					byte unlockedAlloyCount = data.readByte();
					((TEEAnalyzer)te).handlePacketDataFromClient(unlockedAlloyCount);
				}
				else if(te instanceof TEEXray) {
					TEEXray tex = (TEEXray)te;
					tex.detectedBlocks.clear();
					byte size = data.readByte();
					for(int i = 0; i < size; i++)
						tex.detectedBlocks.add(new Point(data.readInt(), data.readShort(), data.readInt()));
				}
				else if(te instanceof TEEPasture) {
					byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
					for(int i = 0; i < mobActions.length; i++)
						mobActions[i] = data.readByte();
					((TEEPasture)te).handlePacketData(mobActions);
				}
			}
		}
	}

	public static Packet250CustomPayload getPacket(TileEntityMachine tem) {
		Object[] data = tem.getSyncDataToClient();
		if(data != null)
			return PacketHandler.getPacket(PacketHandler.TE_SERVER_TO_CLIENT, tem.xCoord, (short)tem.yCoord, tem.zCoord, data);
		return null;
	}
}
