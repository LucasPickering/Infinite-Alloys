package infinitealloys.network;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import infinitealloys.block.BlockMachine;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Point3;
import io.netty.buffer.ByteBuf;

public final class MessageOpenGui implements IMessage, IMessageHandler<MessageOpenGui, IMessage> {

  private Point3 machine;

  public MessageOpenGui() {
  }

  public MessageOpenGui(Point3 machine) {
    this.machine = machine;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    machine = Point3.readFromByteBuf(bytes);
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    machine.writeToByteBuf(bytes);
  }

  @Override
  public IMessage onMessage(MessageOpenGui message, MessageContext context) {
    machine = message.machine;

    EntityPlayer player = context.getServerHandler().playerEntity;
    ((BlockMachine) player.worldObj.getBlock(machine.x, machine.y, machine.z))
        .openGui(player.worldObj, player, (TileEntityMachine) player.worldObj
            .getTileEntity(machine.x, machine.y, machine.z));

    return null;
  }
}
