package infinitealloys.network;

import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TEMEnergyStorage;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import com.google.common.io.ByteArrayDataInput;

public class PacketTEServerToClient implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		final int x = data.readInt();
		final short y = data.readShort();
		final int z = data.readInt();
		final TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityMachine) {
			final byte orientation = data.readByte();
			final int upgrades = data.readInt();
			((TileEntityMachine)te).handlePacketDataFromServer(orientation, upgrades);
			if(te instanceof TEMComputer) {
				final TEMComputer tec = (TEMComputer)te;
				tec.connectedMachines.clear();
				final byte size = data.readByte();
				for(int i = 0; i < size; i++)
					tec.connectedMachines.add(new Point(data.readInt(), data.readShort(), data.readInt()));
			}
			else if(te instanceof TileEntityElectric) {
				final int processProgress = data.readInt();
				((TileEntityElectric)te).handlePacketDataFromServerElectric(processProgress);
				if(te instanceof TEEMetalForge) {
					final byte[] recipeAmts = new byte[Consts.METAL_COUNT];
					for(int i = 0; i < recipeAmts.length; i++)
						recipeAmts[i] = data.readByte();
					((TEEMetalForge)te).handlePacketDataFromClient(recipeAmts);
				}
				if(te instanceof TEEAnalyzer) {
					final byte unlockedAlloyCount = data.readByte();
					((TEEAnalyzer)te).handlePacketDataFromClient(unlockedAlloyCount);
				}
				else if(te instanceof TEEXray) {
					final TEEXray tex = (TEEXray)te;
					tex.detectedBlocks.clear();
					final byte size = data.readByte();
					for(int i = 0; i < size; i++)
						tex.detectedBlocks.add(new Point(data.readInt(), data.readShort(), data.readInt()));
				}
				else if(te instanceof TEEPasture) {
					final byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
					for(int i = 0; i < mobActions.length; i++)
						mobActions[i] = data.readByte();
					((TEEPasture)te).handlePacketData(mobActions);
				}
				else if(te instanceof TEMEnergyStorage) {
					final TEMEnergyStorage tees = ((TEMEnergyStorage)te);
					final int currentRK = data.readInt();
					tees.handlePacketDataFromServer(currentRK);
					tees.connectedMachines.clear();
					final byte size = data.readByte();
					for(int i = 0; i < size; i++) {
						final int machX = data.readInt();
						final short machY = data.readShort();
						final int machZ = data.readInt();
						tees.connectedMachines.add(new Point(machX, machY, machZ));
						((TileEntityElectric)player.worldObj.getBlockTileEntity(machX, machY, machZ)).energyStorage = tees;

					}
				}
			}
		}
	}

	public static Packet250CustomPayload getPacket(TileEntityMachine tem) {
		final Object[] data = tem.getSyncDataToClient();
		if(data != null)
			return PacketHandler.getPacket(PacketHandler.TE_SERVER_TO_CLIENT, tem.xCoord, (short)tem.yCoord, tem.zCoord, data);
		return null;
	}
}
