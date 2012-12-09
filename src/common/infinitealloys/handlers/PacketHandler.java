package infinitealloys.handlers;

import infinitealloys.IAWorldData;
import infinitealloys.InfiniteAlloys;
import infinitealloys.Point;
import infinitealloys.References;
import infinitealloys.block.BlockMachine;
import infinitealloys.tile.TileEntityComputer;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.tile.TileEntityMetalForge;
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

	private static final int WORLD_DATA = 0;
	private static final int TE_SERVER_TO_CLIENT = 1;
	private static final int TE_CLIENT_TO_SERVER = 2;
	private static final int TE_JOULES = 3;
	private static final int COMPUTER_ADD_MACHINE = 4;
	private static final int OPEN_GUI = 5;

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
		int packetIndex = data.readInt();
		World world = ((EntityPlayer)player).worldObj;
		switch(packetIndex) {
			case WORLD_DATA:
				int[] validAlloys = new int[References.validAlloyCount];
				for(int i = 0; i < validAlloys.length; i++)
					validAlloys[i] = data.readInt();
				InfiniteAlloys.instance.worldData = new IAWorldData(validAlloys);
				break;
			case TE_SERVER_TO_CLIENT:
				int x = data.readInt();
				int y = data.readInt();
				int z = data.readInt();
				TileEntity te = world.getBlockTileEntity(x, y, z);
				if(te instanceof TileEntityMachine) {
					int processProgress = data.readInt();
					byte orientation = data.readByte();
					int upgrades = data.readInt();
					double joules = data.readDouble();
					((TileEntityMachine)te).handlePacketDataFromServer(processProgress, orientation, upgrades, joules);
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
						byte[] recipeAmts = new byte[References.metalCount];
						for(int i = 0; i < recipeAmts.length; i++)
							recipeAmts[i] = data.readByte();
						((TileEntityMetalForge)te).handlePacketData(recipeAmts);
					}
				}
				break;
			case TE_CLIENT_TO_SERVER:
				x = data.readInt();
				y = data.readInt();
				z = data.readInt();
				te = world.getBlockTileEntity(x, y, z);
				if(te instanceof TileEntityMetalForge) {
					byte[] recipeAmts = new byte[References.metalCount];
					for(int i = 0; i < recipeAmts.length; i++)
						recipeAmts[i] = data.readByte();
					((TileEntityMetalForge)te).handlePacketData(recipeAmts);
				}
				break;
			case TE_JOULES:
				x = data.readInt();
				y = data.readInt();
				z = data.readInt();
				te = world.getBlockTileEntity(x, y, z);
				if(te instanceof TileEntityMachine) {
					double joules = data.readDouble();
					((TileEntityMachine)te).joules = joules;
				}
				break;
			case COMPUTER_ADD_MACHINE:
				x = data.readInt();
				y = data.readInt();
				z = data.readInt();
				te = world.getBlockTileEntity(x, y, z);
				if(te instanceof TileEntityComputer) {
					int machX = data.readInt();
					int machY = data.readInt();
					int machZ = data.readInt();
					((TileEntityComputer)te).addMachine((EntityPlayer)player, machX, machY, machZ);
				}
				break;
			case OPEN_GUI:
				x = data.readInt();
				y = data.readInt();
				z = data.readInt();
				((BlockMachine)Block.blocksList[world.getBlockId(x, y, z)]).openGui(world, (EntityPlayer)player, (TileEntityMachine)world.getBlockTileEntity(x, y, z), true);
				break;
		}
	}

	public static Packet getWorldDataPacket() {
		return getPacket(WORLD_DATA, InfiniteAlloys.instance.worldData.getValidAlloys());
	}

	public static Packet getTEPacketToClient(TileEntityMachine tem) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(TE_SERVER_TO_CLIENT);
			dos.writeInt(tem.xCoord);
			dos.writeInt(tem.yCoord);
			dos.writeInt(tem.zCoord);
			dos.writeInt(tem.processProgress);
			dos.writeByte(tem.front.ordinal());
			dos.writeInt(tem.getUpgrades());
			dos.writeDouble(tem.joules);
			if(tem instanceof TileEntityComputer) {
				TileEntityComputer tec = (TileEntityComputer)tem;
				dos.writeInt(tec.networkCoords.size());
				for(Point coords : tec.networkCoords) {
					dos.writeInt(coords.x);
					dos.writeInt(coords.y);
					dos.writeInt(coords.z);
				}
			}
			else if(tem instanceof TileEntityMetalForge) {
				TileEntityMetalForge temf = (TileEntityMetalForge)tem;
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

	public static Packet getTEPacketToServer(TileEntityMachine tem) {
		if(!(tem instanceof TileEntityMetalForge))
			return null;
		return getPacket(TE_CLIENT_TO_SERVER, tem.xCoord, tem.yCoord, tem.zCoord, ((TileEntityMetalForge)tem).recipeAmts);
	}

	public static Packet getTEJoulesPacket(TileEntityMachine tem) {
		return getPacket(TE_JOULES, tem.xCoord, tem.yCoord, tem.zCoord, tem.joules);
	}

	public static Packet getComputerPacketAddMachine(int compX, int compY, int compZ, int machX, int machY, int machZ) {
		return getPacket(COMPUTER_ADD_MACHINE, compX, compY, compZ, machX, machY, machZ);
	}

	public static Packet getPacketOpenGui(int x, int y, int z) {
		return getPacket(OPEN_GUI, x, y, z);
	}

	private static Packet getPacket(int id, Object... data) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(id);
			for(Object datum : data) {
				if(datum instanceof Byte)
					dos.writeByte((Byte)datum);
				else if(datum instanceof byte[])
					for(byte datum2 : (byte[])datum)
						dos.writeByte(datum2);
				else if(datum instanceof Short)
					dos.writeShort((Short)datum);
				else if(datum instanceof Integer)
					dos.writeInt((Integer)datum);
				else if(datum instanceof int[])
					for(int datum2 : (int[])datum)
						dos.writeInt(datum2);
				else if(datum instanceof Double)
					dos.writeDouble((Double)datum);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}
}
