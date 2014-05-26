package infinitealloys.network;

import infinitealloys.item.ItemInternetWand;
import infinitealloys.tile.IHost;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

public class PacketWand implements IPacketIA {

	private boolean adding;
	private Point machine;
	private byte index;

	public PacketWand() {}

	/** Adding */
	public PacketWand(int x, int y, int z) {
		adding = true;
		machine = new Point(x, y, z);
	}

	/** Removing */
	public PacketWand(byte index) {
		adding = false;
		this.index = index;
	}

	@Override
	public void readBytes(ByteBuf bytes) {
		adding = bytes.readBoolean();

		if(adding)
			machine = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
		else
			index = bytes.readByte();
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		ChannelHandler.writeObject(bytes, adding);
		if(adding)
			ChannelHandler.writeObject(bytes, machine);
		else
			ChannelHandler.writeObject(bytes, index);
	}

	@Override
	public void executeServer(EntityPlayer player) {
		ItemStack heldItem = player.getHeldItem();
		if(heldItem.getItem() instanceof ItemInternetWand) {
			if(adding)
				((ItemInternetWand)heldItem.getItem()).addMachine(player.worldObj, heldItem, machine.x, machine.y, machine.z);
			else
				((ItemInternetWand)heldItem.getItem()).removeMachine(heldItem, index);
		}
	}

	@Override
	public void executeClient(EntityPlayer player) {}
}
