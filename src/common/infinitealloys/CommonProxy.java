package infinitealloys;

import infinitealloys.handlers.WorldGenHandler;
import net.minecraft.src.Achievement;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy {

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
		for(int i = 0; i < IAValues.metalCount; i++)
			LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 1, i), IAValues.metalNames[i] + " Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "Computer");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 1), "Metal Forge");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 2), "Crafter");
		GameRegistry.registerWorldGenerator(new WorldGenHandler());
	}

	public void initItems() {
		InfiniteAlloys.ingot = new ItemIngot(InfiniteAlloys.ingotID, 0).setCreativeTab(CreativeTabs.tabMaterials).setItemName("iaIngot");
		InfiniteAlloys.alloyIngot = new ItemAlloyIngot(InfiniteAlloys.alloyIngotID, 0).setCreativeTab(CreativeTabs.tabMaterials).setItemName("iaAlloyIngot");
		InfiniteAlloys.upgrade = new ItemUpgrade(InfiniteAlloys.upgradeID, 1).setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc).setItemName("iaUpgrade");
		InfiniteAlloys.gps = new ItemGPS(InfiniteAlloys.gpsID, 2).setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc).setItemName("iaGps");
		for(int i = 0; i < IAValues.metalCount; i++)
			LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 0, i), IAValues.metalNames[i] + " Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.alloyIngot), "Alloy Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.upgrade), "Upgrade");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.gps), "GPS");
	}

	public void initRecipes() {
		addSmelting(InfiniteAlloys.ore.blockID, 0, new ItemStack(InfiniteAlloys.ingot, 1, 0));
		addSmelting(InfiniteAlloys.ore.blockID, 1, new ItemStack(InfiniteAlloys.ingot, 1, 1));
		addSmelting(InfiniteAlloys.ore.blockID, 2, new ItemStack(InfiniteAlloys.ingot, 1, 2));
		addSmelting(InfiniteAlloys.ore.blockID, 3, new ItemStack(InfiniteAlloys.ingot, 1, 3));
		addSmelting(InfiniteAlloys.ore.blockID, 4, new ItemStack(InfiniteAlloys.ingot, 1, 4));
		addSmelting(InfiniteAlloys.ore.blockID, 5, new ItemStack(InfiniteAlloys.ingot, 1, 5));
		addSmelting(InfiniteAlloys.ore.blockID, 6, new ItemStack(InfiniteAlloys.ingot, 1, 6));
		addSmelting(InfiniteAlloys.ore.blockID, 7, new ItemStack(InfiniteAlloys.ingot, 1, 7));
	}

	public void initTileEntities() {
		GameRegistry.registerTileEntity(TileEntityComputer.class, "Computer");
		GameRegistry.registerTileEntity(TileEntityMetalForge.class, "MetalForge");
	}

	public void initAchievements() {
		InfiniteAlloys.smeltAlloy = new Achievement(2001, "smeltAlloy", 1, -2, InfiniteAlloys.alloyIngot, null).registerAchievement();
		InfiniteAlloys.achPage = new AchievementPage("Infinite Alloys", InfiniteAlloys.smeltAlloy);
		AchievementPage.registerAchievementPage(InfiniteAlloys.achPage);
		addLocalization("achievement.smeltAlloy", "en_US", "");
		addLocalization("achievement.smeltAlloy.desc", "en_US", "Created Yo");
	}

	public void initRendering() {
	}

	private void addLocalization(String key, String lang, String value) {
		LanguageRegistry.instance().addStringLocalization(key, lang, value);
	}

	private void addSmelting(int inputID, int inputDamage, ItemStack output) {
		FurnaceRecipes.smelting().addSmelting(inputID, inputDamage, output);
	}
}
