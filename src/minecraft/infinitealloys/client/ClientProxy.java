package infinitealloys.client;

import infinitealloys.CommonProxy;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.TileEntityAnalyzer;
import infinitealloys.TileEntityComputer;
import infinitealloys.TileEntityMetalForge;
import infinitealloys.TileEntityPrinter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

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
			addName(new ItemStack(InfiniteAlloys.ore, 0, i), "metal." + References.metalNames[i] + ".name", "tile.iaOre.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "machine.computer.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 1), "machine.metalforge.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 2), "machine.analyzer.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 3), "machine.printer.name");
	}

	@Override
	public void initItems() {
		super.initItems();
		for(int i = 0; i < References.metalCount; i++)
			addName(new ItemStack(InfiniteAlloys.ingot, 0, i), "metal." + References.metalNames[i] + ".name", "item.iaIngot.name");
		addName(new ItemStack(InfiniteAlloys.alloyIngot), "item.iaAlloyIngot.name");
		addName(new ItemStack(InfiniteAlloys.upgrade), "item.iaUpgrade.name");
		addName(new ItemStack(InfiniteAlloys.gps), "item.iaGps.name");
		addName(new ItemStack(InfiniteAlloys.alloyBook), "item.iaAlloyBook.name");
	}

	@Override
	public void initRendering() {
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "sprites.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/computer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/metalforge.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/analyzer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/printer.png");
	}
}
