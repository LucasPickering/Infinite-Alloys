package infinitealloys.network;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point3;
import io.netty.buffer.ByteBuf;

public class MessageTEToServer implements IMessage, IMessageHandler<MessageTEToServer, IMessage> {

  private Point3 tePoint;
  private Object[] data;
  private ByteBuf bytes;

  public MessageTEToServer() {
  }

  public MessageTEToServer(TileEntityMachine tem) {
    tePoint = tem.coords();
    data = tem.getSyncDataToServer();
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    tePoint = new Point3(bytes.readInt(), bytes.readInt(), bytes.readInt());
    this.bytes = bytes;
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    NetworkHandler.writeObject(bytes, tePoint);
    NetworkHandler.writeObject(bytes, data);
  }

  @Override
  public IMessage onMessage(MessageTEToServer message, MessageContext context) {
    TileEntity te = Funcs.getTileEntity(context.getServerHandler().playerEntity.worldObj,
                                        message.tePoint);

    if (te instanceof TileEntityMachine) {
      ((TileEntityMachine) te).readClientToServerData(message.bytes);
    }

    return null;
  }
}
