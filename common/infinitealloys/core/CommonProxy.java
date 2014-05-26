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
import infinitealloys.network.ChannelHandler;
import infinitealloys.util.Consts;
import infinitealloys.util.MachineHelper;
import java.util.EnumMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	public GfxHandler gfxHandler;
	public EnumMap<Side, FMLEmbeddedChannel> channels;

	public void initLocalization() {}

	public void initBlocks() {
		IABlocks.ore = new BlockOre().setHardness(3F).setBlockName("IAore");
		IABlocks.machine = new BlockMachine().setHardness(3F).setBlockName("IAmachine");

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
		IAItems.multi = new ItemMulti().setUnlocalizedName("IAmulti");
		IAItems.ingot = new ItemIngot().setUnlocalizedName("IAingot");
		IAItems.alloyIngot = new ItemAlloyIngot().setUnlocalizedName("IAalloyingot");
		IAItems.upgrade = new ItemUpgrade().setUnlocalizedName("IAupgrade");
		IAItems.internetWand = new ItemInternetWand().setUnlocalizedName("IAinternetwand");

		GameRegistry.registerItem(IAItems.multi, IAItems.multi.getUnlocalizedName());
		GameRegistry.registerItem(IAItems.ingot, IAItems.ingot.getUnlocalizedName());
		GameRegistry.registerItem(IAItems.alloyIngot, IAItems.alloyIngot.getUnlocalizedName());
		GameRegistry.registerItem(IAItems.upgrade, IAItems.upgrade.getUnlocalizedName());
		GameRegistry.registerItem(IAItems.internetWand, IAItems.internetWand.getUnlocalizedName());

		OreDictionary.registerOre("ingotZinc", new ItemStack(IAItems.ingot));
		OreDictionary.registerOre("ingotMagnesium", new ItemStack(IAItems.ingot, 1, 1));
		OreDictionary.registerOre("ingotScandium", new ItemStack(IAItems.ingot, 1, 2));
		OreDictionary.registerOre("ingotTantalum", new ItemStack(IAItems.ingot, 1, 3));

	}

	public void initRecipes() {
		ItemStack[] alloys = new ItemStack[Consts.VALID_ALLOY_COUNT];
		ItemStack[] upgrades = new ItemStack[Consts.UPGRADE_COUNT];
		for(int i = 0; i < alloys.length; i++)
			alloys[i] = new ItemStack(IAItems.alloyIngot, 1, i + 1);
		for(int i = 0; i < upgrades.length; i++)
			upgrades[i] = new ItemStack(IAItems.upgrade, 1, 1 << i);

		/*---MACHINES---*/
		/* Computer */addRecipe(new ItemStack(IABlocks.machine),
				"W3G", "2C2", "R3R", '2', alloys[2], '3', alloys[3], 'C', IAItems.multi, 'G', Blocks.glass_pane, 'R', Items.redstone, 'W', upgrades[8]);

		/* Metal Forge */addRecipe(new ItemStack(IABlocks.machine, 1, 1), "BBB", "BCB", "BBB", 'B', Items.brick, 'C', IAItems.multi);

		/* Analyzer */addRecipe(new ItemStack(IABlocks.machine, 1, 2), "QIQ", "ICI", "QIQ", 'C', IAItems.multi, 'I', Items.iron_ingot); // TODO: Replace Qs with real items

		/* X-ray */addRecipe(new ItemStack(IABlocks.machine, 1, 4),
				"E5E", "4C4", "D5G", '4', alloys[4], '5', alloys[5], 'C', IAItems.multi, 'D', Items.diamond, 'E', Items.ender_pearl, 'G', Blocks.glass_pane);

		/* Pasture */addRecipe(new ItemStack(IABlocks.machine, 1, 4), "F4F", "3C3", "F4F", '3', alloys[3], '4', alloys[4], 'C', IAItems.multi, 'F', Blocks.fence);

		/* ESU */addRecipe(new ItemStack(IABlocks.machine, 1, 4), "QIQ", "ICI", "QIQ", 'C', IAItems.multi, 'I', Items.iron_ingot); // TODO: Replace Qs with real items

		/*---UPGRADES---*/
		/* Speed I */addRecipeDict(upgrades[0], "AGA", "AUA", 'A', alloys[2], 'G', Items.gold_ingot, 'U', new ItemStack(IAItems.multi, 1, 1));

		/* Speed II */addRecipeDict(upgrades[1], "ADA", "AUA", 'A', alloys[5], 'D', Items.diamond, 'U', upgrades[0]);

		/* Efficiency I */addRecipeDict(upgrades[2], "AIA", "AUA", 'A', alloys[1], 'I', Items.iron_shovel, 'U', new ItemStack(IAItems.multi, 1, 1));

		/* Efficiency II */addRecipeDict(upgrades[3], "AGA", "AUA", 'A', alloys[4], 'G', Items.golden_shovel, 'U', upgrades[2]);

		/* Capacity I */addRecipeDict(upgrades[4], "ASA", "AUA", 'A', alloys[0], 'S', Blocks.chest, 'U', new ItemStack(IAItems.multi, 1, 1));

		/* Capacity II */addRecipeDict(upgrades[5], "ASA", "AUA", 'A', alloys[3], 'S', Blocks.chest, 'U', upgrades[4]);

		/* Range I */addRecipeDict(upgrades[6], "AIA", "AUA", 'A', alloys[3], 'I', Items.iron_sword, 'U', new ItemStack(IAItems.multi, 1, 1));

		/* Range II */addRecipeDict(upgrades[7], "AGA", "AUA", 'A', alloys[5], 'G', Items.golden_sword, 'U', upgrades[6]);

		/* Wireless */addRecipeDict(upgrades[8], "AEA", "AUA", 'A', alloys[1], 'E', Items.ender_pearl, 'U', new ItemStack(IAItems.multi, 1, 1));

		/*---OTHER ITEMS---*/
		/* Internet Wand */addRecipeDict(new ItemStack(IAItems.internetWand), " W ", "RSR", 'R', Items.redstone, 'S', Items.stick, 'W', upgrades[8]);
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
		for(int i = 0; i < Consts.MACHINE_COUNT; i++)
			GameRegistry.registerTileEntity(MachineHelper.MACHINE_CLASSES[i], MachineHelper.MACHINE_NAMES[i]);
		MachineHelper.addDetectable(Blocks.coal_ore, 1);
		MachineHelper.addDetectable(Blocks.iron_ore, 2);
		MachineHelper.addDetectable(Blocks.gold_ore, 6);
		MachineHelper.addDetectable(Blocks.diamond_ore, 8);

		MachineHelper.addDictDetectable("oreZinc", 3);
		MachineHelper.addDictDetectable("oreMagnesium", 4);
		MachineHelper.addDictDetectable("oreScandium", 5);
		MachineHelper.addDictDetectable("oreTantalum", 6);
		for(int i = 4; i < Consts.METAL_COUNT; i++)
			MachineHelper.addDetectable(new ItemStack(IABlocks.ore, 1, i), i + 3);

		MachineHelper.addDictDetectable("oreCopper", 2);
		MachineHelper.addDictDetectable("oreTin", 2);
	}

	public void initHandlers() {
		channels = NetworkRegistry.INSTANCE.newChannel("infinitealloys", new ChannelHandler());
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