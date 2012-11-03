package infinitealloys.handlers;

import infinitealloys.References;
import infinitealloys.TileEntityAnalyzer;
import infinitealloys.TileEntityMachine;
import infinitealloys.TileEntityComputer;
import infinitealloys.TileEntityMetalForge;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
		// 0: TE server -> client
		// 1: Computer add machine
		// 2: Computer open GUI
		int packetIndex = data.readInt();
		World world = ((EntityPlayer)player).worldObj;
		int x = data.readInt();
		int y = data.readInt();
		int z = data.readInt();
		switch(packetIndex) {
			case 0:
				TileEntity te = world.getBlockTileEntity(x, y, z);
				if(te instanceof TileEntityMachine) {
					int upgrades = data.readInt();
					byte orientation = data.readByte();
					((TileEntityMachine)te).handlePacketDataFromServer(orientation, upgrades);
					if(te instanceof TileEntityComputer) {
						TileEntityComputer tec = (TileEntityComputer)te;
						tec.networkCoords.clear();
						int networkSize = data.readInt();
						for(int i = 0; i < networkSize; i++) {
							int machX = data.readInt();
							int machY = data.readInt();
							int machZ = data.readInt();
							tec.networkCoords.add(Vec3.createVectorHelper(machX, machY, machZ));
						}
					}
					if(te instanceof TileEntityMetalForge) {
						int currentFuelBurnTime = data.readShort();
						int heatLeft = data.readShort();
						int smeltProgress = data.readShort();
						byte[] recipeAmts = new byte[References.metalCount];
						for(int i = 0; i < recipeAmts.length; i++)
							recipeAmts[i] = data.readByte();
						int numUsingPlayers = data.readShort();
						((TileEntityMetalForge)te).handlePacketDataFromServer(currentFuelBurnTime, heatLeft, smeltProgress, recipeAmts, numUsingPlayers);
					}
					if(te instanceof TileEntityAnalyzer) {
						int analysisProgress = data.readShort();
						int ticksSinceStart = data.readShort();
						int ticksSinceFinish = data.readShort();
						((TileEntityAnalyzer)te).handlePacketDataFromServer(analysisProgress, ticksSinceStart, ticksSinceFinish);
					}
				}
				break;
			case 1:
				TileEntity te1 = world.getBlockTileEntity(x, y, z);
				if(te1 instanceof TileEntityComputer) {
					int machX = data.readInt();
					int machY = data.readInt();
					int machZ = data.readInt();
					((TileEntityComputer)te1).addMachine((EntityPlayer)player, machX, machY, machZ);
				}
				break;
			case 2:
				Block.blocksList[world.getBlockId(x, y, z)].onBlockActivated(world, x, y, z, (EntityPlayer)player, 0, 0, 0, 0);
				break;
		}
	}

	public static Packet getTEPacketToClient(TileEntityMachine tem) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(0);
			dos.writeInt(tem.xCoord);
			dos.writeInt(tem.yCoord);
			dos.writeInt(tem.zCoord);
			dos.writeInt(tem.upgrades);
			dos.writeByte(tem.orientation);
			if(tem instanceof TileEntityComputer) {
				TileEntityComputer tec = (TileEntityComputer)tem;
				dos.writeInt(tec.networkCoords.size());
				for(Vec3 vec : tec.networkCoords) {
					dos.writeInt((int)vec.xCoord);
					dos.writeInt((int)vec.yCoord);
					dos.writeInt((int)vec.zCoord);
				}
			}
			else if(tem instanceof TileEntityMetalForge) {
				TileEntityMetalForge temf = (TileEntityMetalForge)tem;
				dos.writeShort(temf.currentFuelBurnTime);
				dos.writeShort(temf.heatLeft);
				dos.writeShort(temf.smeltProgress);
				for(byte amt : temf.recipeAmts)
					dos.writeByte(amt);
				dos.writeShort(temf.numUsingPlayers);
			}
			else if(tem instanceof TileEntityAnalyzer) {
				TileEntityAnalyzer tea = (TileEntityAnalyzer)tem;
				dos.writeShort(tea.analysisProgress);
				dos.writeShort(tea.ticksSinceStart);
				dos.writeShort(tea.ticksSinceFinish);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}

	public static Packet getComputerPacketAddMachine(int compX, int compY, int compZ, int machX, int machY, int machZ) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(1);
			dos.writeInt(compX);
			dos.writeInt(compY);
			dos.writeInt(compZ);
			dos.writeInt(machX);
			dos.writeInt(machY);
			dos.writeInt(machZ);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}

	public static Packet getComputerPacketOpenGui(int x, int y, int z) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(2);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(z);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}
}
