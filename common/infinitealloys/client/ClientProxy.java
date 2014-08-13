package infinitealloys.client;

import infinitealloys.core.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void initHandlers() {
		super.initHandlers();
		MinecraftForge.EVENT_BUS.register(gfxHandler);
	}

	@Override
	public void initRendering() {
		gfxHandler.renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(gfxHandler);
	}
}
