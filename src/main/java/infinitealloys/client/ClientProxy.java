package infinitealloys.client;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import infinitealloys.client.render.RenderBoss;
import infinitealloys.core.CommonProxy;
import infinitealloys.util.EnumBoss;
import infinitealloys.util.EnumMachine;

public class ClientProxy extends CommonProxy {

  @Override
  public void initHandlers() {
    super.initHandlers();
    MinecraftForge.EVENT_BUS.register(gfxHandler);
  }

  @Override
  public void initRendering() {
    gfxHandler.renderID = RenderingRegistry.getNextAvailableRenderId();
    for (EnumMachine machine : EnumMachine.values()) {
      ClientRegistry.bindTileEntitySpecialRenderer(machine.temClass, machine.getTEMR());
    }
    RenderingRegistry.registerBlockHandler(gfxHandler);
    try {
      for (EnumBoss boss : EnumBoss.values()) {
        RenderingRegistry.registerEntityRenderingHandler(boss.entityClass, new RenderBoss(boss));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
