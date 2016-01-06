package infinitealloys.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import infinitealloys.tile.IHost;
import infinitealloys.util.Funcs;
import io.netty.buffer.ByteBuf;

/**
 * Add or remove a machine to or from a machine network. Sorry for the confusion between machine
 * networks and real networks.
 */
public final class MessageNetworkEditToServer
    implements IMessage, IMessageHandler<MessageNetworkEditToServer, IMessage> {

  private boolean adding;
  private int dimensionID;
  private BlockPos host;
  private BlockPos client;

  public MessageNetworkEditToServer() {
  }

  public MessageNetworkEditToServer(boolean adding, int dimensionID, BlockPos host, BlockPos client) {
    this.adding = adding;
    this.dimensionID = dimensionID;
    this.host = host;
    this.client = client;
  }

  @Override
  public void fromBytes(ByteBuf bytes) {
    adding = bytes.readBoolean();
    dimensionID = bytes.readInt();
    host = Funcs.readBlockPosFromByteBuf(bytes);
    client = Funcs.readBlockPosFromByteBuf(bytes);
  }

  @Override
  public void toBytes(ByteBuf bytes) {
    bytes.writeBoolean(adding);
    bytes.writeInt(dimensionID);
    Funcs.writeBlockPosToByteBuf(bytes, host);
    Funcs.writeBlockPosToByteBuf(bytes, client);
  }

  @Override
  public IMessage onMessage(MessageNetworkEditToServer message, MessageContext context) {
    adding = message.adding;
    dimensionID = message.dimensionID;
    host = message.host;
    client = message.client;

    TileEntity te = DimensionManager.getWorld(dimensionID).getTileEntity(host);
    if (te instanceof IHost) {
      if (adding) {
        ((IHost) te).addClientWithChecks(context.getServerHandler().playerEntity, client, false);
      } else {
        ((IHost) te).removeClient(client, false);
      }
    }

    return null;
  }
}
