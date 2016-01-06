package infinitealloys.network;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import infinitealloys.item.IAItems;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.util.Point3;
import io.netty.buffer.ByteBuf;

public final class MessageWand implements IMessage, IMessageHandler<MessageWand, IMessage> {

  private boolean adding;
  private Point3 machine;
  private byte index;

  public MessageWand() {
  }

  /**
   * Adding
   */
  public MessageWand(Point3 machine) {
    adding = true;
    this.machine = machine;
  }

  /**
   * Removing
   */
  public MessageWand(byte index) {
    adding = false;
    this.index = index;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    adding = bytes.readBoolean();

    if (adding) {
      machine = Point3.readFromByteBuf(bytes);
    } else {
      index = bytes.readByte();
    }
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    bytes.writeBoolean(adding);
    if (adding) {
      machine.writeToByteBuf(bytes);
    } else {
      bytes.writeByte(index);
    }
  }

  @Override
  public IMessage onMessage(MessageWand message, MessageContext context) {
    adding = message.adding;
    machine = message.machine;
    index = message.index;

    ItemStack heldItem = context.getServerHandler().playerEntity.getHeldItem();
    if (heldItem.getItem() == IAItems.internetWand) {
      if (adding) {
        ((ItemInternetWand) heldItem.getItem())
            .addMachine(context.getServerHandler().playerEntity.worldObj, heldItem, machine.x,
                        machine.y, machine.z);
      } else {
        ((ItemInternetWand) heldItem.getItem()).removeMachine(heldItem, index);
      }
    }

    return null;
  }
}
