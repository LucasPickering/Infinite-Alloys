package infinitealloys.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Consts;
import io.netty.buffer.ByteBuf;

public class MessageValidAlloys implements IMessage, IMessageHandler<MessageValidAlloys, IMessage> {

  private int[] validAlloys = new int[Consts.VALID_ALLOY_COUNT];

  public MessageValidAlloys() {
  }

  public MessageValidAlloys(int[] validAlloys) {
    this.validAlloys = validAlloys;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    for (int i = 0; i < validAlloys.length; i++) {
      validAlloys[i] = bytes.readInt();
    }
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    NetworkHandler.writeObject(bytes, validAlloys);
  }

  @Override
  public IMessage onMessage(MessageValidAlloys message, MessageContext context) {
    InfiniteAlloys.instance.setValidAlloys(message.validAlloys);
    return null;
  }
}
