package infinitealloys.core;

import infinitealloys.block.IABlocks;
import infinitealloys.item.IAItems;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.Funcs;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "infinitealloys", name = "Infinite Alloys", version = "@VERSION@")
public class InfiniteAlloys {

	@Instance("InfiniteAlloys")
	public static InfiniteAlloys instance;
	@SidedProxy(clientSide = "infinitealloys.client.ClientProxy", serverSide = "infinitealloys.CommonProxy")
	public static CommonProxy proxy;
	public static boolean[] spawnOres = new boolean[Consts.METAL_COUNT];
	public static CreativeTabs tabIA;
	private int[] validAlloys;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		final int[] metalColors = { 0x2a2a2a, 0xd2cda3, 0xccc34f, 0xcde0ef, 0xae2305, 0x177c19, 0x141dce, 0x7800be };
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			Consts.metalColors[i] = config.get("Metal Colors", Consts.METAL_NAMES[i], metalColors[i]).getInt();
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			spawnOres[i] = config.get("World Gen", Consts.METAL_NAMES[i], true).getBoolean(true);
		config.save();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		tabIA = new CreativeTabIA(CreativeTabs.getNextID(), "main");
		proxy.initLocalization();
		proxy.initBlocks();
		proxy.initItems();
		proxy.initRecipes();
		proxy.initTileEntities();
		proxy.initHandlers();
		proxy.initRendering();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}

	public void saveAlloyData(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setIntArray("validAlloys", validAlloys);
	}

	public void loadAlloyData(NBTTagCompound nbtTagCompound) {
		validAlloys = nbtTagCompound.getIntArray("validAlloys");
	}

	/** Generate new validAlloys. This should only be called when a world is first generated, and will not do anything if validAlloys already has a value. */
	public void generateAlloyData() {
		if(validAlloys == null) {
			validAlloys = new int[Consts.VALID_ALLOY_COUNT];
			Random random = new Random();
			for(int i = 0; i < Consts.VALID_ALLOY_COUNT; i++) { // For each alloy that needs to be generated
				int alloy = 0; // An int to hold each digit that is generated
				for(int j = 0; j < Consts.METAL_COUNT; j++) { // For each metal, i.e. for each digit in the alloy
					int min = Funcs.intAtPos(EnumAlloy.values()[i].min, Consts.ALLOY_RADIX, j); // Metal's min value in the alloy
					int max = Funcs.intAtPos(EnumAlloy.values()[i].max, Consts.ALLOY_RADIX, j); // Metal's max value in the alloy
					// Randomly gen a value in [min, max] and add it to the alloy
					alloy += (min + (max == min ? 0 : random.nextInt(max - min + 1))) * Math.pow(Consts.ALLOY_RADIX, j);
				}

				validAlloys[i] = Funcs.reduceAlloy(alloy); // Add the new alloy to the array
				if(Loader.isModLoaded("mcp"))
					System.out.println("SPOILER ALERT! Alloy " + i + ": " + validAlloys[i]); // Debug line, only runs in MCP
			}
			System.out.println("Successfully generated IA alloys");
		}
	}

	public int[] getValidAlloys() {
		return validAlloys;
	}

	/** Set validAlloys to the given value, but only if this is called client-side. This is only used when syncing data from the server. */
	public void setValidAlloys(int[] validAlloys) {
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			this.validAlloys = validAlloys;
	}
}