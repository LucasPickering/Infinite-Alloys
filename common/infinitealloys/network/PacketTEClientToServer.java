package infinitealloys.network;

import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEHComputer;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import com.google.common.io.ByteArrayDataInput;

public class PacketTEClientToServer implements PacketIA {

	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		int x = data.readInt();
		short y = data.readShort();
		int z = data.readInt();
		TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
		if(te instanceof TEEMetalForge) {
			byte[] recipeAmts = new byte[Consts.METAL_COUNT];
			for(int i = 0; i < recipeAmts.length; i++)
				recipeAmts[i] = data.readByte();
			((TEEMetalForge)te).handlePacketDataFromClient(recipeAmts);
		}
		else if(te instanceof TEEPasture) {
			byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
			for(int i = 0; i < mobActions.length; i++)
				mobActions[i] = data.readByte();
			((TEEPasture)te).handlePacketData(mobActions);
		}
	}

	public static Packet250CustomPayload getPacket(TileEntityMachine tem) {
		Object[] data = tem.getSyncDataToServer();
		if(data != null)
			return PacketHandler.getPacket(PacketHandler.TE_CLIENT_TO_SERVER, tem.xCoord, (short)tem.yCoord, tem.zCoord, data);
		return null;
	}
}
