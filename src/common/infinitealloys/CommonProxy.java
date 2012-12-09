package infinitealloys;

import infinitealloys.block.BlockMachine;
import infinitealloys.block.BlockOre;
import infinitealloys.block.Blocks;
import infinitealloys.handlers.EventHandler;
import infinitealloys.handlers.GuiHandler;
import infinitealloys.handlers.WorldGenHandler;
import infinitealloys.item.ItemAlloyBook;
import infinitealloys.item.ItemAlloyIngot;
import infinitealloys.item.ItemBlockIA;
import infinitealloys.item.ItemGPS;
import infinitealloys.item.ItemIngot;
import infinitealloys.item.ItemUpgrade;
import infinitealloys.item.Items;
import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TileEntityAnalyzer;
import infinitealloys.tile.TileEntityComputer;
import infinitealloys.tile.TileEntityMetalForge;
import infinitealloys.tile.TileEntityPrinter;
import infinitealloys.tile.TileEntityXray;
import net.minecraft.src.Achievement;
import net.minecraft.src.Block;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy {

	private ItemStack[] alloys = new ItemStack[References.validAlloyCount];
	private ItemStack[] upgrades = new ItemStack[References.upgradeCount];

	public void initLocalization() {}

	public void initBlocks() {
		Blocks.ore = new BlockOre(InfiniteAlloys.oreID, 0).setCreativeTab(InfiniteAlloys.tabIA).setHardness(2F).setBlockName("ore");
		Blocks.machine = new BlockMachine(InfiniteAlloys.machineID, 6).setCreativeTab(InfiniteAlloys.tabIA).setHardness(2F).setBlockName("machine");
		GameRegistry.registerBlock(Blocks.ore, ItemBlockIA.class);
		GameRegistry.registerBlock(Blocks.machine, ItemBlockIA.class);
		OreDictionary.registerOre("oreZinc", new ItemStack(Blocks.ore, 1, 0));
		OreDictionary.registerOre("oreMagnesium", new ItemStack(Blocks.ore, 1, 1));
		OreDictionary.registerOre("oreScandium", new ItemStack(Blocks.ore, 1, 2));
		OreDictionary.registerOre("oreTantalum", new ItemStack(Blocks.ore, 1, 3));
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 0, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 1, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 2, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 3, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 4, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 5, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 6, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 7, "pickaxe", 3);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 0, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 1, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 2, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 3, "pickaxe", 0);
	}

	public void initItems() {
		Items.ingot = new ItemIngot(InfiniteAlloys.ingotID, 128).setCreativeTab(InfiniteAlloys.tabIA).setItemName("ingot");
		Items.alloyIngot = new ItemAlloyIngot(InfiniteAlloys.alloyIngotID, 128).setItemName("alloyIngot");
		Items.upgrade = new ItemUpgrade(InfiniteAlloys.upgradeID, 129).setMaxStackSize(1).setCreativeTab(InfiniteAlloys.tabIA).setItemName("upgrade");
		Items.gps = new ItemGPS(InfiniteAlloys.gpsID, 140).setMaxStackSize(1).setCreativeTab(InfiniteAlloys.tabIA).setItemName("gps");
		Items.alloyBook = new ItemAlloyBook(InfiniteAlloys.alloyBookID, 141).setMaxStackSize(1).setCreativeTab(InfiniteAlloys.tabIA).setItemName("alloyBook");
		OreDictionary.registerOre("ingotZinc", new ItemStack(Items.ingot, 1, 0));
		OreDictionary.registerOre("ingotMagnesium", new ItemStack(Items.ingot, 1, 1));
		OreDictionary.registerOre("ingotScandium", new ItemStack(Items.ingot, 1, 2));
		OreDictionary.registerOre("ingotTantalum", new ItemStack(Items.ingot, 1, 3));
		for(int i = 0; i < alloys.length; i++)
			alloys[i] = new ItemStack(Items.alloyIngot, 1, i);
		for(int i = 0; i < upgrades.length; i++)
			upgrades[i] = new ItemStack(Items.upgrade, 1, (int)Math.pow(2D, i));
	}

	public void initRecipes() {
		addRecipe(new ItemStack(Blocks.machine, 1, 0), "AWA", "SGS", "ABA", 'A', alloys[2], 'B', alloys[3], 'G', Block.thinGlass, 'S', Block.stoneButton, 'W', upgrades[8]);
		addRecipe(new ItemStack(Blocks.machine, 1, 1), "BBB", "BDB", "NNN", 'B', Block.brick, 'D', Item.doorSteel, 'N', Block.netherrack);
		// TODO: Finish this recipe
		addRecipe(new ItemStack(Blocks.machine, 1, 2), "III", "RGR", "XXX", 'G', Block.glowStone, 'I', Item.ingotIron, 'R', Item.redstone);
		addRecipe(new ItemStack(Blocks.machine, 1, 3), "APA", "BIB", "OOO", 'A', alloys[0], 'B', alloys[1], 'I', new ItemStack(Item.dyePowder, 1, 15), 'O', Block.obsidian, 'P', Block.pistonBase);
		addRecipe(new ItemStack(Blocks.machine, 1, 4), "ADA", "BGB", "RER", 'A', alloys[4], 'B', alloys[5], 'D', Item.diamond, 'E', Item.enderPearl, 'G', Block.thinGlass, 'R', Item.redstone);
		// TODO: Finish this recipe
		addRecipe(upgrades[0], "ASA", "XXX", "AXA", 'A', alloys[0], 'S', Item.sugar);
		// TODO: Finish this recipe
		addRecipe(upgrades[1], "AXA", "XUX", "AXA", 'A', alloys[3], 'U', upgrades[0]);
		// TODO: Finish this recipe
		addRecipe(upgrades[2], "XXX", "XXX", "XXX");
		// TODO: Finish this recipe
		addRecipe(upgrades[3], "XXX", "XUX", "XXX", 'U', upgrades[2]);
		// TODO: Finish this recipe
		addRecipe(upgrades[4], "XXX", "XXX", "XXX");
		// TODO: Finish this recipe
		addRecipe(upgrades[5], "XXX", "XUX", "XXX", 'U', upgrades[4]);
		// TODO: Finish this recipe
		addRecipe(upgrades[6], "XXX", "XXX", "XXX");
		// TODO: Finish this recipe
		addRecipe(upgrades[7], "XXX", "XUX", "XXX", 'U', upgrades[6]);
		// TODO: Finish this recipe
		addRecipe(upgrades[8], "XEX", "XXX", "XXX", 'E', Item.enderPearl);
		// TODO: Finish this recipe
		addRecipe(upgrades[9], "XXX", "XXX", "XXX");
		// TODO: Finish this recipe
		addRecipe(upgrades[10], "XXX", "XUX", "XXX", 'U', upgrades[9]);
		// TODO: Finish this recipe
		addRecipe(new ItemStack(Items.gps), "XWX", "RXR", "XXX", upgrades[8], 'R', Item.redstone);
		addRecipe(new ItemStack(Items.alloyBook), "R", "B", 'B', Item.writableBook, 'R', Item.redstone);
		addSmelting(Blocks.ore.blockID, 0, new ItemStack(Items.ingot, 1, 0), 0.6F);
		addSmelting(Blocks.ore.blockID, 1, new ItemStack(Items.ingot, 1, 1), 0.6F);
		addSmelting(Blocks.ore.blockID, 2, new ItemStack(Items.ingot, 1, 2), 0.7F);
		addSmelting(Blocks.ore.blockID, 3, new ItemStack(Items.ingot, 1, 3), 0.7F);
		addSmelting(Blocks.ore.blockID, 4, new ItemStack(Items.ingot, 1, 4), 0.85F);
		addSmelting(Blocks.ore.blockID, 5, new ItemStack(Items.ingot, 1, 5), 0.85F);
		addSmelting(Blocks.ore.blockID, 6, new ItemStack(Items.ingot, 1, 6), 0.85F);
		addSmelting(Blocks.ore.blockID, 7, new ItemStack(Items.ingot, 1, 7), 1.0F);
	}

	public void initTileEntities() {
		GameRegistry.registerTileEntity(TileEntityComputer.class, "Computer");
		GameRegistry.registerTileEntity(TileEntityMetalForge.class, "MetalForge");
		GameRegistry.registerTileEntity(TileEntityAnalyzer.class, "Analyzer");
		GameRegistry.registerTileEntity(TileEntityPrinter.class, "Printer");
		GameRegistry.registerTileEntity(TileEntityXray.class, "Xray");
		TEHelper.addDetectable(Block.oreCoal, 1);
		TEHelper.addDetectable(Block.oreIron, 2);
		TEHelper.addDetectable(Block.oreGold, 6);
		TEHelper.addDetectable(Block.oreDiamond, 8);
		TEHelper.addDetectable(Blocks.ore, 0, 3);
		TEHelper.addDetectable(Blocks.ore, 1, 4);
		TEHelper.addDetectable(Blocks.ore, 2, 5);
		TEHelper.addDetectable(Blocks.ore, 3, 6);
		TEHelper.addDetectable(Blocks.ore, 4, 7);
		TEHelper.addDetectable(Blocks.ore, 5, 8);
		TEHelper.addDetectable(Blocks.ore, 6, 9);
		TEHelper.addDetectable(Blocks.ore, 7, 10);
	}

	public void initHandlers() {
		GameRegistry.registerWorldGenerator(new WorldGenHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		NetworkRegistry.instance().registerGuiHandler(InfiniteAlloys.instance, new GuiHandler());
	}

	public void initAchievements() {
		InfiniteAlloys.achievements[0] = new Achievement(2000, "craftMetalForge", 0, 0, new ItemStack(Blocks.machine, 1, 1), null).registerAchievement();
		for(int i = 1; i <= References.validAlloyCount; i++)
			InfiniteAlloys.achievements[i] = new Achievement(2000 + i, "smeltAlloy" + i, 2 * i, 0, new ItemStack(Items.alloyIngot, 1, i), InfiniteAlloys.achievements[i - 1]).registerAchievement();
		InfiniteAlloys.achPage = new AchievementPage("Infinite Alloys", InfiniteAlloys.achievements);
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
