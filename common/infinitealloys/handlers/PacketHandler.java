package infinitealloys.handlers;

import infinitealloys.block.BlockMachine;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.WorldData;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TEMEnergyStorage;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
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
				if(te instanceof TileEntityMachine) {
					byte orientation = data.readByte();
					int upgrades = data.readInt();
					((TileEntityMachine)te).handlePacketDataFromServer(orientation, upgrades);
					if(te instanceof TEMComputer) {
						TEMComputer tec = (TEMComputer)te;
						tec.connectedMachines.clear();
						int networkSize = data.readInt();
						for(int i = 0; i < networkSize; i++) {
							int machX = data.readInt();
							int machY = data.readInt();
							int machZ = data.readInt();
							tec.connectedMachines.add(new Point(machX, machY, machZ));
						}
					}
					else if(te instanceof TEMEnergyStorage) {
						int currentRK = data.readInt();
						((TEMEnergyStorage)te).handlePacketDataFromServer(currentRK);
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
						else if(te instanceof TEEXray) {
							TEEXray tex = (TEEXray)te;
							tex.clearDetectedBlocks();
							short size = data.readShort();
							for(int i = 0; i < size; i++)
								tex.addDetectedBlock(new Point(data.readInt(), data.readShort(), data.readInt()));
						}
						else if(te instanceof TEEPasture) {
							byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
							for(int i = 0; i < mobActions.length; i++)
								mobActions[i] = data.readByte();
							((TEEPasture)te).handlePacketData(mobActions);
						}
					}
				}
				break;
			case TE_CLIENT_TO_SERVER:
				x = data.readInt();
				y = data.readInt();
				z = data.readInt();
				te = world.getBlockTileEntity(x, y, z);
				if(te instanceof TEMComputer) {
					boolean autoSearch = data.readBoolean();
					((TEMComputer)te).handlePacketDataFromClient(autoSearch);
				}
				if(te instanceof TEEMetalForge) {
					byte[] recipeAmts = new byte[Consts.METAL_COUNT];
					for(int i = 0; i < recipeAmts.length; i++)
						recipeAmts[i] = data.readByte();
					((TEEMetalForge)te).handlePacketDataFromClient(recipeAmts);
				}
				else if(te instanceof TEEXray) {
					boolean searching = data.readBoolean();
					((TEEXray)te).handlePacketDataFromClient(searching);
				}
				else if(te instanceof TEEPasture) {
					byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
					for(int i = 0; i < mobActions.length; i++)
						mobActions[i] = data.readByte();
					((TEEPasture)te).handlePacketData(mobActions);
				}
				break;
			case OPEN_GUI:
				x = data.readInt();
				y = data.readInt();
				z = data.readInt();
				boolean fromComputer = data.readBoolean();
				((BlockMachine)Funcs.getBlock(world, x, y, z)).openGui(world, (EntityPlayer)player, (TileEntityElectric)world.getBlockTileEntity(x, y, z),
						fromComputer);
				break;
			case XRAY_SEARCH:
				x = data.readInt();
				y = data.readInt();
				z = data.readInt();
				((TEEXray)world.getBlockTileEntity(x, y, z)).shouldSearch = true;
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
			dos.writeByte(TE_SERVER_TO_CLIENT);
			dos.writeInt(tem.xCoord);
			dos.writeInt(tem.yCoord);
			dos.writeInt(tem.zCoord);
			dos.writeByte(tem.front);
			dos.writeInt(tem.getUpgrades());
			if(tem instanceof TEMComputer) {
				TEMComputer tec = (TEMComputer)tem;
				dos.writeInt(tec.connectedMachines.size());
				for(Point coords : tec.connectedMachines) {
					dos.writeInt(coords.x);
					dos.writeInt(coords.y);
					dos.writeInt(coords.z);
				}
			}
			if(tem instanceof TEMEnergyStorage)
				dos.writeInt(((TEMEnergyStorage)tem).getCurrentRK());
			else if(tem instanceof TileEntityElectric) {
				TileEntityElectric tee = (TileEntityElectric)tem;
				dos.writeInt(tee.processProgress);
				if(tee instanceof TEEMetalForge)
					for(byte amt : ((TEEMetalForge)tee).recipeAmts)
						dos.writeByte(amt);
				else if(tee instanceof TEEXray) {
					dos.writeShort(((TEEXray)tee).getDetectedBlocks().size());
					for(Point p : ((TEEXray)tee).getDetectedBlocks()) {
						dos.writeInt(p.x);
						dos.writeShort((short)p.y);
						dos.writeInt(p.z);
					}
				}
				else if(tee instanceof TEEPasture)
					for(byte mob : ((TEEPasture)tee).mobActions)
						dos.writeByte(mob);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}

	public static Packet getTEPacketToServer(TileEntityMachine tem) {
		if(tem instanceof TEMComputer)
			return getPacket(TE_CLIENT_TO_SERVER, tem.xCoord, tem.yCoord, tem.zCoord, ((TEMComputer)tem).autoSearch);
		if(tem instanceof TEEMetalForge)
			return getPacket(TE_CLIENT_TO_SERVER, tem.xCoord, tem.yCoord, tem.zCoord, ((TEEMetalForge)tem).recipeAmts);
		if(tem instanceof TEEXray)
			return getPacket(TE_CLIENT_TO_SERVER, tem.xCoord, tem.yCoord, tem.zCoord);
		if(tem instanceof TEEPasture)
			return getPacket(TE_CLIENT_TO_SERVER, tem.xCoord, tem.yCoord, tem.zCoord, ((TEEPasture)tem).mobActions);
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
		}catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("InfiniteAlloys", bos.toByteArray());
		packet.length = bos.size();
		return packet;
	}
}
