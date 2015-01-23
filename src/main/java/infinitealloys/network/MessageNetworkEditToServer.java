package infinitealloys.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

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
public class MessageNetworkEditToServer
    implements IMessage, IMessageHandler<MessageNetworkEditToServer, IMessage> {

  private boolean adding;
  private int dimensionID;
  private Point3 host;
  private Point3 client;

  public MessageNetworkEditToServer() {
  }

  public MessageNetworkEditToServer(boolean adding, int dimensionID, Point3 host, Point3 client) {
    this.adding = adding;
    this.dimensionID = dimensionID;
    this.host = host;
    this.client = client;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    adding = bytes.readBoolean();
    dimensionID = bytes.readInt();
    host = new Point3(bytes.readInt(), bytes.readInt(), bytes.readInt());
    client = new Point3(bytes.readInt(), bytes.readInt(), bytes.readInt());
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    NetworkHandler.writeObject(bytes, adding);
    NetworkHandler.writeObject(bytes, dimensionID);
    NetworkHandler.writeObject(bytes, host);
    NetworkHandler.writeObject(bytes, client);
  }

  @Override
  public IMessage onMessage(MessageNetworkEditToServer message, MessageContext context) {
    adding = message.adding;
    dimensionID = message.dimensionID;
    host = message.host;
    client = message.client;

    TileEntity te = Funcs.getTileEntity(DimensionManager.getWorld(dimensionID), host);
    if (te instanceof IHost) {
      if (adding) {
        ((IHost) te).addClient(context.getServerHandler().playerEntity, client, false);
      } else {
        ((IHost) te).removeClient(client, false);
      }
    }

    return null;
  }
}
