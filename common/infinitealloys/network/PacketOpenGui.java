package infinitealloys.network;

import infinitealloys.block.BlockMachine;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import com.google.common.io.ByteArrayDataInput;

public class PacketOpenGui implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		World world = player.worldObj;
		int x = data.readInt();
		int y = data.readInt();
		int z = data.readInt();
		boolean fromComputer = data.readBoolean();
		((BlockMachine)Funcs.getBlock(world, x, y, z)).openGui(world, player, (TileEntityMachine)world.getBlockTileEntity(x, y, z), fromComputer);
	}

	public static Packet250CustomPayload getPacket(Point machine, boolean fromComputer) {
		return PacketHandler.getPacket(PacketHandler.OPEN_GUI, machine, fromComputer);
	}
}
