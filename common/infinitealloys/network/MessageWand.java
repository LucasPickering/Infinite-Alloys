package infinitealloys.network;

import infinitealloys.item.ItemInternetWand;
import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageWand implements IMessage, IMessageHandler<MessageWand, IMessage> {

	private boolean adding;
	private Point machine;
	private byte index;

	public MessageWand() {}
	
	/** Adding */
	public MessageWand(int x, int y, int z) {
		adding = true;
		machine = new Point(x, y, z);
	}

	/** Removing */
	public MessageWand(byte index) {
		adding = false;
		this.index = index;
	}

	@Override
	public void fromBytes(ByteBuf bytes) {
		adding = bytes.readBoolean();

		if(adding)
			machine = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
		else
			index = bytes.readByte();
	}

	@Override
	public void toBytes(ByteBuf bytes) {
		NetworkHandler.writeObject(bytes, adding);
		if(adding)
			NetworkHandler.writeObject(bytes, machine);
		else
			NetworkHandler.writeObject(bytes, index);
	}

	@Override
	public IMessage onMessage(MessageWand message, MessageContext context) {
		ItemStack heldItem = context.getServerHandler().playerEntity.getHeldItem();
		if(heldItem.getItem() instanceof ItemInternetWand) {
			if(adding)
				((ItemInternetWand)heldItem.getItem()).addMachine(context.getServerHandler().playerEntity.worldObj, heldItem, machine.x, machine.y, machine.z);
			else
				((ItemInternetWand)heldItem.getItem()).removeMachine(heldItem, index);
		}

		return null;
	}
}
