package infinitealloys.core;

import net.minecraft.entity.Entity;
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
import infinitealloys.block.BlockIA;
import infinitealloys.block.BlockMachine;
import infinitealloys.block.BlockOre;
import infinitealloys.item.ItemAlloyIngot;
import infinitealloys.item.ItemBlockMachine;
import infinitealloys.item.ItemBlockOre;
import infinitealloys.item.ItemIA;
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
import infinitealloys.util.EnumMetal;
import infinitealloys.util.MachineHelper;

public class CommonProxy {

  public GfxHandler gfxHandler;

  public void initBlocks() {
    BlockIA.ore = new BlockOre().setHardness(3F).setBlockName("ore");
    BlockIA.machine = new BlockMachine().setHardness(3F).setBlockName("machine");

    GameRegistry.registerBlock(BlockIA.ore, ItemBlockOre.class, BlockIA.ore.getUnlocalizedName());
    GameRegistry.registerBlock(BlockIA.machine, ItemBlockMachine.class,
                               BlockIA.machine.getUnlocalizedName());

    for (EnumMetal metal : EnumMetal.values()) {
      OreDictionary.registerOre("ore" + metal.name, new ItemStack(BlockIA.ore, 1, 0));
    }

    BlockIA.ore.setHarvestLevel("pickaxe", 1, 0);
    BlockIA.ore.setHarvestLevel("pickaxe", 1, 1);
    BlockIA.ore.setHarvestLevel("pickaxe", 1, 2);
    BlockIA.ore.setHarvestLevel("pickaxe", 1, 3);
    BlockIA.ore.setHarvestLevel("pickaxe", 2, 4);
    BlockIA.ore.setHarvestLevel("pickaxe", 2, 5);
    BlockIA.ore.setHarvestLevel("pickaxe", 2, 6);
    BlockIA.ore.setHarvestLevel("pickaxe", 3, 7);
  }

  public void initItems() {
    ItemIA.multi = new ItemMulti().setUnlocalizedName("multi");
    ItemIA.ingot = new ItemIngot().setUnlocalizedName("ingot");
    ItemIA.alloyIngot = new ItemAlloyIngot().setUnlocalizedName("alloyIngot");
    ItemIA.internetWand = new ItemInternetWand().setUnlocalizedName("internetWand");
    ItemIA.upgrades[Consts.SPEED] =
        (ItemUpgrade) new ItemUpgradeSpeed().setUnlocalizedName("upgradeSpeed");
    ItemIA.upgrades[Consts.EFFICIENCY] =
        (ItemUpgrade) new ItemUpgradeEfficiency().setUnlocalizedName("upgradeEfficiency");
    ItemIA.upgrades[Consts.CAPACITY] =
        (ItemUpgrade) new ItemUpgradeCapacity().setUnlocalizedName("upgradeCapacity");
    ItemIA.upgrades[Consts.RANGE] =
        (ItemUpgrade) new ItemUpgradeRange().setUnlocalizedName("upgradeRange");
    ItemIA.upgrades[Consts.WIRELESS] =
        (ItemUpgrade) new ItemUpgradeWireless().setUnlocalizedName("upgradeWireless");
    ItemIA.upgrades[Consts.ALLOY_UPG] =
        (ItemUpgrade) new ItemUpgradeAlloy().setUnlocalizedName("upgradeAlloy");

    GameRegistry.registerItem(ItemIA.multi, ItemIA.multi.getUnlocalizedName());
    GameRegistry.registerItem(ItemIA.ingot, ItemIA.ingot.getUnlocalizedName());
    GameRegistry.registerItem(ItemIA.alloyIngot, ItemIA.alloyIngot.getUnlocalizedName());
    GameRegistry.registerItem(ItemIA.internetWand, ItemIA.internetWand.getUnlocalizedName());
    for (Item upgrade : ItemIA.upgrades) {
      GameRegistry.registerItem(upgrade, upgrade.getUnlocalizedName());
    }

    for (EnumMetal metal : EnumMetal.values()) {
      OreDictionary.registerOre("ingot" + metal.name,
                                new ItemStack(ItemIA.ingot, 1, metal.ordinal()));
    }
  }

