package infinitealloys.handlers;

import infinitealloys.block.BlockMachine;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.WorldData;
import infinitealloys.tile.TEUComputer;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.tile.TEMMetalForge;
import infinitealloys.tile.TEMPasture;
import infinitealloys.tile.TileEntityUpgradable;
import infinitealloys.tile.TEMXray;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	private static final byte WORLD_DATA = 0;
	private static final byte TE_SERVER_TO_CLIENT = 1;
	private static final byte TE_CLIENT_TO_SERVER = 2;
	private static final byte OPEN_GUI = 3;
	private static final byte XRAY_SEARCH = 4;

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
		int packetIndex = data.readByte();
		World world = ((EntityPlayer)player).worldObj;
		switch(packetIndex) {
			case WORLD_DATA:
				int[] validAlloys = new int[Consts.VALID_ALLOY_COUNT];
				for(int i = 0; i < validAlloys.length; i++)
					validAlloys[i] = data.readInt();
				InfiniteAlloys.instance.worldData = new WorldData(validAlloys);
				break;
			case TE_SERVER_TO_CLIENT:
				int x = data.readInt();
				int y = data.readInt();
				int z = data.readInt();
				TileEntity te = world.getBlockTileEntity(x, y, z);
				if(te instanceof TileEntityUpgradable) {
					byte orientation = data.readByte();
					int upgrades = data.readInt();
					((TileEntityUpgradable)te).handlePacketDataFromServer(orientation, upgrades);
					if(te instanceof TEUComputer) {
						TEUComputer tec = (TEUComputer)te;
						tec.connectedTEUs.clear();
						int networkSize = data.readInt();
						for(int i = 0; i < networkSize; i++) {
							int machX = data.readInt();
							int machY = data.readInt();
							int machZ = data.readInt();
							tec.connectedTEUs.add(new Point(machX, machY, machZ));
						}
					}
					else if(te instanceof TileEntityMachine) {
						int processProgress = data.readInt();
						((TileEntityMachine)te).handlePacketDataFromServer(processProgress);
						if(te instanceof TEMMetalForge) {
							byte[] recipeAmts = new byte[Consts.METAL_COUNT];
							for(int i = 0; i < recipeAmts.length; i++)
								recipeAmts[i] = data.readByte();
							((TEMMetalForge)te).handlePacketDataFromClient(recipeAmts);
						}
						else if(te instanceof TEMXray) {
							TEMXray tex = (TEMXray)te;
							tex.clearDetectedBlocks();
							short size = data.readShort();
							for(int i = 0; i < size; i++)
								tex.addDetectedBlock(new Point(data.readInt(), data.readShort(), data.readInt()));
						}
						else if(te instanceof TEMPasture) {
							byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
							for(int i = 0; i < mobActions.length; i++)
								mobActions[i] = data.readByte();
							((TEMPasture)te).handlePacketData(mobActions);
						}
					}
				}
				break;
			case TE_CLIENT_TO_SERVER:
				x = data.readInt();
				y = data.readInt();
				z = data.readInt();
				te = world.getBlockTileEntity(x, y, z);
				if(te instanceof TEUComputer) {
					boolean autoSearch = data.readBoolean();
					((TEUComputer)te).handlePacketDataFromClient(autoSearch);
				}
				if(te instanceof TEMMetalForge) {
					byte[] recipeAmts = new byte[Consts.METAL_COUNT];
					for(int i = 0; i < recipeAmts.length; i++)
						recipeAmts[i] = data.readByte();
					((TEMMetalForge)te).handlePacketDataFromClient(recipeAmts);
				}
				else if(te instanceof TEMXray) {
					boolean searching = data.readBoolean();
					((TEMXray)te).handlePacketDataFromClient(searching);
				}
				else if(te instanceof TEMPasture) {
					byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
					for(int i = 0; i < mobActions.length; i++)
						mobActions[i] = data.readByte();
					((TEMPasture)te).handlePacketData(mobActions);
				}
				break;
			case OPEN_GUI:
				x = data.readInt();
				y = data.readInt();
				z = data.readInt();
				boolean fromComputer = data.readBoolean();
				((BlockMachine)Funcs.getBlock(world, x, y, z)).openGui(world, (EntityPlayer)player, (TileEntityMachine)world.getBlockTileEntity(x, y, z),
						fromComputer);
				break;
			case XRAY_SEARCH:
				x = data.readInt();
				y = data.readInt();
				z = data.readInt();
				((TEMXray)world.getBlockTileEntity(x, y, z)).shouldSearch = true;
				break;
		}
	}

	public static Packet getWorldDataPacket() {
		return getPacket(WORLD_DATA, InfiniteAlloys.instance.worldData.getValidAlloys());
	}

	public static Packet getTEPacketToClient(TileEntityUpgradable teu) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(TE_SERVER_TO_CLIENT);
			dos.writeInt(teu.xCoord);
			dos.writeInt(teu.yCoord);
			dos.writeInt(teu.zCoord);
			dos.writeByte(teu.front);
			dos.writeInt(teu.getUpgrades());
			if(teu instanceof TEUComputer) {
				TEUComputer tec = (TEUComputer)teu;
				dos.writeInt(tec.connectedTEUs.size());
				for(Point coords : tec.connectedTEUs) {
					dos.writeInt(coords.x);
					dos.writeInt(coords.y);
					dos.writeInt(coords.z);
				}
			}
			else if(teu instanceof TileEntityMachine) {
				TileEntityMachine tem = (TileEntityMachine)teu;
				dos.writeInt(tem.processProgress);
				if(tem instanceof TEMMetalForge)
					for(byte amt : ((TEMMetalForge)tem).recipeAmts)
						dos.writeByte(amt);
				else if(tem instanceof TEMXray) {
					dos.writeShort(((TEMXray)tem).getDetectedBlocks().size());
					for(Point p : ((TEMXray)tem).getDetectedBlocks()) {
						dos.writeInt(p.x);
						dos.writeShort((short)p.y);
						dos.writeInt(p.z);
					}
				}
				else if(tem instanceof TEMPasture)
					for(byte mob : ((TEMPasture)tem).mobActions)
						dos.writeByte(mob);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}

	public static Packet getTEPacketToServer(TileEntityUpgradable teu) {
		if(teu instanceof TEUComputer)
			return getPacket(TE_CLIENT_TO_SERVER, teu.xCoord, teu.yCoord, teu.zCoord, ((TEUComputer)teu).autoSearch);
		if(teu instanceof TEMMetalForge)
			return getPacket(TE_CLIENT_TO_SERVER, teu.xCoord, teu.yCoord, teu.zCoord, ((TEMMetalForge)teu).recipeAmts);
		if(teu instanceof TEMXray)
			return getPacket(TE_CLIENT_TO_SERVER, teu.xCoord, teu.yCoord, teu.zCoord);
		if(teu instanceof TEMPasture)
			return getPacket(TE_CLIENT_TO_SERVER, teu.xCoord, teu.yCoord, teu.zCoord, ((TEMPasture)teu).mobActions);
		return null;
	}

	public static Packet getPacketOpenGui(int x, int y, int z, boolean fromComputer) {
		return getPacket(OPEN_GUI, x, y, z, fromComputer);
	}

	public static Packet getPacketSearch(int x, int y, int z) {
		return getPacket(XRAY_SEARCH, x, y, z);
	}

	private static Packet getPacket(int id, Object... data) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(id);
			for(Object datum : data) {
				if(datum instanceof Byte)
					dos.writeByte((Byte)datum);

				else if(datum instanceof byte[])
					for(byte datum2 : (byte[])datum)
						dos.writeByte(datum2);

				else if(datum instanceof Short)
					dos.writeShort((Short)datum);

				else if(datum instanceof short[])
					for(short datum2 : (short[])datum)
						dos.writeShort(datum2);

				else if(datum instanceof Integer)
					dos.writeInt((Integer)datum);

				else if(datum instanceof int[])
					for(int datum2 : (int[])datum)
						dos.writeInt(datum2);

				else if(datum instanceof Double)
					dos.writeDouble((Double)datum);

				else if(datum instanceof Boolean)
					dos.writeBoolean((Boolean)datum);
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
