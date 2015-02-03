package infinitealloys.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import infinitealloys.tile.IHost;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point3;
import io.netty.buffer.ByteBuf;

/**
 * Add or remove a machine to or from a machine network. Sorry for the confusion between machine
 * networks and real networks.
 */
public class MessageNetworkEditToClient
    implements IMessage, IMessageHandler<MessageNetworkEditToClient, IMessage> {

  private boolean adding;
  private int dimensionID;
  private Point3 host;
  private Point3 client;

  public MessageNetworkEditToClient() {
  }

  public MessageNetworkEditToClient(boolean adding, int dimensionID, Point3 host, Point3 client) {
    this.adding = adding;
    this.dimensionID = dimensionID;
    this.host = host;
    this.client = client;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    adding = bytes.readBoolean();
    dimensionID = bytes.readInt();
    host = Point3.readFromByteBuf(bytes);
    client = Point3.readFromByteBuf(bytes);
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    bytes.writeBoolean(adding);
    bytes.writeInt(dimensionID);
    host.writeToByteBuf(bytes);
    client.writeToByteBuf(bytes);
  }

  @Override
  public IMessage onMessage(MessageNetworkEditToClient message, MessageContext context) {
    adding = message.adding;
    dimensionID = message.dimensionID;
    host = message.host;
    client = message.client;

    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    if (dimensionID == player.dimension) {
      TileEntity te = Funcs.getTileEntity(player.worldObj, host);
      if (te instanceof IHost) {
        if (adding) {
          ((IHost) te).addClient(null, client, false);
        } else {
          ((IHost) te).removeClient(client, false);
        }
      }
    }

    return null;
  }
}
