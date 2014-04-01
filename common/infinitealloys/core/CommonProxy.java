package infinitealloys.core;

import infinitealloys.block.BlockMachine;
import infinitealloys.block.BlockOre;
import infinitealloys.block.Blocks;
import infinitealloys.item.ItemAlloyIngot;
import infinitealloys.item.ItemBlockMachine;
import infinitealloys.item.ItemBlockOre;
import infinitealloys.item.ItemIngot;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.item.ItemMulti;
import infinitealloys.item.ItemUpgrade;
import infinitealloys.item.Items;
import infinitealloys.util.Consts;
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

public class CommonProxy {

	public GfxHandler gfxHandler;

	public void initLocalization() {}

	public void initBlocks() {
		Blocks.ore = new BlockOre(Blocks.oreID).setHardness(3F).setUnlocalizedName("IAore");
		Blocks.machine = new BlockMachine(Blocks.machineID).setHardness(3F).setUnlocalizedName("IAmachine");

		GameRegistry.registerBlock(Blocks.ore, ItemBlockOre.class, Blocks.ore.getUnlocalizedName());
		GameRegistry.registerBlock(Blocks.machine, ItemBlockMachine.class, Blocks.machine.getUnlocalizedName());

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
		Items.multi = new ItemMulti(Items.multiID).setUnlocalizedName("IAmulti");
		Items.ingot = new ItemIngot(Items.ingotID).setUnlocalizedName("IAingot");
		Items.alloyIngot = new ItemAlloyIngot(Items.alloyIngotID).setUnlocalizedName("IAalloyingot");
		Items.upgrade = new ItemUpgrade(Items.upgradeID).setUnlocalizedName("IAupgrade");
		Items.internetWand = new ItemInternetWand(Items.internetWandID).setUnlocalizedName("IAinternetwand");

		GameRegistry.registerItem(Items.multi, Items.multi.getUnlocalizedName());
		GameRegistry.registerItem(Items.ingot, Items.ingot.getUnlocalizedName());
		GameRegistry.registerItem(Items.alloyIngot, Items.alloyIngot.getUnlocalizedName());
		GameRegistry.registerItem(Items.upgrade, Items.upgrade.getUnlocalizedName());
		GameRegistry.registerItem(Items.internetWand, Items.internetWand.getUnlocalizedName());

		OreDictionary.registerOre("ingotZinc", new ItemStack(Items.ingot));
		OreDictionary.registerOre("ingotMagnesium", new ItemStack(Items.ingot, 1, 1));
		OreDictionary.registerOre("ingotScandium", new ItemStack(Items.ingot, 1, 2));
		OreDictionary.registerOre("ingotTantalum", new ItemStack(Items.ingot, 1, 3));

	}

	public void initRecipes() {
		ItemStack[] alloys = new ItemStack[Consts.VALID_ALLOY_COUNT];
		ItemStack[] upgrades = new ItemStack[Consts.UPGRADE_COUNT];
		for(int i = 0; i < alloys.length; i++)
			alloys[i] = new ItemStack(Items.alloyIngot, 1, i + 1);
		for(int i = 0; i < upgrades.length; i++)
			upgrades[i] = new ItemStack(Items.upgrade, 1, 1 << i);

		/*---MACHINES---*/
		/* Computer */addRecipe(new ItemStack(Blocks.machine),
				"W3G", "2C2", "R3R", '2', alloys[2], '3', alloys[3], 'C', Items.multi, 'G', Block.thinGlass, 'R', Item.redstone, 'W', upgrades[8]);
		/* Metal Forge */addRecipe(new ItemStack(Blocks.machine, 1, 1),
				"BBB", "BCB", "BBB", 'B', Item.brick, 'C', Items.multi);
		/* Analyzer */addRecipe(new ItemStack(Blocks.machine, 1, 2), // TODO: Replace Qs with real items
				"QIQ", "ICI", "QIQ", 'C', Items.multi, 'I', Item.ingotIron);
		/* X-ray */addRecipe(new ItemStack(Blocks.machine, 1, 4),
				"E5E", "4C4", "D5G", '4', alloys[4], '5', alloys[5], 'C', Items.multi, 'D', Item.diamond, 'E', Item.enderPearl, 'G', Block.thinGlass);
		/* Pasture */addRecipe(new ItemStack(Blocks.machine, 1, 4),
				"F4F", "3C3", "F4F", '3', alloys[3], '4', alloys[4], 'C', Items.multi, 'F', Block.fence);
		/* ESU */addRecipe(new ItemStack(Blocks.machine, 1, 4), // TODO: Replace Qs with real items
				"QIQ", "ICI", "QIQ", 'C', Items.multi, 'I', Item.ingotIron);

		/*---UPGRADES---*/
		/* Speed I */addRecipeDict(upgrades[0],
				"AGA", "AUA", 'A', alloys[2], 'G', Item.ingotGold, 'U', new ItemStack(Items.multi, 1, 1));

		/* Speed II */addRecipeDict(upgrades[1],
				"ADA", "AUA", 'A', alloys[5], 'D', Item.diamond, 'U', upgrades[0]);

		/* Efficiency I */addRecipeDict(upgrades[2],
				"AIA", "AUA", 'A', alloys[1], 'I', Item.shovelIron, 'U', new ItemStack(Items.multi, 1, 1));

		/* Efficiency II */addRecipeDict(upgrades[3],
				"AGA", "AUA", 'A', alloys[4], 'G', Item.shovelGold, 'U', upgrades[2]);

		/* Capacity I */addRecipeDict(upgrades[4],
				"ASA", "AUA", 'A', alloys[0], 'S', Block.chest, 'U', new ItemStack(Items.multi, 1, 1));

		/* Capacity II */addRecipeDict(upgrades[5],
				"ASA", "AUA", 'A', alloys[3], 'S', Block.chest, 'U', upgrades[4]);

		/* Range I */addRecipeDict(upgrades[6],
				"AIA", "AUA", 'A', alloys[3], 'I', Item.swordIron, 'U', new ItemStack(Items.multi, 1, 1));

		/* Range II */addRecipeDict(upgrades[7],
				"AGA", "AUA", 'A', alloys[5], 'G', Item.swordGold, 'U', upgrades[6]);

		/* Wireless */addRecipeDict(upgrades[8],
				"AEA", "AUA", 'A', alloys[1], 'E', Item.enderPearl, 'U', new ItemStack(Items.multi, 1, 1));

		/*---OTHER ITEMS---*/
		/* Internet Wand */addRecipeDict(new ItemStack(Items.internetWand),
				" W ", "RSR", 'R', Item.redstone, 'S', Item.stick, 'W', upgrades[8]);
		/* Machine Component */addRecipeDict(new ItemStack(Items.multi, 1, 0), // TODO: Make a real recipe
				" W ", "CBC", " W ", 'B', "battery", 'C', "ingotCopper", 'W', "copperWire");
		/* Upgrade Component */addRecipeDict(new ItemStack(Items.multi, 1, 1), // TODO: Make a real recipe
				"CTC", "IWI", 'C', "ingotCopper", 'I', Item.ingotIron, 'T', "ingotTin", 'W', "copperWire");

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
		EventHandler eventHandler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		GameRegistry.registerCraftingHandler(eventHandler);
		GameRegistry.registerWorldGenerator(new WorldGenHandler());
		NetworkRegistry.instance().registerGuiHandler(InfiniteAlloys.instance, gfxHandler);
	}

	public void initRendering() {}

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