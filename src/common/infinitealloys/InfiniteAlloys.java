package infinitealloys;

import net.minecraft.src.Achievement;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "InfiniteAlloys", name = "Infinite Alloys", version = "0.0.2")
@NetworkMod(channels = { "InfiniteAlloys" }, clientSideRequired = true, serverSideRequired = false, packetHandler = infinitealloys.handlers.PacketHandler.class)
public class InfiniteAlloys {

	@Instance("InfiniteAlloys")
	public static InfiniteAlloys instance;
	@SidedProxy(clientSide = "infinitealloys.client.ClientProxy", serverSide = "infinitealloys.CommonProxy")
	public static CommonProxy proxy;
	public static int oreID;
	public static int machineID;
	public static int ingotID;
	public static int alloyIngotID;
	public static int upgradeID;
	public static int gpsID;
	public static int alloyBookID;
	public static boolean[] spawnOres = new boolean[References.metalCount];
	public static Block ore;
	public static Block machine;
	public static Item ingot;
	public static Item alloyIngot;
	public static Item upgrade;
	public static Item gps;
	public static Item alloyBook;
	public static CreativeTabs tabIA;
	public static Achievement craftMetalForge;
	public static Achievement smeltAlloy;
	public static AchievementPage achPage;
	public IAWorldData worldData;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		oreID = config.getBlock("Ore", 3000).getInt();
		machineID = config.getBlock("Machine", 3001).getInt();
		ingotID = config.getItem(Configuration.CATEGORY_ITEM, "Ingot", 15000).getInt();
		alloyIngotID = config.getItem(Configuration.CATEGORY_ITEM, "AlloyIngot", 15001).getInt();
		upgradeID = config.getItem(Configuration.CATEGORY_ITEM, "Upgrade", 15002).getInt();
		gpsID = config.getItem(Configuration.CATEGORY_ITEM, "GPS", 15003).getInt();
		alloyBookID = config.getItem(Configuration.CATEGORY_ITEM, "AlloyBook", 15004).getInt();
		int[] metalColors = { 0xc5763d, 0x858586, 0xd2cda3, 0xcde0ef, 0xae2305, 0x177c19, 0x141dce, 0x7800be };
		for(int i = 0; i < References.metalCount; i++)
			References.metalColors[i] = config.get("Metal Colors", References.metalNames[i], metalColors[i]).getInt();
		for(int i = 0; i < References.metalCount; i++)
			spawnOres[i] = config.get("World Gen", References.metalNames[i], true).getBoolean(true);
		config.save();
	}

	@Init
	public void load(FMLInitializationEvent event) {
		tabIA = new CreativeTabIA(CreativeTabs.getNextID(), "main");
		proxy.initLocalization();
		proxy.initBlocks();
		proxy.initItems();
		proxy.initRecipes();
		proxy.initTileEntities();
		proxy.initHandlers();
		proxy.initAchievements();
		proxy.initRendering();
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}

	public static int intAtPosRadix(int radix, int strlen, int n, int pos) {
		return new Integer(String.valueOf(addLeadingZeros(Integer.toString(n, radix), strlen).charAt(pos)));
	}

	public static double logn(int base, double num) {
		return Math.log(num) / Math.log(base);
	}

	public static String addLeadingZeros(String s, int finalSize) {
		int length = s.length();
		for(int i = 0; i < finalSize - length; i++)
			s = "0" + s;
		return s;
	}

	public static String getStringLocalization(String key) {
		return LanguageRegistry.instance().getStringLocalization(key);
	}
}
