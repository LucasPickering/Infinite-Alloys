package infinitealloys;

import infinitealloys.handlers.EventHandler;
import infinitealloys.handlers.GuiHandler;
import infinitealloys.handlers.WorldGenHandler;
import net.minecraft.src.Achievement;
import net.minecraft.src.Block;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy {

	public void initLocalization() {}

	public void initBlocks() {
		InfiniteAlloys.ore = new BlockOre(InfiniteAlloys.oreID, 0).setCreativeTab(InfiniteAlloys.tabIA).setHardness(2F).setBlockName("ore");
		InfiniteAlloys.machine = new BlockMachine(InfiniteAlloys.machineID, 6).setCreativeTab(InfiniteAlloys.tabIA).setHardness(2F).setBlockName("machine");
		GameRegistry.registerBlock(InfiniteAlloys.ore, ItemBlockIA.class);
		GameRegistry.registerBlock(InfiniteAlloys.machine, ItemBlockIA.class);
		OreDictionary.registerOre("oreCopper", new ItemStack(InfiniteAlloys.ore, 1, 0));
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
		MinecraftForge.setBlockHarvestLevel(InfiniteAlloys.machine, 3, "pickaxe", 0);
	}

	public void initItems() {
		InfiniteAlloys.ingot = new ItemIngot(InfiniteAlloys.ingotID, 128).setCreativeTab(InfiniteAlloys.tabIA).setItemName("ingot");
		InfiniteAlloys.alloyIngot = new ItemAlloyIngot(InfiniteAlloys.alloyIngotID, 128).setItemName("alloyIngot");
		InfiniteAlloys.upgrade = new ItemUpgrade(InfiniteAlloys.upgradeID, 129).setMaxStackSize(1).setCreativeTab(InfiniteAlloys.tabIA).setItemName("upgrade");
		InfiniteAlloys.gps = new ItemGPS(InfiniteAlloys.gpsID, 138).setMaxStackSize(1).setCreativeTab(InfiniteAlloys.tabIA).setItemName("gps");
		InfiniteAlloys.alloyBook = new ItemAlloyBook(InfiniteAlloys.alloyBookID, 139).setMaxStackSize(1).setCreativeTab(InfiniteAlloys.tabIA).setItemName("alloyBook");
		OreDictionary.registerOre("ingotCopper", new ItemStack(InfiniteAlloys.ingot, 1, 0));
	}

	public void initRecipes() {
		addRecipe(new ItemStack(InfiniteAlloys.machine, 1, 1), "BBB", "BDB", "BBB", 'B', Block.brick, 'D', Item.doorSteel);
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
		GameRegistry.registerTileEntity(TileEntityPrinter.class, "Printer");
		GameRegistry.registerTileEntity(TileEntityXray.class, "Xray");
	}

	public void initHandlers() {
		GameRegistry.registerWorldGenerator(new WorldGenHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		NetworkRegistry.instance().registerGuiHandler(InfiniteAlloys.instance, new GuiHandler());
	}

	public void initAchievements() {
		ItemStack alloyIngot = new ItemStack(InfiniteAlloys.alloyIngot);
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setInteger("alloy", 11);
		alloyIngot.setTagCompound(tagCompound);
		InfiniteAlloys.craftMetalForge = new Achievement(2000, "craftMetalForge", 0, 0, new ItemStack(InfiniteAlloys.machine, 1, 1), null).registerAchievement();
		InfiniteAlloys.smeltAlloy = new Achievement(2001, "smeltAlloy", 2, 0, alloyIngot, InfiniteAlloys.craftMetalForge).registerAchievement();
		InfiniteAlloys.achPage = new AchievementPage("Infinite Alloys", InfiniteAlloys.craftMetalForge, InfiniteAlloys.smeltAlloy);
		AchievementPage.registerAchievementPage(InfiniteAlloys.achPage);
	}

	public void initRendering() {}

	protected void addName(Object obj, String... keys) {
		String name = "";
		for(String key : keys)
			name = name + LanguageRegistry.instance().getStringLocalization(key);
		LanguageRegistry.addName(obj, name);
	}

	private void addRecipe(ItemStack result, Object... params) {
		GameRegistry.addRecipe(result, params);
	}

	private void addSmelting(int inputID, int inputDamage, ItemStack output, float experience) {
		FurnaceRecipes.smelting().addSmelting(inputID, inputDamage, output, experience);
	}
}
