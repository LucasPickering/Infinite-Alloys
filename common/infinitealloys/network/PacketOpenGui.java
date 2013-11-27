package infinitealloys.network;

import infinitealloys.block.BlockMachine;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.util.Funcs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import com.google.common.io.ByteArrayDataInput;

public class PacketOpenGui {

	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		World world = player.worldObj;
		int x = data.readInt();
		int y = data.readShort();
		int z = data.readInt();
		boolean fromComputer = data.readBoolean();
		((BlockMachine)Funcs.getBlock(world, x, y, z)).openGui(world, player, (TileEntityElectric)world.getBlockTileEntity(x, y, z), fromComputer);
	}

	public static Packet250CustomPayload getPacket(int x, short y, int z, boolean fromComputer) {
		return PacketHandler.getPacket(PacketHandler.OPEN_GUI, x, y, z, fromComputer);
	}
}
