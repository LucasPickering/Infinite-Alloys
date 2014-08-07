package infinitealloys.core;

import infinitealloys.block.BlockMachine;
import infinitealloys.block.BlockOre;
import infinitealloys.block.IABlocks;
import infinitealloys.item.IAItems;
import infinitealloys.item.ItemAlloyIngot;
import infinitealloys.item.ItemBlockMachine;
import infinitealloys.item.ItemBlockOre;
import infinitealloys.item.ItemIngot;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.item.ItemMulti;
import infinitealloys.item.ItemUpgrade;
import infinitealloys.item.ItemUpgradeAlloy;
import infinitealloys.item.ItemUpgradeCapacity;
import infinitealloys.item.ItemUpgradeEfficiency;
import infinitealloys.item.ItemUpgradeRange;
import infinitealloys.item.ItemUpgradeSpeed;
import infinitealloys.item.ItemUpgradeWireless;
import infinitealloys.network.NetworkHandler;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.MachineHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
		IABlocks.ore = new BlockOre().setHardness(3F).setBlockName("iaOre");
		IABlocks.machine = new BlockMachine().setHardness(3F).setBlockName("iaMachine");

		GameRegistry.registerBlock(IABlocks.ore, ItemBlockOre.class, IABlocks.ore.getUnlocalizedName());
		GameRegistry.registerBlock(IABlocks.machine, ItemBlockMachine.class, IABlocks.machine.getUnlocalizedName());

		OreDictionary.registerOre("oreZinc", new ItemStack(IABlocks.ore, 1, 0));
		OreDictionary.registerOre("oreMagnesium", new ItemStack(IABlocks.ore, 1, 1));
		OreDictionary.registerOre("oreScandium", new ItemStack(IABlocks.ore, 1, 2));
		OreDictionary.registerOre("oreTantalum", new ItemStack(IABlocks.ore, 1, 3));

		IABlocks.ore.setHarvestLevel("pickaxe", 1, 0);
		IABlocks.ore.setHarvestLevel("pickaxe", 1, 1);
		IABlocks.ore.setHarvestLevel("pickaxe", 1, 2);
		IABlocks.ore.setHarvestLevel("pickaxe", 1, 3);
		IABlocks.ore.setHarvestLevel("pickaxe", 2, 4);
		IABlocks.ore.setHarvestLevel("pickaxe", 2, 5);
		IABlocks.ore.setHarvestLevel("pickaxe", 2, 6);
		IABlocks.ore.setHarvestLevel("pickaxe", 3, 7);
		IABlocks.machine.setHarvestLevel("pickaxe", 0);
	}

	public void initItems() {
		IAItems.multi = new ItemMulti().setUnlocalizedName("iaMulti");
		IAItems.ingot = new ItemIngot().setUnlocalizedName("iaIngot");
		IAItems.alloyIngot = new ItemAlloyIngot().setUnlocalizedName("iaAlloyIngot");
		IAItems.internetWand = new ItemInternetWand().setUnlocalizedName("iaInternetWand");
		IAItems.upgrades[Consts.SPEED] = (ItemUpgrade)new ItemUpgradeSpeed().setUnlocalizedName("iaUpgradeSpeed");
		IAItems.upgrades[Consts.EFFICIENCY] = (ItemUpgrade)new ItemUpgradeEfficiency().setUnlocalizedName("iaUpgradeEfficiency");
		IAItems.upgrades[Consts.CAPACITY] = (ItemUpgrade)new ItemUpgradeCapacity().setUnlocalizedName("iaUpgradeCapacity");
		IAItems.upgrades[Consts.RANGE] = (ItemUpgrade)new ItemUpgradeRange().setUnlocalizedName("iaUpgradeRange");
		IAItems.upgrades[Consts.WIRELESS] = (ItemUpgrade)new ItemUpgradeWireless().setUnlocalizedName("iaUpgradeWireless");
		IAItems.upgrades[Consts.ALLOY_UPG] = (ItemUpgrade)new ItemUpgradeAlloy().setUnlocalizedName("iaUpgradeAlloy");

		GameRegistry.registerItem(IAItems.multi, IAItems.multi.getUnlocalizedName());
		GameRegistry.registerItem(IAItems.ingot, IAItems.ingot.getUnlocalizedName());
		GameRegistry.registerItem(IAItems.alloyIngot, IAItems.alloyIngot.getUnlocalizedName());
		GameRegistry.registerItem(IAItems.internetWand, IAItems.internetWand.getUnlocalizedName());
		for(Item upgrade : IAItems.upgrades)
			GameRegistry.registerItem(upgrade, upgrade.getUnlocalizedName());

		OreDictionary.registerOre("ingotZinc", new ItemStack(IAItems.ingot));
		OreDictionary.registerOre("ingotMagnesium", new ItemStack(IAItems.ingot, 1, 1));
		OreDictionary.registerOre("ingotScandium", new ItemStack(IAItems.ingot, 1, 2));
		OreDictionary.registerOre("ingotTantalum", new ItemStack(IAItems.ingot, 1, 3));

	}

	public void initRecipes() {
		ItemStack machineComponent = new ItemStack(IAItems.multi, 1, 0);
		ItemStack upgradeComponent = new ItemStack(IAItems.multi, 1, 1);
		ItemStack[] alloys = new ItemStack[Consts.VALID_ALLOY_COUNT];
		ItemStack[][] upgrades = new ItemStack[Consts.UPGRADE_TYPE_COUNT][6];
		for(int i = 0; i < alloys.length; i++)
			alloys[i] = new ItemStack(IAItems.alloyIngot, 1, i + 1);
		for(int i = 0; i < upgrades.length; i++)
			for(int j = 0; j < upgrades[i].length; j++)
				upgrades[i][j] = new ItemStack(IAItems.upgrades[i], 1, j);

		/*---MACHINES---*/
		/* Computer */addRecipe(new ItemStack(IABlocks.machine), "W3G", "2C2", "R3R",
				'2', alloys[2], '3', alloys[3], 'C', machineComponent, 'G', Blocks.glass_pane, 'R', Items.redstone, 'W', upgrades[Consts.WIRELESS][0]);

		/* Metal Forge */addRecipe(new ItemStack(IABlocks.machine, 1, 1), "BBB", "BCB", "BBB", 'B', Items.brick, 'C', machineComponent);

		/* X-ray */addRecipe(new ItemStack(IABlocks.machine, 1, 4), "E5E", "4C4", "D5G",
				'4', alloys[4], '5', alloys[5], 'C', machineComponent, 'D', Items.diamond, 'E', Items.ender_pearl, 'G', Blocks.glass_pane);

		/* Pasture */addRecipe(new ItemStack(IABlocks.machine, 1, 4), "F4F", "3C3", "F4F", '3', alloys[3], '4', alloys[4], 'C', machineComponent, 'F', Blocks.fence);

		/* ESU */addRecipe(new ItemStack(IABlocks.machine, 1, 4), "QIQ", "ICI", "QIQ", 'C', machineComponent, 'I', Items.iron_ingot); // TODO: Replace Qs with real items

		/*---UPGRADES---*/
		/* Speed I */addRecipeDict(upgrades[Consts.SPEED][0], "AGA", "AUA", 'A', alloys[0], 'G', Items.gold_ingot, 'U', upgradeComponent);

		/* Speed II */addRecipeDict(upgrades[Consts.SPEED][1], "ADA", "AUA", 'A', alloys[2], 'D', Items.diamond, 'U', upgrades[Consts.SPEED][0]);

		/* Speed III */addRecipeDict(upgrades[Consts.SPEED][1], "ADA", "AUA", 'A', alloys[4], 'D', Items.diamond, 'U', upgrades[Consts.SPEED][0]);

		/* Efficiency I */addRecipeDict(upgrades[Consts.EFFICIENCY][0], "ASA", "AUA", 'A', alloys[1], 'S', Items.iron_shovel, 'U', upgradeComponent);

		/* Efficiency II */addRecipeDict(upgrades[Consts.EFFICIENCY][1], "ASA", "AUA", 'A', alloys[3], 'S', Items.golden_shovel, 'U', upgrades[Consts.EFFICIENCY][0]);

		/* Efficiency III */addRecipeDict(upgrades[Consts.EFFICIENCY][2], "ASA", "AUA", 'A', alloys[5], 'S', Items.diamond_shovel, 'U', upgrades[Consts.EFFICIENCY][1]);

		/* Capacity I */addRecipeDict(upgrades[Consts.CAPACITY][0], "ACA", "AUA", 'A', alloys[0], 'C', Blocks.chest, 'U', upgradeComponent);

		/* Capacity II */addRecipeDict(upgrades[Consts.CAPACITY][1], "ACA", "AUA", 'A', alloys[2], 'C', Blocks.chest, 'U', upgrades[Consts.CAPACITY][0]);

		/* Capacity III */addRecipeDict(upgrades[Consts.CAPACITY][2], "ACA", "AUA", 'A', alloys[4], 'C', Blocks.chest, 'U', upgrades[Consts.CAPACITY][1]);

		/* Range I */addRecipeDict(upgrades[Consts.RANGE][0], "ASA", "AUA", 'A', alloys[1], 'S', Items.iron_sword, 'U', upgradeComponent);

		/* Range II */addRecipeDict(upgrades[Consts.RANGE][1], "ASA", "AUA", 'A', alloys[3], 'S', Items.golden_sword, 'U', upgrades[Consts.RANGE][0]);

		/* Range III */addRecipeDict(upgrades[Consts.RANGE][2], "ASA", "AUA", 'A', alloys[5], 'S', Items.golden_sword, 'U', upgrades[Consts.RANGE][1]);

		/* Wireless */addRecipeDict(upgrades[Consts.WIRELESS][0], "AEA", "AUA", 'A', alloys[1], 'E', Items.ender_pearl, 'U', upgradeComponent);

		/*---OTHER ITEMS---*/
		/* Internet Wand */addRecipeDict(new ItemStack(IAItems.internetWand), " W ", "RSR", 'R', Items.redstone, 'S', Items.stick, 'W', upgrades[Consts.WIRELESS][0]);
		/* Machine Component */addRecipeDict(new ItemStack(IAItems.multi, 1, 0), " W ", "CBC", " W ", 'B', "battery", 'C', "ingotCopper", 'W', "copperWire"); // TODO: Make a real
																																								// recipe
		/* Upgrade Component */addRecipeDict(new ItemStack(IAItems.multi, 1, 1), "CTC", "IWI", 'C', "ingotCopper", 'I', Items.iron_ingot, 'T', "ingotTin", 'W', "copperWire"); // TODO:
																																												// Make
																																												// a
																																												// real
																																												// recipe

		for(int i = 0; i < Consts.METAL_COUNT; i++)
			FurnaceRecipes.smelting().func_151394_a(new ItemStack(IABlocks.ore, 1, i), new ItemStack(IAItems.ingot, 1, i), 0.6F);
	}

	public void initTileEntities() {
		for(EnumMachine machine : EnumMachine.values())
			GameRegistry.registerTileEntity(machine.getTEMClass(), machine.getName());
		MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.coal_ore), 0, 0x333333, 1);
		MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.iron_ore), 0, 0xffffff, 2);
		MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.gold_ore), 0, 0xffffff, 6);
		MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.diamond_ore), 0, 0xffffff, 8);

		MachineHelper.addDictDetectable("oreZinc", Consts.metalColors[0], 3);
		MachineHelper.addDictDetectable("oreMagnesium", Consts.metalColors[1], 4);
		MachineHelper.addDictDetectable("oreScandium", Consts.metalColors[2], 5);
		MachineHelper.addDictDetectable("oreTantalum", Consts.metalColors[3], 6);
		for(int i = 4; i < Consts.METAL_COUNT; i++)
			MachineHelper.addDetectable(Item.getItemFromBlock(IABlocks.ore), i, Consts.metalColors[i], i + 3);

		MachineHelper.addDictDetectable("oreCopper", 0xffffff, 2);
		MachineHelper.addDictDetectable("oreTin", 0xffffff, 2);
	}

	public void initHandlers() {
		NetworkHandler.init();
		gfxHandler = new GfxHandler();
		EventHandler eventHandler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		GameRegistry.registerWorldGenerator(new WorldGenHandler(), 100);
		NetworkRegistry.INSTANCE.registerGuiHandler(InfiniteAlloys.instance, gfxHandler);
	}

	public void initRendering() {}

	private static void addRecipe(ItemStack result, Object... params) {
		GameRegistry.addRecipe(result, params);
	}

	private static void addRecipeDict(ItemStack result, Object... params) {
		GameRegistry.addRecipe(new ShapedOreRecipe(result, params));
	}
}