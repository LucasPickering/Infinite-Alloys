package infinitealloys.client;

import infinitealloys.block.Blocks;
import infinitealloys.core.CommonProxy;
import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import infinitealloys.util.MachineHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void initLocalization() {
		for(final String file : Consts.langFiles)
			LanguageRegistry.instance().loadLocalization(Consts.LANG_PATH + file, file.substring(file.lastIndexOf('/') + 1, file.lastIndexOf('.')), false);
	}

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
