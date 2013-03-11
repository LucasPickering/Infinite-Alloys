package infinitealloys.client;

import infinitealloys.block.Blocks;
import infinitealloys.core.CommonProxy;
import infinitealloys.item.Items;
import infinitealloys.util.References;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void initLocalization() {
		for(String langFile : References.langFiles)
			LanguageRegistry.instance().loadLocalization(getClass().getResource(langFile),
					langFile.substring(langFile.lastIndexOf('/') + 1, langFile.lastIndexOf('.')), true);
	}

	@Override
	public void initBlocks() {
		super.initBlocks();
		for(int i = 0; i < References.METAL_COUNT; i++)
			addName(new ItemStack(Blocks.ore, 0, i), "metal." + References.metalNames[i] + ".name", "tile.ore.name");
		addName(new ItemStack(Blocks.machine, 1, 0), "machine.computer.name");
		addName(new ItemStack(Blocks.machine, 1, 1), "machine.metalforge.name");
		addName(new ItemStack(Blocks.machine, 1, 2), "machine.analyzer.name");
		addName(new ItemStack(Blocks.machine, 1, 3), "machine.printer.name");
		addName(new ItemStack(Blocks.machine, 1, 4), "machine.xray.name");
	}

	@Override
	public void initItems() {
		super.initItems();
		addName(new ItemStack(Items.multi), "multi.machineComp.name");
		addName(new ItemStack(Items.multi, 1, 1), "multi.upgComp.name");
		for(int i = 0; i < References.METAL_COUNT; i++)
			addName(new ItemStack(Items.ingot, 1, i), "metal." + References.metalNames[i] + ".name", "item.ingot.name");
		addName(new ItemStack(Items.alloyIngot), "item.alloyIngot.name");
		addName(new ItemStack(Items.upgrade), "item.upgrade.name");
		addName(new ItemStack(Items.gps), "item.gps.name");
		addName(new ItemStack(Items.alloyBook), "item.alloyBook.name");
	}

	@Override
	public void initHandlers() {
		super.initHandlers();
		MinecraftForge.EVENT_BUS.register(gfxHandler);
	}

	@Override
	public void initRendering() {
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "sprites.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/computer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/metalforge.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/analyzer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/printer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/alloybook.png");
		gfxHandler.renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(gfxHandler);
	}
}
