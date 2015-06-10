package infinitealloys.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import infinitealloys.tile.TileEntityMachine;
import io.netty.buffer.ByteBuf;

public final class MessageTEToClient
    implements IMessage, IMessageHandler<MessageTEToClient, IMessage> {

  private TileEntityMachine tem;
  private ByteBuf bytes;

  public MessageTEToClient() {
  }

  public MessageTEToClient(TileEntityMachine tem) {
    this.tem = tem;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    this.bytes = bytes;
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    tem.writeToClientData(bytes);
  }

  @Override
  public IMessage onMessage(MessageTEToClient message, MessageContext context) {
    TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(
        message.bytes.readInt(), message.bytes.readInt(), message.bytes.readInt());
    if (te instanceof TileEntityMachine) {
      ((TileEntityMachine) te).readToClientData(message.bytes);
    }
    return null;
  }
}
