package infinitealloys.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import infinitealloys.block.BlockMachine;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;

public class PacketOpenGui implements IPacketIA {

	private Point machine;
	private boolean fromComputer;

	public PacketOpenGui() {}

	public PacketOpenGui(Point machine, boolean fromComputer) {
		this.machine = machine;
		this.fromComputer = fromComputer;
	}

	@Override
	public void readBytes(ByteBuf bytes) {
		machine = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
		fromComputer = bytes.readBoolean();
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		ChannelHandler.writeObject(bytes, machine);
		ChannelHandler.writeObject(bytes, fromComputer);
	}

	@Override
	public void executeServer(EntityPlayer player) {
		((BlockMachine)player.worldObj.getBlock(machine.x, machine.y, machine.z)).openGui(player.worldObj, player, (TileEntityMachine)player.worldObj.getTileEntity(machine.x, machine.y, machine.z), fromComputer);
	}

	@Override
	public void executeClient(EntityPlayer player) {}
}
