package infinitealloys.network;

import infinitealloys.IAValues;
import infinitealloys.InfiniteAlloys;
import infinitealloys.TileEntityMachine;
import infinitealloys.TileEntityMetalForge;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
		boolean toClient = data.readBoolean();
		if(toClient) {
			int x = data.readInt();
			int y = data.readInt();
			int z = data.readInt();
			TileEntity te = ((EntityPlayer)player).worldObj.getBlockTileEntity(x, y, z);
			if(te instanceof TileEntityMachine) {
				byte networkID = data.readByte();
				byte orientation = data.readByte();
				((TileEntityMachine)te).handlePacketData(orientation, networkID);
				if(te instanceof TileEntityMetalForge) {
					int currentFuelBurnTime = data.readShort();
					int heatLeft = data.readShort();
					int smeltProgress = data.readShort();
					byte[] recipeAmts = new byte[IAValues.metalCount];
					for(int i = 0; i < recipeAmts.length; i++)
						recipeAmts[i] = data.readByte();
					((TileEntityMetalForge)te).handlePacketData(currentFuelBurnTime, heatLeft, smeltProgress, recipeAmts);
				}
			}
		}
		else {
			int x = data.readInt();
			int y = data.readInt();
			int z = data.readInt();
			TileEntity te = ((EntityPlayer)player).worldObj.getBlockTileEntity(x, y, z);
			if(te instanceof TileEntityMachine) {
				byte networkID = data.readByte();
				((TileEntityMachine)te).handlePacketData(networkID);
			}
		}
	}

	public static Packet getPacketToClient(TileEntityMachine tem) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeBoolean(true);
			dos.writeInt(tem.xCoord);
			dos.writeInt(tem.yCoord);
			dos.writeInt(tem.zCoord);
			dos.writeByte(tem.networkID);
			dos.writeByte(tem.orientation);
			if(tem instanceof TileEntityMetalForge) {
				TileEntityMetalForge temf = (TileEntityMetalForge)tem;
				dos.writeShort(temf.currentFuelBurnTime);
				dos.writeShort(temf.heatLeft);
				dos.writeShort(temf.smeltProgress);
				for(byte amt : temf.recipeAmts)
					dos.writeByte(amt);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}

	public static Packet getPacketToServer(TileEntityMachine tem) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeBoolean(false);
			dos.writeInt(tem.xCoord);
			dos.writeInt(tem.yCoord);
			dos.writeInt(tem.zCoord);
			dos.writeByte(tem.networkID);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}
}
