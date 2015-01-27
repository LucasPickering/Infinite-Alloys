package infinitealloys.network;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
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
    tePoint = message.tePoint;
    bytes = message.bytes;

    TileEntity te = Funcs.getTileEntity(context.getServerHandler().playerEntity.worldObj, tePoint);

    if (te instanceof TileEntityMachine) {
      switch (((TileEntityMachine) te).getEnumMachine()) {
        case METAL_FORGE:
          byte recipeAlloyID = bytes.readByte();
          ((TEEMetalForge) te).handleTEMFDataFromClient(recipeAlloyID);
          break;

        case XRAY:
          boolean shouldSearch = bytes.readBoolean();
          ((TEEXray) te).handleTEXDataFromClient(shouldSearch);
          break;

        case PASTURE:
          byte[] mobActions = new byte[TEEPasture.mobClasses.length];
          for (int i = 0; i < mobActions.length; i++) {
            mobActions[i] = bytes.readByte();
          }
          ((TEEPasture) te).handleTEPPacketData(mobActions);
          break;
      }
    }

    return null;
  }
}
