package infinitealloys.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import infinitealloys.util.Consts;

public class NetworkHandler {

  public static final SimpleNetworkWrapper simpleNetworkWrapper =
      NetworkRegistry.INSTANCE.newSimpleChannel(Consts.MOD_ID);

  public static void init() {
    simpleNetworkWrapper.registerMessage(MessageNetworkEditToClient.class,
                                         MessageNetworkEditToClient.class, 0, Side.CLIENT);
    simpleNetworkWrapper.registerMessage(MessageNetworkEditToServer.class,
                                         MessageNetworkEditToServer.class, 1, Side.SERVER);
    simpleNetworkWrapper.registerMessage(MessageOpenGui.class,
                                         MessageOpenGui.class, 2, Side.CLIENT);
    simpleNetworkWrapper.registerMessage(MessageTEToClient.class,
                                         MessageTEToClient.class, 3, Side.CLIENT);
    simpleNetworkWrapper.registerMessage(MessageTEToServer.class,
                                         MessageTEToServer.class, 4, Side.SERVER);
    simpleNetworkWrapper.registerMessage(MessageWand.class,
                                         MessageWand.class, 5, Side.SERVER);
    simpleNetworkWrapper.registerMessage(MessageValidAlloys.class,
                                         MessageValidAlloys.class, 6, Side.CLIENT);
    simpleNetworkWrapper.registerMessage(MessageOpenGui.class,
                                         MessageOpenGui.class, 7, Side.SERVER);
  }
}