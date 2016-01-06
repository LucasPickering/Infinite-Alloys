package infinitealloys.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
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
public final class MessageNetworkEditToClient
    implements IMessage, IMessageHandler<MessageNetworkEditToClient, IMessage> {

  private boolean adding;
  private int dimensionID;
  private BlockPos host;
  private BlockPos client;

  public MessageNetworkEditToClient() {
  }

  public MessageNetworkEditToClient(boolean adding, int dimensionID, BlockPos host, BlockPos client) {
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
  public IMessage onMessage(MessageNetworkEditToClient message, MessageContext context) {
    adding = message.adding;
    dimensionID = message.dimensionID;
    host = message.host;
    client = message.client;

    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    if (dimensionID == player.dimension) {
      TileEntity te = player.worldObj.getTileEntity(host);
      if (te instanceof IHost) {
        if (adding) {
          ((IHost) te).addClientWithChecks(null, client, false);
        } else {
          ((IHost) te).removeClient(client, false);
        }
      }
    }

    return null;
  }
}
