package infinitealloys.core;

import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Random;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
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
import infinitealloys.util.EnumBoss;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.MachineHelper;

public class CommonProxy {

  public GfxHandler gfxHandler;

  public void initBlocks() {
    IABlocks.ore =
        new BlockOre().setHardness(3F).setCreativeTab(InfiniteAlloys.tabIA).setBlockName("iaOre");
    IABlocks.machine =
        new BlockMachine().setHardness(3F).setCreativeTab(InfiniteAlloys.tabIA).setBlockName(
            "iaMachine");

    GameRegistry.registerBlock(IABlocks.ore, ItemBlockOre.class, IABlocks.ore.getUnlocalizedName());
    GameRegistry.registerBlock(IABlocks.machine, ItemBlockMachine.class,
                               IABlocks.machine.getUnlocalizedName());

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      OreDictionary.registerOre("ore" + Consts.METAL_NAMES[i], new ItemStack(IABlocks.ore, 1, 0));
    }

    IABlocks.ore.setHarvestLevel("pickaxe", 1, 0);
    IABlocks.ore.setHarvestLevel("pickaxe", 1, 1);
    IABlocks.ore.setHarvestLevel("pickaxe", 1, 2);
    IABlocks.ore.setHarvestLevel("pickaxe", 1, 3);
    IABlocks.ore.setHarvestLevel("pickaxe", 2, 4);
    IABlocks.ore.setHarvestLevel("pickaxe", 2, 5);
    IABlocks.ore.setHarvestLevel("pickaxe", 2, 6);
    IABlocks.ore.setHarvestLevel("pickaxe", 3, 7);
  }

  public void initItems() {
    IAItems.multi = new ItemMulti().setUnlocalizedName("iaMulti");
    IAItems.ingot = new ItemIngot().setUnlocalizedName("iaIngot");
    IAItems.alloyIngot = new ItemAlloyIngot().setUnlocalizedName("iaAlloyIngot");
    IAItems.internetWand = new ItemInternetWand().setUnlocalizedName("iaInternetWand");
    IAItems.upgrades[Consts.SPEED] =
        (ItemUpgrade) new ItemUpgradeSpeed().setUnlocalizedName("iaUpgradeSpeed");
    IAItems.upgrades[Consts.EFFICIENCY] =
        (ItemUpgrade) new ItemUpgradeEfficiency().setUnlocalizedName("iaUpgradeEfficiency");
    IAItems.upgrades[Consts.CAPACITY] =
        (ItemUpgrade) new ItemUpgradeCapacity().setUnlocalizedName("iaUpgradeCapacity");
    IAItems.upgrades[Consts.RANGE] =
        (ItemUpgrade) new ItemUpgradeRange().setUnlocalizedName("iaUpgradeRange");
    IAItems.upgrades[Consts.WIRELESS] =
        (ItemUpgrade) new ItemUpgradeWireless().setUnlocalizedName("iaUpgradeWireless");
    IAItems.upgrades[Consts.ALLOY_UPG] =
        (ItemUpgrade) new ItemUpgradeAlloy().setUnlocalizedName("iaUpgradeAlloy");

    GameRegistry.registerItem(IAItems.multi, IAItems.multi.getUnlocalizedName());
    GameRegistry.registerItem(IAItems.ingot, IAItems.ingot.getUnlocalizedName());
    GameRegistry.registerItem(IAItems.alloyIngot, IAItems.alloyIngot.getUnlocalizedName());
    GameRegistry.registerItem(IAItems.internetWand, IAItems.internetWand.getUnlocalizedName());
    for (Item upgrade : IAItems.upgrades) {
      GameRegistry.registerItem(upgrade, upgrade.getUnlocalizedName());
    }

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      OreDictionary
          .registerOre("ingot" + Consts.METAL_NAMES[i], new ItemStack(IAItems.ingot, 1, i));
    }
  }

  public void initEntities() {
    for (EnumBoss bossType : EnumBoss.values()) {
      registerEntity(bossType.entityClass, bossType.name);
    }
  }

  public void initRecipes() {
    ItemStack machineComponent = new ItemStack(IAItems.multi, 1, 0);
    ItemStack upgradeComponent = new ItemStack(IAItems.multi, 1, 1);

    ItemStack[] alloys = new ItemStack[Consts.VALID_ALLOY_COUNT];
    for (int i = 0; i < alloys.length; i++) {
      alloys[i] = new ItemStack(IAItems.alloyIngot, 1, i + 1);
    }

    ItemStack[][]
        upgrades =
        new ItemStack[Consts.UPGRADE_TYPE_COUNT][6]; // The second index is the current max value for tiers of an upgrade
    for (int i = 0; i < upgrades.length; i++) {
      for (int j = 0; j < upgrades[i].length; j++) {
        upgrades[i][j] = new ItemStack(IAItems.upgrades[i], 1, j);
      }
    }

		/*---MACHINES---*/
                /* Computer */
    addRecipeDict(new ItemStack(IABlocks.machine), "W3G", "2M2", "R3R",
                  '2', alloys[2], '3', alloys[3], 'M', machineComponent, 'G', Blocks.glass_pane,
                  'R', Items.redstone, 'W', upgrades[Consts.WIRELESS][0]);

		/* Metal Forge */
    addRecipeDict(new ItemStack(IABlocks.machine, 1, 1), "BBB", "BMB", "BBB", 'B', Items.brick, 'M',
                  machineComponent);

		/* X-ray */
    addRecipeDict(new ItemStack(IABlocks.machine, 1, 2), "E5E", "4M4", "D5G",
                  '4', alloys[4], '5', alloys[5], 'M', machineComponent, 'D', Items.diamond, 'E',
                  Items.ender_pearl, 'G', Blocks.glass_pane);

		/* Pasture */
    addRecipeDict(new ItemStack(IABlocks.machine, 1, 3), "F4F", "3M3", "F4F", '3', alloys[3], '4',
                  alloys[4], 'M', machineComponent, 'F', Blocks.fence);

		/* ESU */
    addRecipeDict(new ItemStack(IABlocks.machine, 1, 4), "IAI", "CMC", "IAI", 'M', machineComponent,
                  'I', Items.iron_ingot, 'C', "ingotCopper", 'A', "ingotAluminium");

		/*---UPGRADES---*/
                /* Speed I */
    addRecipeDict(upgrades[Consts.SPEED][0], "0C0", "0U0", '0', alloys[0], 'C', Items.cookie, 'U',
                  upgradeComponent);

		/* Speed II */
    addRecipeDict(upgrades[Consts.SPEED][1], "2C2", "2U2", '2', alloys[2], 'D', Items.cake, 'U',
                  upgrades[Consts.SPEED][0]);

		/* Speed III */
    addRecipeDict(upgrades[Consts.SPEED][2], "4C4", "4U4", '4', alloys[4], 'D', Items.golden_carrot,
                  'U', upgrades[Consts.SPEED][1]);

		/* Efficiency I */
    addRecipeDict(upgrades[Consts.EFFICIENCY][0], "1S1", "1U1", '1', alloys[1], 'S',
                  Items.iron_shovel, 'U', upgradeComponent);

		/* Efficiency II */
    addRecipeDict(upgrades[Consts.EFFICIENCY][1], "3S3", "3U3", '3', alloys[3], 'S',
                  Items.golden_shovel, 'U', upgrades[Consts.EFFICIENCY][0]);

		/* Efficiency III */
    addRecipeDict(upgrades[Consts.EFFICIENCY][2], "5S5", "5U5", '5', alloys[5], 'S',
                  Items.diamond_shovel, 'U', upgrades[Consts.EFFICIENCY][1]);

		/* Capacity I */
    addRecipeDict(upgrades[Consts.CAPACITY][0], "0C0", "0U0", "III", '0', alloys[0], 'C',
                  Blocks.chest, 'I', Items.iron_ingot, 'U', upgradeComponent);

		/* Capacity II */
    addRecipeDict(upgrades[Consts.CAPACITY][1], "2C2", "2U2", "GGG", '2', alloys[2], 'C',
                  Blocks.chest, 'G', Items.gold_ingot, 'U', upgrades[Consts.CAPACITY][0]);

		/* Capacity III */
    addRecipeDict(upgrades[Consts.CAPACITY][2], "4C4", "4U4", "DDD", '4', alloys[4], 'C',
                  Blocks.chest, 'D', Items.diamond, 'U', upgrades[Consts.CAPACITY][1]);

		/* Range I */
    addRecipeDict(upgrades[Consts.RANGE][0], "1S1", "1U1", '1', alloys[1], 'S', Items.snowball, 'U',
                  upgradeComponent);

		/* Range II */
    addRecipeDict(upgrades[Consts.RANGE][1], "3B3", "3U3", '3', alloys[3], 'B', Items.blaze_rod,
                  'U', upgrades[Consts.RANGE][0]);

		/* Range III */
    addRecipeDict(upgrades[Consts.RANGE][2], "5E5", "5U5", '5', alloys[5], 'E', Items.ender_eye,
                  'U', upgrades[Consts.RANGE][1]);

		/* Wireless */
    addRecipeDict(upgrades[Consts.WIRELESS][0], "1E1", "1U1", '1', alloys[1], 'E',
                  Items.ender_pearl, 'U', upgradeComponent);

		/*---OTHER ITEMS---*/
		/* Internet Wand */
    addRecipeDict(new ItemStack(IAItems.internetWand), " W ", "RSR", 'R', Items.redstone, 'S',
                  Items.stick, 'W', upgrades[Consts.WIRELESS][0]);
		/* Machine Frame */
    addRecipeDict(new ItemStack(IAItems.multi, 1, 0), "CCC", "AAA", "MMM", 'C', "ingotCopper", 'A',
                  "ingotAluminium", 'M', "ingotMagnesium");
		/* Upgrade Component */
    addRecipeDict(new ItemStack(IAItems.multi, 1, 1), "CTC", "ATA", "A A", 'C', "ingotCopper", 'A',
                  "ingotAluminium", 'T', "ingotTantalum");

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      FurnaceRecipes.smelting()
          .func_151394_a(new ItemStack(IABlocks.ore, 1, i), new ItemStack(IAItems.ingot, 1, i),
                         0.6F);
    }
  }

  public void initTileEntities() {
    for (EnumMachine machine : EnumMachine.values()) {
      GameRegistry.registerTileEntity(machine.getTEMClass(), machine.getName());
    }

    MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.coal_ore), 0, 1);
    MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.iron_ore), 0, 2);
    MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.gold_ore), 0, 6);
    MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.diamond_ore), 0, 8);

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      MachineHelper.addDictDetectable("ore" + Consts.METAL_NAMES[i], i + 3);
    }

    MachineHelper.addDictDetectable("oreTin", 2);
    MachineHelper.addDictDetectable("oreAluminum", 3);
    MachineHelper.addDictDetectable("oreUranium", 8);
  }

  public void initHandlers() {
    NetworkHandler.init();
    gfxHandler = new GfxHandler();
    EventHandler eventHandler = new EventHandler();
    MinecraftForge.EVENT_BUS.register(eventHandler);
    GameRegistry.registerWorldGenerator(new WorldGenHandler(), 100);
    NetworkRegistry.INSTANCE.registerGuiHandler(InfiniteAlloys.instance, gfxHandler);
  }

  public void initRendering() {
  }

  private void addRecipeDict(ItemStack result, Object... params) {
    GameRegistry.addRecipe(new ShapedOreRecipe(result, params));
  }

  private void registerEntity(Class entityClass, String name) {
    int entityID = EntityRegistry.findGlobalUniqueEntityId();
    Random rand = new Random(name.hashCode());
    int primaryColor = rand.nextInt() * 0xffffff;
    int secondaryColor = rand.nextInt() * 0xffffff;

    EntityRegistry.registerGlobalEntityID(entityClass, name, entityID);
    EntityRegistry
        .registerModEntity(entityClass, name, entityID, InfiniteAlloys.instance, 64, 1, true);
    EntityList.entityEggs.put(Integer.valueOf(entityID),
                              new EntityList.EntityEggInfo(entityID, primaryColor, secondaryColor));
  }
}