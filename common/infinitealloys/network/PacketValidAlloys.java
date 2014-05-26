package infinitealloys.network;

import infinitealloys.block.BlockMachine;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import com.google.common.io.ByteArrayDataInput;

public class PacketValidAlloys implements IPacketIA {

	private int[] validAlloys = new int[Consts.VALID_ALLOY_COUNT];

	public PacketValidAlloys() {}

	public PacketValidAlloys(int[] validAlloys) {
		this.validAlloys = validAlloys;
	}

	@Override
	public void readBytes(ByteBuf bytes) {
		for(int i = 0; i < validAlloys.length; i++)
			validAlloys[i] = bytes.readInt();
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		ChannelHandler.writeObject(bytes, validAlloys);
	}

	@Override
	public void executeServer(EntityPlayer player) {}

	@Override
	public void executeClient(EntityPlayer player) {
		InfiniteAlloys.instance.setValidAlloys(validAlloys);
	}
}
