package infinitealloys.network;

import infinitealloys.block.BlockMachine;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Funcs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import com.google.common.io.ByteArrayDataInput;

public class PacketOpenGui implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		final World world = player.worldObj;
		final int x = data.readInt();
		final int y = data.readShort();
		final int z = data.readInt();
		final boolean fromComputer = data.readBoolean();
		((BlockMachine)Funcs.getBlock(world, x, y, z)).openGui(world, player, (TileEntityMachine)world.getBlockTileEntity(x, y, z), fromComputer);
	}

	public static Packet250CustomPayload getPacket(int x, short y, int z, boolean fromComputer) {
		return PacketHandler.getPacket(PacketHandler.OPEN_GUI, x, y, z, fromComputer);
	}
}
