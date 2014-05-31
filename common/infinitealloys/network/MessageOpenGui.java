package infinitealloys.network;

import infinitealloys.block.BlockMachine;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Point;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenGui implements IMessage, IMessageHandler<MessageOpenGui, IMessage> {

	private Point machine;
	private boolean fromComputer;

	public MessageOpenGui() {}

	public MessageOpenGui(Point machine, boolean fromComputer) {
		this.machine = machine;
		this.fromComputer = fromComputer;
	}

	@Override
	public void fromBytes(ByteBuf bytes) {
		machine = new Point(bytes.readInt(), bytes.readInt(), bytes.readInt());
		fromComputer = bytes.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf bytes) {
		NetworkHandler.writeObject(bytes, machine);
		NetworkHandler.writeObject(bytes, fromComputer);
	}

	@Override
	public IMessage onMessage(MessageOpenGui message, MessageContext context) {
		machine = message.machine;
		fromComputer = message.fromComputer;

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		((BlockMachine)player.worldObj.getBlock(machine.x, machine.y, machine.z)).openGui(player.worldObj, player, (TileEntityMachine)player.worldObj.getTileEntity(machine.x, machine.y, machine.z), fromComputer);

		return null;
	}
}
