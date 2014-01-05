package infinitealloys.network;

import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import com.google.common.io.ByteArrayDataInput;

public class PacketTEClientToServer implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		final int x = data.readInt();
		final short y = data.readShort();
		final int z = data.readInt();
		final TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
		if(te instanceof TEEMetalForge) {
			final byte[] recipeAmts = new byte[Consts.METAL_COUNT];
			for(int i = 0; i < recipeAmts.length; i++)
				recipeAmts[i] = data.readByte();
			((TEEMetalForge)te).handlePacketDataFromClient(recipeAmts);
		}
		else if(te instanceof TEEPasture) {
			final byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
			for(int i = 0; i < mobActions.length; i++)
				mobActions[i] = data.readByte();
			((TEEPasture)te).handlePacketData(mobActions);
		}
	}

	public static Packet250CustomPayload getPacket(TileEntityMachine tem) {
		final Object[] data = tem.getSyncDataToServer();
		if(data != null)
			return PacketHandler.getPacket(PacketHandler.TE_CLIENT_TO_SERVER, tem.xCoord, (short)tem.yCoord, tem.zCoord, data);
		return null;
	}
}
