package infinitealloys.network;

import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TEEEnergyStorage;
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
		final int x = data.readInt();
		final short y = data.readShort();
		final int z = data.readInt();
		final TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityMachine) {
			((TileEntityMachine)te).handlePacketDataFromServer(data.readByte()/* orientation */, data.readShort()/* upgrades */);

			if(te instanceof TileEntityElectric) {
				final int processProgress = data.readInt();
				((TileEntityElectric)te).handlePacketDataFromServerElectric(processProgress);

				switch(((TileEntityElectric)te).getID()) {
					case MachineHelper.METAL_FORGE:
						byte recipeAlloyID = data.readByte();
						((TEEMetalForge)te).handlePacketDataFromClient(recipeAlloyID);
						break;

					case MachineHelper.ANALYZER:
						((TEEAnalyzer)te).handlePacketDataFromClient(data.readInt(), data.readInt()/* unlockedAlloyCount */);
						break;

					case MachineHelper.XRAY:
						final TEEXray tex = (TEEXray)te;
						tex.detectedBlocks.clear();
						for(int i = 0; i < data.readByte()/* Size */; i++)
							tex.detectedBlocks.add(new Point(data.readInt()/* X */, data.readShort()/* Y */, data.readInt()/* Z */));
						break;

					case MachineHelper.PASTURE:
						final byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
						for(int i = 0; i < mobActions.length; i++)
							mobActions[i] = data.readByte();
						((TEEPasture)te).handlePacketData(mobActions);
						break;

					case MachineHelper.ENERGY_STORAGE:
						((TEEEnergyStorage)te).handlePacketDataFromServer(data.readInt()/* ticksToProcess */, data.readInt()/* currentRK */);
						break;
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
