package infinitealloys.handlers;

import infinitealloys.Point;
import infinitealloys.References;
import infinitealloys.BlockMachine;
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
import net.minecraft.src.World;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	private static final int TE_SERVER_TO_CLIENT = 0;
	private static final int TE_CONTROLLER = 1;
	private static final int COMPUTER_ADD_MACHINE = 2;
	private static final int OPEN_GUI = 3;

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
		int packetIndex = data.readInt();
		World world = ((EntityPlayer)player).worldObj;
		int x = data.readInt();
		int y = data.readInt();
		int z = data.readInt();
		switch(packetIndex) {
			case TE_SERVER_TO_CLIENT:
				TileEntity te = world.getBlockTileEntity(x, y, z);
				if(te instanceof TileEntityMachine) {
					int upgrades = data.readInt();
					byte orientation = data.readByte();
					double joules = data.readDouble();
					((TileEntityMachine)te).handlePacketDataFromServer(orientation, upgrades, joules);
					if(te instanceof TileEntityComputer) {
						TileEntityComputer tec = (TileEntityComputer)te;
						tec.networkCoords.clear();
						int networkSize = data.readInt();
						for(int i = 0; i < networkSize; i++) {
							int machX = data.readInt();
							int machY = data.readInt();
							int machZ = data.readInt();
							tec.networkCoords.add(new Point(machX, machY, machZ));
						}
					}
					if(te instanceof TileEntityMetalForge) {
						int smeltProgress = data.readShort();
						byte[] recipeAmts = new byte[References.metalCount];
						for(int i = 0; i < recipeAmts.length; i++)
							recipeAmts[i] = data.readByte();
						((TileEntityMetalForge)te).handlePacketDataFromServer(smeltProgress, recipeAmts);
					}
					if(te instanceof TileEntityAnalyzer) {
						int analysisProgress = data.readShort();
						int ticksSinceStart = data.readShort();
						int ticksSinceFinish = data.readShort();
						((TileEntityAnalyzer)te).handlePacketDataFromServer(analysisProgress);
					}
				}
				break;
			case TE_CONTROLLER:
				if(y >= 0)
					TileEntityMachine.controller = new Point(x, y, z);
				break;
			case COMPUTER_ADD_MACHINE:
				TileEntity te1 = world.getBlockTileEntity(x, y, z);
				if(te1 instanceof TileEntityComputer) {
					int machX = data.readInt();
					int machY = data.readInt();
					int machZ = data.readInt();
					((TileEntityComputer)te1).addMachine((EntityPlayer)player, machX, machY, machZ);
				}
				break;
			case OPEN_GUI:
				((BlockMachine)Block.blocksList[world.getBlockId(x, y, z)]).openGui(world, (EntityPlayer)player, (TileEntityMachine)world.getBlockTileEntity(x, y, z), true);
				break;
		}
	}

	public static Packet getTEPacketToClient(TileEntityMachine tem) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(TE_SERVER_TO_CLIENT);
			dos.writeInt(tem.xCoord);
			dos.writeInt(tem.yCoord);
			dos.writeInt(tem.zCoord);
			dos.writeInt(tem.getUpgrades());
			dos.writeByte(tem.front.ordinal());
			dos.writeDouble(tem.joules);
			if(tem instanceof TileEntityComputer) {
				TileEntityComputer tec = (TileEntityComputer)tem;
				dos.writeInt(tec.networkCoords.size());
				for(Point coords : tec.networkCoords) {
					dos.writeInt((int)coords.x);
					dos.writeInt((int)coords.y);
					dos.writeInt((int)coords.z);
				}
			}
			else if(tem instanceof TileEntityMetalForge) {
				TileEntityMetalForge temf = (TileEntityMetalForge)tem;
				dos.writeShort(temf.smeltProgress);
				for(byte amt : temf.recipeAmts)
					dos.writeByte(amt);
			}
			else if(tem instanceof TileEntityAnalyzer) {
				TileEntityAnalyzer tea = (TileEntityAnalyzer)tem;
				dos.writeShort(tea.analysisProgress);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}

	public static Packet getTEControllerPacket(EntityPlayer player) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(TE_CONTROLLER);
			Point controller = TileEntityMachine.controllers.get(player.username);
			if(controller != null) {
				dos.writeInt(controller.x);
				dos.writeInt(controller.y);
				dos.writeInt(controller.z);
			}
			else {
				dos.writeInt(0);
				dos.writeInt(-1);
				dos.writeInt(0);
			}
		}catch(IOException e) {
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
			dos.writeInt(COMPUTER_ADD_MACHINE);
			dos.writeInt(compX);
			dos.writeInt(compY);
			dos.writeInt(compZ);
			dos.writeInt(machX);
			dos.writeInt(machY);
			dos.writeInt(machZ);
		}catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}

	public static Packet getPacketOpenGui(int x, int y, int z) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(OPEN_GUI);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(z);
		}catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}
}
