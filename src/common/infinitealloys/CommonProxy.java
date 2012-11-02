package infinitealloys;

import java.io.File;
import java.net.MalformedURLException;
import infinitealloys.handlers.EventHandler;
import infinitealloys.handlers.GuiHandler;
import infinitealloys.handlers.WorldGenHandler;
import net.minecraft.src.Achievement;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy {

	public void initLocalization() {
		/*for(String langFile : References.langFiles) {
			try {
				System.out.println(new File(langFile).toURI().toURL().toString());
				LanguageRegistry.instance().loadLocalization(langFile, langFile.substring(langFile.lastIndexOf('/') + 1, langFile.lastIndexOf('.')), true);
			}
			catch(MalformedURLException e) {
				System.out.println("Infinite Alloys is missing file " + langFile);
			}
		}*/
	}

	public void initBlocks() {
		InfiniteAlloys.ore = new BlockOre(InfiniteAlloys.oreID, 0).setCreativeTab(CreativeTabs.tabBlock).setHardness(2F).setBlockName("iaOre");
		InfiniteAlloys.machine = new BlockMachine(InfiniteAlloys.machineID, 6).setCreativeTab(CreativeTabs.tabBlock).setHardness(2F).setBlockName("iaMachine");
		GameRegistry.registerBlock(InfiniteAlloys.ore, ItemBlockIA.class);
		GameRegistry.registerBlock(InfiniteAlloys.machine, ItemBlockIA.class);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.ore, 0, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.ore, 1, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.ore, 2, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.ore, 3, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.ore, 4, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.ore, 5, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.ore, 6, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.ore, 7, "pickaxe", 3);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.machine, 0, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.machine, 1, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.machine, 2, "pickaxe", 0);
		for(int i = 0; i < References.metalCount; i++)
			addName(new ItemStack(InfiniteAlloys.ore, 0, i), "metal." + References.metalNames[i] + ".name", "tile.iaOre.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "machine.computer.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "machine.metalforge.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "machine.analyzer.name");
	}

	public void initItems() {
		InfiniteAlloys.ingot = new ItemIngot(InfiniteAlloys.ingotID, 128).setCreativeTab(CreativeTabs.tabMaterials).setItemName("iaIngot");
		InfiniteAlloys.alloyIngot = new ItemAlloyIngot(InfiniteAlloys.alloyIngotID, 128).setCreativeTab(CreativeTabs.tabMaterials).setItemName("iaAlloyIngot");
		InfiniteAlloys.upgrade = new ItemUpgrade(InfiniteAlloys.upgradeID, 129).setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc).setItemName("iaUpgrade");
		InfiniteAlloys.gps = new ItemGPS(InfiniteAlloys.gpsID, 138).setMaxStackSize(10).setCreativeTab(CreativeTabs.tabMisc).setItemName("iaGps");
		for(int i = 0; i < References.metalCount; i++)
			addName(new ItemStack(InfiniteAlloys.ingot, 0, i), "metal." + References.metalNames[i] + ".name", "item.iaIngot.name");
		addName(new ItemStack(InfiniteAlloys.alloyIngot), "item.iaAlloyIngot.name");
		addName(new ItemStack(InfiniteAlloys.upgrade), "item.iaUpgrade.name");
		addName(new ItemStack(InfiniteAlloys.gps), "item.iaGps.name");
	}

	public void initRecipes() {
		addSmelting(InfiniteAlloys.ore.blockID, 0, new ItemStack(InfiniteAlloys.ingot, 1, 0), 0.6F);
		addSmelting(InfiniteAlloys.ore.blockID, 1, new ItemStack(InfiniteAlloys.ingot, 1, 1), 0.6F);
		addSmelting(InfiniteAlloys.ore.blockID, 2, new ItemStack(InfiniteAlloys.ingot, 1, 2), 0.7F);
		addSmelting(InfiniteAlloys.ore.blockID, 3, new ItemStack(InfiniteAlloys.ingot, 1, 3), 0.7F);
		addSmelting(InfiniteAlloys.ore.blockID, 4, new ItemStack(InfiniteAlloys.ingot, 1, 4), 0.85F);
		addSmelting(InfiniteAlloys.ore.blockID, 5, new ItemStack(InfiniteAlloys.ingot, 1, 5), 0.85F);
		addSmelting(InfiniteAlloys.ore.blockID, 6, new ItemStack(InfiniteAlloys.ingot, 1, 6), 0.85F);
		addSmelting(InfiniteAlloys.ore.blockID, 7, new ItemStack(InfiniteAlloys.ingot, 1, 7), 1.0F);
	}

	public void initTileEntities() {
		GameRegistry.registerTileEntity(TileEntityComputer.class, "Computer");
		GameRegistry.registerTileEntity(TileEntityMetalForge.class, "MetalForge");
		GameRegistry.registerTileEntity(TileEntityAnalyzer.class, "Analyzer");
	}

	public void initHandlers() {
		GameRegistry.registerWorldGenerator(new WorldGenHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		NetworkRegistry.instance().registerGuiHandler(InfiniteAlloys.instance, new GuiHandler());
	}

	public void initAchievements() {
		InfiniteAlloys.smeltAlloy = new Achievement(2001, "smeltAlloy", 1, -2, InfiniteAlloys.alloyIngot, null).registerAchievement();
		InfiniteAlloys.achPage = new AchievementPage("Infinite Alloys", InfiniteAlloys.smeltAlloy);
		AchievementPage.registerAchievementPage(InfiniteAlloys.achPage);
	}

	public void initRendering() {}

	private void addName(Object obj, String key, String... extraKeys) {
		String name = LanguageRegistry.instance().getStringLocalization(key);
		for(String extraKey : extraKeys)
			name = name + LanguageRegistry.instance().getStringLocalization(extraKey);
		LanguageRegistry.addName(obj, name);
	}

	private void addSmelting(int inputID, int inputDamage, ItemStack output, float experience) {
		FurnaceRecipes.smelting().addSmelting(inputID, inputDamage, output, experience);
	}
}
