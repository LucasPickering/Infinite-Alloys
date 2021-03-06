package infinitealloys.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Random;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumMetal;
import infinitealloys.util.Funcs;

@Mod(modid = Consts.MOD_ID, name = "Infinite Alloys", version = "@VERSION@")
public final class InfiniteAlloys {

  @Mod.Instance(Consts.MOD_ID)
  public static InfiniteAlloys instance;
  @SidedProxy(clientSide = "infinitealloys.client.ClientProxy", serverSide = "infinitealloys.CommonProxy")
  public static CommonProxy proxy;
  public static boolean[] spawnOres = new boolean[Consts.METAL_COUNT];
  public static CreativeTabs creativeTab;
  private int[] validAlloys;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    config.load();
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      spawnOres[i] = config.get("World Gen", EnumMetal.values()[i].name, true).getBoolean(true);
    }
    config.save();
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {
    creativeTab = new CreativeTabIA(CreativeTabs.getNextID(), "main");
    proxy.initBlocks();
    proxy.initItems();
    proxy.initEntities();
    proxy.initRecipes();
    proxy.initTileEntities();
    proxy.initHandlers();
    proxy.initRendering();
  }

  public void saveAlloyData(NBTTagCompound nbtTagCompound) {
    nbtTagCompound.setIntArray("validAlloys", validAlloys);
  }

  public void loadAlloyData(NBTTagCompound nbtTagCompound) {
    validAlloys = nbtTagCompound.getIntArray("validAlloys");
  }

  /**
   * Generate new validAlloys. This should only be called when a world is first generated, and will
   * not do anything if validAlloys already has a value.
   */
  public void generateAlloyData() {
    if (validAlloys == null) {
      validAlloys = new int[Consts.VALID_ALLOY_COUNT];
      Random random = new Random();
      // For each alloy that needs to be generated
      for (int i = 0; i < Consts.VALID_ALLOY_COUNT; i++) {
        int alloy = 0; // An int to hold each digit that is generated
        // For each metal, i.e. for each digit in the alloy
        for (int j = 0; j < Consts.METAL_COUNT; j++) {
          // Metal's min value in the alloy
          int min = Funcs.intAtPos(EnumAlloy.values()[i].min, Consts.ALLOY_RADIX, j);
          // Metal's max value in the alloy
          int max = Funcs.intAtPos(EnumAlloy.values()[i].max, Consts.ALLOY_RADIX, j);
          // Randomly gen a value in [min, max] and add it to the alloy
          alloy += (min + random.nextInt(max - min + 1)) * Math.pow(Consts.ALLOY_RADIX, j);
        }

        validAlloys[i] = Funcs.reduceAlloy(alloy); // Add the new alloy to the array
      }
      System.out.println("Successfully generated IA alloys");
    }
  }

  public int[] getValidAlloys() {
    return validAlloys;
  }

  /**
   * Set validAlloys to the given value, but only if this is called client-side. This is only used
   * when syncing data from the server.
   */
  public void setValidAlloys(int[] validAlloys) {
    if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
      this.validAlloys = validAlloys;
    }
  }
}