package infinitealloys.client;

import infinitealloys.CommonProxy;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import net.minecraft.src.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void initLocalization() {
		for(String langFile : References.langFiles)
			LanguageRegistry.instance().loadLocalization(getClass().getResource(langFile), langFile.substring(langFile.lastIndexOf('/') + 1, langFile.lastIndexOf('.')), true);
	}

	@Override
	public void initBlocks() {
		super.initBlocks();
		for(int i = 0; i < References.metalCount; i++)
			addName(new ItemStack(InfiniteAlloys.ore, 0, i), "metal." + References.metalNames[i] + ".name", "tile.ore.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "machine.computer.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 1), "machine.metalforge.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 2), "machine.analyzer.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 3), "machine.printer.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 4), "machine.xray.name");
	}

	@Override
	public void initItems() {
		super.initItems();
		for(int i = 0; i < References.metalCount; i++)
			addName(new ItemStack(InfiniteAlloys.ingot, 1, i), "metal." + References.metalNames[i] + ".name", "item.ingot.name");
		addName(new ItemStack(InfiniteAlloys.alloyIngot), "item.alloyIngot.name");
		addName(new ItemStack(InfiniteAlloys.upgrade), "item.upgrade.name");
		addName(new ItemStack(InfiniteAlloys.gps), "item.gps.name");
		addName(new ItemStack(InfiniteAlloys.alloyBook), "item.alloyBook.name");
	}

	@Override
	public void initRendering() {
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "sprites.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/computer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/metalforge.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/analyzer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/printer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/alloybook.png");
	}
}
