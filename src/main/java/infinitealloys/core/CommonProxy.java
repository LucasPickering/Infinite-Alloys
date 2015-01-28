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
import infinitealloys.network.NetworkHandler;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumBoss;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumMetal;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.MachineHelper;

public class CommonProxy {

  public GfxHandler gfxHandler;

  public void initBlocks() {
    IABlocks.ore =
        new BlockOre().setHardness(3F).setCreativeTab(InfiniteAlloys.tabIA).setBlockName("ore");
    IABlocks.machine = new BlockMachine().setHardness(3F)
        .setCreativeTab(InfiniteAlloys.tabIA).setBlockName("machine");

    GameRegistry.registerBlock(IABlocks.ore, ItemBlockOre.class, IABlocks.ore.getUnlocalizedName());
    GameRegistry.registerBlock(IABlocks.machine, ItemBlockMachine.class,
                               IABlocks.machine.getUnlocalizedName());

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      OreDictionary
          .registerOre("ore" + EnumMetal.values()[i].name, new ItemStack(IABlocks.ore, 1, i));
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
    IAItems.multi = new ItemMulti().setUnlocalizedName("multi");
    IAItems.ingot = new ItemIngot().setUnlocalizedName("ingot");
    IAItems.alloyIngot = new ItemAlloyIngot().setUnlocalizedName("alloyIngot");
    IAItems.internetWand = new ItemInternetWand().setUnlocalizedName("internetWand");
    for (EnumUpgrade upgradeType : EnumUpgrade.values()) {
      IAItems.upgrades[upgradeType.ordinal()] = upgradeType.getItem();
    }

    GameRegistry.registerItem(IAItems.multi, IAItems.multi.getUnlocalizedName());
    GameRegistry.registerItem(IAItems.ingot, IAItems.ingot.getUnlocalizedName());
    GameRegistry.registerItem(IAItems.alloyIngot, IAItems.alloyIngot.getUnlocalizedName());
    GameRegistry.registerItem(IAItems.internetWand, IAItems.internetWand.getUnlocalizedName());
    for (Item upgrade : IAItems.upgrades) {
      GameRegistry.registerItem(upgrade, upgrade.getUnlocalizedName());
    }

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      OreDictionary
          .registerOre("ingot" + EnumMetal.values()[i].name, new ItemStack(IAItems.ingot, 1, i));
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

		/*---MACHINES---*/
                /* Computer */
    addRecipeDict(new ItemStack(IABlocks.machine), "W3G", "2M2", "R3R",
                  '2', alloys[2], '3', alloys[3], 'M', machineComponent, 'G', Blocks.glass_pane,
                  'R', Items.redstone, 'W', EnumUpgrade.WIRELESS.getItemStackForTier(1));

		/* Metal Forge */
    addRecipeDict(new ItemStack(IABlocks.machine, 1, 1), "BBB", "BMB", "BBB",
                  'B', Items.brick, 'M', machineComponent);

		/* X-ray */
    addRecipeDict(new ItemStack(IABlocks.machine, 1, 2), "E5E", "4M4", "D5G",
                  '4', alloys[4], '5', alloys[5], 'M', machineComponent, 'D', Items.diamond, 'E',
                  Items.ender_pearl, 'G', Blocks.glass_pane);

		/* Pasture */
    addRecipeDict(new ItemStack(IABlocks.machine, 1, 3), "F4F", "3M3", "F4F",
                  '3', alloys[3], '4', alloys[4], 'M', machineComponent, 'F', Blocks.fence);

		/* ESU */
    addRecipeDict(new ItemStack(IABlocks.machine, 1, 4), "IAI", "CMC", "IAI",
                  'M', machineComponent, 'I', Items.iron_ingot,
                  'C', "ingotCopper", 'A', "ingotAluminium");

		/*---UPGRADES---*/
                /* Speed I */
    addRecipeDict(EnumUpgrade.SPEED.getItemStackForTier(1), "0C0", "0U0",
                  '0', alloys[0], 'C', Items.cookie, 'U', upgradeComponent);

		/* Speed II */
    addRecipeDict(EnumUpgrade.SPEED.getItemStackForTier(2), "2C2", "2U2",
                  '2', alloys[2], 'D', Items.cake, 'U', EnumUpgrade.SPEED.getItemStackForTier(1));

		/* Speed III */
    addRecipeDict(EnumUpgrade.SPEED.getItemStackForTier(3), "4C4", "4U4",
                  '4', alloys[4], 'D', Items.golden_carrot,
                  'U', EnumUpgrade.SPEED.getItemStackForTier(2));

		/* Efficiency I */
    addRecipeDict(EnumUpgrade.EFFICIENCY.getItemStackForTier(1), "1S1", "1U1",
                  '1', alloys[1], 'S', Items.iron_shovel, 'U', upgradeComponent);

		/* Efficiency II */
    addRecipeDict(EnumUpgrade.EFFICIENCY.getItemStackForTier(2), "3S3", "3U3",
                  '3', alloys[3], 'S', Items.golden_shovel,
                  'U', EnumUpgrade.EFFICIENCY.getItemStackForTier(1));

		/* Efficiency III */
    addRecipeDict(EnumUpgrade.EFFICIENCY.getItemStackForTier(3), "5S5", "5U5",
                  '5', alloys[5], 'S', Items.diamond_shovel,
                  'U', EnumUpgrade.EFFICIENCY.getItemStackForTier(2));

		/* Capacity I */
    addRecipeDict(EnumUpgrade.CAPACITY.getItemStackForTier(1), "0C0", "0U0", "III",
                  '0', alloys[0], 'C', Blocks.chest, 'I', Items.iron_ingot, 'U', upgradeComponent);

		/* Capacity II */
    addRecipeDict(EnumUpgrade.CAPACITY.getItemStackForTier(2), "2C2", "2U2", "GGG",
                  '2', alloys[2], 'C', Blocks.chest, 'G', Items.gold_ingot,
                  'U', EnumUpgrade.CAPACITY.getItemStackForTier(1));

		/* Capacity III */
    addRecipeDict(EnumUpgrade.CAPACITY.getItemStackForTier(3), "4C4", "4U4", "DDD",
                  '4', alloys[4], 'C', Blocks.chest, 'D', Items.diamond,
                  'U', EnumUpgrade.CAPACITY.getItemStackForTier(2));

		/* Range I */
    addRecipeDict(EnumUpgrade.RANGE.getItemStackForTier(1), "1S1", "1U1",
                  '1', alloys[1], 'S', Items.snowball, 'U', upgradeComponent);

		/* Range II */
    addRecipeDict(EnumUpgrade.RANGE.getItemStackForTier(2), "3B3", "3U3",
                  '3', alloys[3], 'B', Items.blaze_rod,
                  'U', EnumUpgrade.RANGE.getItemStackForTier(1));

		/* Range III */
    addRecipeDict(EnumUpgrade.RANGE.getItemStackForTier(3), "5E5", "5U5",
                  '5', alloys[5], 'E', Items.ender_eye,
                  'U', EnumUpgrade.RANGE.getItemStackForTier(2));

		/* Wireless */
    addRecipeDict(EnumUpgrade.WIRELESS.getItemStackForTier(1), "1E1", "1U1",
                  '1', alloys[1], 'E', Items.ender_pearl, 'U', upgradeComponent);

		/*---OTHER ITEMS---*/
                /* Internet Wand */
    addRecipeDict(new ItemStack(IAItems.internetWand), " W ", "RSR",
                  'R', Items.redstone, 'S', Items.stick,
                  'W', EnumUpgrade.WIRELESS.getItemStackForTier(1));
                /* Machine Frame */
    addRecipeDict(new ItemStack(IAItems.multi, 1, 0), "CCC", "AAA", "MMM",
                  'C', "ingotCopper", 'A', "ingotAluminium", 'M', "ingotMagnesium");
                /* Upgrade Component */
    addRecipeDict(new ItemStack(IAItems.multi, 1, 1), "CTC", "ATA", "A A",
                  'C', "ingotCopper", 'A', "ingotAluminium", 'T', "ingotTantalum");

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      FurnaceRecipes.smelting()
          .func_151394_a(new ItemStack(IABlocks.ore, 1, i), new ItemStack(IAItems.ingot, 1, i),
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

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      MachineHelper.addDictDetectable("ore" + EnumMetal.values()[i].name, i + 3);
    }

    MachineHelper.addDictDetectable("oreTin", 2);
    MachineHelper.addDictDetectable("oreAluminum", 3);
    MachineHelper.addDictDetectable("oreUranium", 8);
  }

  public void initHandlers() {
    NetworkHandler.init();
    gfxHandler = new GfxHandler();
    MinecraftForge.EVENT_BUS.register(new EventHandler());
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
    EntityRegistry
        .registerModEntity(entityClass, name, entityID, InfiniteAlloys.instance, 64, 1, true);
    EntityList.entityEggs.put(entityID,
                              new EntityList.EntityEggInfo(entityID, primaryColor, secondaryColor));
  }
}