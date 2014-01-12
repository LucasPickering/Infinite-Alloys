package infinitealloys.core;

import infinitealloys.block.BlockMachine;
import infinitealloys.block.BlockOre;
import infinitealloys.block.Blocks;
import infinitealloys.handlers.EventHandler;
import infinitealloys.handlers.GfxHandler;
import infinitealloys.handlers.WorldGenHandler;
import infinitealloys.item.ItemAlloyIngot;
import infinitealloys.item.ItemBlockIA;
import infinitealloys.item.ItemIngot;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.item.ItemMulti;
import infinitealloys.item.ItemUpgrade;
import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy {

	private final ItemStack[] alloys = new ItemStack[Consts.VALID_ALLOY_COUNT];
	private final ItemStack[] upgrades = new ItemStack[Consts.UPGRADE_COUNT];
	public GfxHandler gfxHandler;

	public void initLocalization() {}

	public void initBlocks() {
		Blocks.ore = new BlockOre(Blocks.oreID).setHardness(3F).setUnlocalizedName("IAore");
		Blocks.machine = new BlockMachine(Blocks.machineID).setHardness(3F).setUnlocalizedName("IAmachine");

		GameRegistry.registerBlock(Blocks.ore, ItemBlockIA.class, "IAore");
		GameRegistry.registerBlock(Blocks.machine, ItemBlockIA.class, "IAmachine");

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
		Items.multi = new ItemMulti(Items.multiID);
		Items.ingot = new ItemIngot(Items.ingotID);
		Items.alloyIngot = new ItemAlloyIngot(Items.alloyIngotID);
		Items.upgrade = new ItemUpgrade(Items.upgradeID);
		Items.internetWand = new ItemInternetWand(Items.internetWandID).setMaxStackSize(1);

		OreDictionary.registerOre("ingotZinc", new ItemStack(Items.ingot));
		OreDictionary.registerOre("ingotMagnesium", new ItemStack(Items.ingot, 1, 1));
		OreDictionary.registerOre("ingotScandium", new ItemStack(Items.ingot, 1, 2));
		OreDictionary.registerOre("ingotTantalum", new ItemStack(Items.ingot, 1, 3));

		for(int i = 0; i < alloys.length; i++)
			alloys[i] = new ItemStack(Items.alloyIngot, 1, i + 1);
		for(int i = 0; i < upgrades.length; i++)
			upgrades[i] = new ItemStack(Items.upgrade, 1, 1 << i);
	}

	public void initRecipes() {
		/* Machine Component */addRecipeDict(new ItemStack(Items.multi, 1, 0), " W ", "CBC", " W ", 'B', "battery", 'C', "ingotCopper", 'W', "copperWire");
		/* Upgrade Component */addRecipeDict(new ItemStack(Items.multi, 1, 1), "CTC", "IWI", 'C', "ingotCopper", 'I', Item.ingotIron, 'T', "ingotTin", 'W', "copperWire");

		/* Computer */addRecipe(new ItemStack(Blocks.machine), "ASA", "WCG", "ABA", 'A', alloys[2], 'B', alloys[3], 'C', Items.multi, 'G', Block.thinGlass, 'S', Block.stoneButton, 'W', upgrades[8]);
		/* Metal Forge */addRecipe(new ItemStack(Blocks.machine, 1, 1), "B B", " C ", "B B", 'B', Item.brick, 'C', Items.multi);
		/* Analyzer */addRecipe(new ItemStack(Blocks.machine, 1, 2), "I I", " C ", "I I", 'C', Items.multi, 'I', Item.ingotIron);
		/* X-ray */addRecipe(new ItemStack(Blocks.machine, 1, 4), "ADA", "BCB", "EGE", 'A', alloys[4], 'B', alloys[5], 'C', Items.multi, 'D', Item.diamond, 'E', Item.enderPearl, 'G', Block.thinGlass);

		/* Speed I */addRecipeDict(upgrades[0], "AGA", "AUA", 'A', alloys[2], 'G', Item.ingotGold, 'U', new ItemStack(Items.multi, 1, 1));
		/* Speed II */addRecipeDict(upgrades[1], "ADA", "AUA", 'A', alloys[5], 'D', Item.diamond, 'U', upgrades[0]);
		/* Efficiency I */addRecipeDict(upgrades[2], "AIA", "AUA", 'A', alloys[1], 'I', Item.shovelIron, 'U', new ItemStack(Items.multi, 1, 1));
		/* Efficiency II */addRecipeDict(upgrades[3], "AGA", "AUA", 'A', alloys[4], 'G', Item.shovelGold, 'U', upgrades[2]);
		/* Capacity I */addRecipeDict(upgrades[4], "ASA", "AUA", 'A', alloys[0], 'S', Block.chest, 'U', new ItemStack(Items.multi, 1, 1));
		/* Capacity II */addRecipeDict(upgrades[5], "ASA", "AUA", 'A', alloys[3], 'S', Block.chest, 'U', upgrades[4]);
		/* Range I */addRecipeDict(upgrades[6], "AIA", "AUA", 'A', alloys[3], 'I', Item.swordIron, 'U', new ItemStack(Items.multi, 1, 1));
		/* Range II */addRecipeDict(upgrades[7], "AGA", "AUA", 'A', alloys[5], 'G', Item.swordGold, 'U', upgrades[6]);
		/* Wireless */addRecipeDict(upgrades[8], "AEA", "AUA", 'A', alloys[1], 'E', Item.enderPearl, 'U', new ItemStack(Items.multi, 1, 1));

		/* Internet Wand */addRecipeDict(new ItemStack(Items.internetWand), " W ", "RSR", 'R', Item.redstone, 'S', Item.stick, 'W', upgrades[8]);

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
		for(int i = 0; i < Consts.MACHINE_COUNT; i++)
			GameRegistry.registerTileEntity(MachineHelper.MACHINE_CLASSES[i], MachineHelper.MACHINE_NAMES[i]);
		MachineHelper.addDetectable(Block.oreCoal, 1);
		MachineHelper.addDetectable(Block.oreIron, 2);
		MachineHelper.addDetectable(Block.oreGold, 6);
		MachineHelper.addDetectable(Block.oreDiamond, 8);
		MachineHelper.addDictDetectables("oreZinc", 3);
		MachineHelper.addDictDetectables("oreMagnesium", 4);
		MachineHelper.addDictDetectables("oreScandium", 5);
		MachineHelper.addDictDetectables("oreTantalum", 6);
		MachineHelper.addDetectable(Blocks.ore, 4, 7);
		MachineHelper.addDetectable(Blocks.ore, 5, 8);
		MachineHelper.addDetectable(Blocks.ore, 6, 9);
		MachineHelper.addDetectable(Blocks.ore, 7, 10);
		MachineHelper.addDictDetectables("oreCopper", 2);
		MachineHelper.addDictDetectables("oreTin", 2);
	}

	public void initHandlers() {
		gfxHandler = new GfxHandler();
		final EventHandler eventHandler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		GameRegistry.registerCraftingHandler(eventHandler);
		GameRegistry.registerWorldGenerator(new WorldGenHandler());
		NetworkRegistry.instance().registerGuiHandler(InfiniteAlloys.instance, gfxHandler);
	}

	public void initRendering() {}

	protected void addName(Object obj, String... keys) {
		String name = "";
		for(final String key : keys)
			name = name + Funcs.getLoc(key);
		LanguageRegistry.addName(obj, name);
	}

	private static void addRecipe(ItemStack result, Object... params) {
		GameRegistry.addRecipe(result, params);
	}

	private static void addRecipeDict(ItemStack result, Object... params) {
		GameRegistry.addRecipe(new ShapedOreRecipe(result, params));
	}

	private static void addSmelting(int inputID, int inputDamage, ItemStack output, float experience) {
		FurnaceRecipes.smelting().addSmelting(inputID, inputDamage, output, experience);
	}
}