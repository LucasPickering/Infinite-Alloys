package infinitealloys.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import infinitealloys.tile.TEEEnergyStorage;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityIA;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point3;
import io.netty.buffer.ByteBuf;

public class MessageTEToClient implements IMessage, IMessageHandler<MessageTEToClient, IMessage> {

  private Point3 tePoint;
  private Object[] data;
  private ByteBuf bytes;

  public MessageTEToClient() {
  }

  public MessageTEToClient(TileEntityIA teia) {
    tePoint = teia.coords();
    data = teia.getSyncDataToClient();
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
  public IMessage onMessage(MessageTEToClient message, MessageContext context) {
    tePoint = message.tePoint;
    bytes = message.bytes;

    TileEntity te = Funcs.getTileEntity(Minecraft.getMinecraft().theWorld, tePoint);

    if (te instanceof TileEntityIA) {
      byte facingDir = bytes.readByte();
      ((TileEntityIA) te).handleTEIADataFromServer(facingDir);

      if (te instanceof TileEntityMachine) {
        int[] upgrades = new int[Consts.UPGRADE_TYPE_COUNT];
        for (int i = 0; i < upgrades.length; i++) {
          upgrades[i] = bytes.readInt();
        }
        ((TileEntityMachine) te).handleTEMDataFromServer(upgrades);

        if (te instanceof TileEntityElectric) {
          int processProgress = bytes.readInt();
          ((TileEntityElectric) te).handleTEEDataFromServer(processProgress);

          switch (((TileEntityElectric) te).getEnumMachine()) {
            case METAL_FORGE:
              byte recipeAlloyID = bytes.readByte();
              ((TEEMetalForge) te).handleTEMFDataFromServer(recipeAlloyID);
              break;

            case XRAY:
              int detectedBlocksSize = bytes.readInt();
              Point3[] detectedBlocks = new Point3[detectedBlocksSize];
              for (int i = 0; i < detectedBlocksSize; i++) {
                detectedBlocks[i] = new Point3(bytes.readInt(), bytes.readInt(), bytes.readInt());
              }
              ((TEEXray) te).handleTEXDataFromServer(detectedBlocks);
              break;

            case PASTURE:
              byte[] mobActions = new byte[TEEPasture.mobClasses.length];
              for (int i = 0; i < mobActions.length; i++) {
                mobActions[i] = bytes.readByte();
              }
              ((TEEPasture) te).handleTEPPacketData(mobActions);
              break;

            case ENERGY_STORAGE:
              int currentRK = bytes.readInt();
              int baseRKPerTick = bytes.readInt();
              ((TEEEnergyStorage) te).handleTEESDataFromServer(currentRK, baseRKPerTick);
              break;
          }
        }
      }
    }

    return null;
  }
}
