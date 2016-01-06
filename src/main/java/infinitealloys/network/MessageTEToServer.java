package infinitealloys.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import infinitealloys.tile.TileEntityMachine;
import io.netty.buffer.ByteBuf;

public final class MessageTEToServer
    implements IMessage, IMessageHandler<MessageTEToServer, IMessage> {

  private TileEntityMachine tem;
  private ByteBuf bytes;

  public MessageTEToServer() {
  }

  public MessageTEToServer(TileEntityMachine tem) {
    this.tem = tem;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    this.bytes = bytes;
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    tem.writeToServerData(bytes);
  }

  @Override
  public IMessage onMessage(MessageTEToServer message, MessageContext context) {
    TileEntity te = context.getServerHandler().playerEntity.worldObj.getTileEntity(
        message.bytes.readInt(), message.bytes.readInt(), message.bytes.readInt());
    if (te instanceof TileEntityMachine) {
      ((TileEntityMachine) te).readToServerData(message.bytes);
    }
    return null;
  }
}