  public void initEntities() {
    for (EnumBoss bossType : EnumBoss.values()) {
      registerEntity(bossType.entityClass, bossType.name);
    }
  }

  public void initRecipes() {
    final ItemStack machineComponent = new ItemStack(ItemIA.multi, 1, 0);
    final ItemStack upgradeComponent = new ItemStack(ItemIA.multi, 1, 1);

    ItemStack[] alloys = new ItemStack[Consts.VALID_ALLOY_COUNT];
    for (int i = 0; i < alloys.length; i++) {
      alloys[i] = new ItemStack(ItemIA.alloyIngot, 1, i + 1);
    }

    ItemStack[][]
        upgrades =
        new ItemStack[Consts.UPGRADE_TYPE_COUNT][6]; // The second index is the current max value for tiers of an upgrade
    for (int i = 0; i < upgrades.length; i++) {
      for (int j = 0; j < upgrades[i].length; j++) {
        upgrades[i][j] = new ItemStack(ItemIA.upgrades[i], 1, j);
      }
    }

		/*---MACHINES---*/
                /* Computer */
    addRecipeDict(new ItemStack(BlockIA.machine), "W3G", "2M2", "R3R",
                  '2', alloys[2], '3', alloys[3], 'M', machineComponent, 'G', Blocks.glass_pane,
                  'R', Items.redstone, 'W', upgrades[Consts.WIRELESS][0]);

		/* Metal Forge */
    addRecipeDict(new ItemStack(BlockIA.machine, 1, 1), "BBB", "BMB", "BBB",
                  'B', Items.brick, 'M', machineComponent);

		/* X-ray */
    addRecipeDict(new ItemStack(BlockIA.machine, 1, 2), "E5E", "4M4", "D5G",
                  '4', alloys[4], '5', alloys[5], 'M', machineComponent, 'D', Items.diamond,
                  'E', Items.ender_pearl, 'G', Blocks.glass_pane);

		/* Pasture */
    addRecipeDict(new ItemStack(BlockIA.machine, 1, 3), "F4F", "3M3", "F4F",
                  '3', alloys[3], '4', alloys[4], 'M', machineComponent, 'F', Blocks.fence);

		/* ESU */
    addRecipeDict(new ItemStack(BlockIA.machine, 1, 4), "IAI", "CMC", "IAI",
                  'M', machineComponent, 'I', Items.iron_ingot,
                  'C', "ingotCopper", 'A', "ingotAluminium");


		/*---UPGRADES---*/
                /* Speed I */
    addRecipeDict(upgrades[Consts.SPEED][0], "0C0", "0U0",
                  '0', alloys[0], 'C', Items.cookie, 'U', upgradeComponent);

		/* Speed II */
    addRecipeDict(upgrades[Consts.SPEED][1], "2C2", "2U2",
                  '2', alloys[2], 'D', Items.cake, 'U', upgrades[Consts.SPEED][0]);

		/* Speed III */
    addRecipeDict(upgrades[Consts.SPEED][2], "4C4", "4U4",
                  '4', alloys[4], 'D', Items.golden_carrot, 'U', upgrades[Consts.SPEED][1]);

		/* Efficiency I */
    addRecipeDict(upgrades[Consts.EFFICIENCY][0], "1S1", "1U1",
                  '1', alloys[1], 'S',
                  Items.iron_shovel, 'U', upgradeComponent);

		/* Efficiency II */
    addRecipeDict(upgrades[Consts.EFFICIENCY][1], "3S3", "3U3",
                  '3', alloys[3], 'S',
                  Items.golden_shovel, 'U', upgrades[Consts.EFFICIENCY][0]);

		/* Efficiency III */
    addRecipeDict(upgrades[Consts.EFFICIENCY][2], "5S5", "5U5",
                  '5', alloys[5], 'S', Items.diamond_shovel, 'U', upgrades[Consts.EFFICIENCY][1]);

		/* Capacity I */
    addRecipeDict(upgrades[Consts.CAPACITY][0], "0C0", "0U0", "III",
                  '0', alloys[0], 'C', Blocks.chest, 'I', Items.iron_ingot,
                  'U', upgradeComponent);

		/* Capacity II */
    addRecipeDict(upgrades[Consts.CAPACITY][1], "2C2", "2U2", "GGG",
                  '2', alloys[2], 'C', Blocks.chest, 'G', Items.gold_ingot,
                  'U', upgrades[Consts.CAPACITY][0]);

		/* Capacity III */
    addRecipeDict(upgrades[Consts.CAPACITY][2], "4C4", "4U4", "DDD",
                  '4', alloys[4], 'C', Blocks.chest, 'D', Items.diamond,
                  'U', upgrades[Consts.CAPACITY][1]);

		/* Range I */
    addRecipeDict(upgrades[Consts.RANGE][0], "1S1", "1U1",
                  '1', alloys[1], 'S', Items.snowball, 'U', upgradeComponent);

		/* Range II */
    addRecipeDict(upgrades[Consts.RANGE][1], "3B3", "3U3",
                  '3', alloys[3], 'B', Items.blaze_rod, 'U', upgrades[Consts.RANGE][0]);

		/* Range III */
    addRecipeDict(upgrades[Consts.RANGE][2], "5E5", "5U5",
                  '5', alloys[5], 'E', Items.ender_eye, 'U', upgrades[Consts.RANGE][1]);

		/* Wireless */
    addRecipeDict(upgrades[Consts.WIRELESS][0], "1E1", "1U1",
                  '1', alloys[1], 'E', Items.ender_pearl, 'U', upgradeComponent);


		/*---OTHER ITEMS---*/
                /* Internet Wand */
    addRecipeDict(new ItemStack(ItemIA.internetWand), " W ", "RSR",
                  'R', Items.redstone, 'S', Items.stick, 'W', upgrades[Consts.WIRELESS][0]);

		/* Machine Frame */
    addRecipeDict(new ItemStack(ItemIA.multi, 1, 0), "CCC", "AAA", "MMM",
                  'C', "ingotCopper", 'A', "ingotAluminium", 'M', "ingotMagnesium");

		/* Upgrade Component */
    addRecipeDict(new ItemStack(ItemIA.multi, 1, 1), "CTC", "ATA", "A A",
                  'C', "ingotCopper", 'A', "ingotAluminium", 'T', "ingotTantalum");

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      FurnaceRecipes.smelting().func_151394_a(new ItemStack(BlockIA.ore, 1, i),
                                              new ItemStack(ItemIA.ingot, 1, i),
                                              0.6F);
    }
  }

  public void initTileEntities() {
    for (EnumMachine machine : EnumMachine.values()) {
      GameRegistry.registerTileEntity(machine.temClass, machine.name);
    }

    MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.coal_ore), 0, 1);
    MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.iron_ore), 0, 2);
    MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.gold_ore), 0, 6);
    MachineHelper.addDetectable(Item.getItemFromBlock(Blocks.diamond_ore), 0, 8);

    for (EnumMetal metal : EnumMetal.values()) {
      MachineHelper.addDictDetectable("ore" + metal.name, metal.ordinal() + 3);
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

  private void registerEntity(Class<? extends Entity> entityClass, String name) {
    int entityID = EntityRegistry.findGlobalUniqueEntityId();
    Random rand = new Random(name.hashCode());
    int primaryColor = rand.nextInt() * 0xffffff;
    int secondaryColor = rand.nextInt() * 0xffffff;

    EntityRegistry.registerGlobalEntityID(entityClass, name, entityID);
    EntityRegistry.registerModEntity(entityClass, name, entityID,
                                     InfiniteAlloys.instance, 64, 1, true);
    EntityList.entityEggs.put(entityID, new EntityList.EntityEggInfo(entityID,
                                                                     primaryColor, secondaryColor));
  }
}