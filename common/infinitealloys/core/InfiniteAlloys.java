package infinitealloys.core;

import infinitealloys.block.Blocks;
import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "InfiniteAlloys", name = "Infinite Alloys", version = "@VERSION@")
@NetworkMod(channels = { "InfiniteAlloys" }, clientSideRequired = true, serverSideRequired = false, packetHandler = infinitealloys.handlers.PacketHandler.class)
public class InfiniteAlloys {

	@Instance("InfiniteAlloys")
	public static InfiniteAlloys instance;
	@SidedProxy(clientSide = "infinitealloys.client.ClientProxy", serverSide = "infinitealloys.CommonProxy")
	public static CommonProxy proxy;
	public static boolean[] spawnOres = new boolean[Consts.METAL_COUNT];
	public static CreativeTabs tabIA;
	public static Achievement[] achievements = new Achievement[7];
	public static AchievementPage achPage;
	public WorldData worldData;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		Blocks.oreID = config.getBlock("Ore", 3000).getInt();
		Blocks.machineID = config.getBlock("Machine", 3001).getInt();
		Items.multiID = config.getItem(Configuration.CATEGORY_ITEM, "MultiItem", 15000).getInt();
		Items.ingotID = config.getItem(Configuration.CATEGORY_ITEM, "Ingot", 15001).getInt();
		Items.alloyIngotID = config.getItem(Configuration.CATEGORY_ITEM, "AlloyIngot", 15002).getInt();
		Items.upgradeID = config.getItem(Configuration.CATEGORY_ITEM, "Upgrade", 15003).getInt();
		Items.internetWandID = config.getItem(Configuration.CATEGORY_ITEM, "Internet Wand", 15004).getInt();
		Items.alloyBookID = config.getItem(Configuration.CATEGORY_ITEM, "AlloyBook", 15005).getInt();

		int[] metalColors = { 0x2a2a2a, 0xd2cda3, 0xccc34f, 0xcde0ef, 0xae2305, 0x177c19, 0x141dce, 0x7800be };
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
		proxy.initAchievements();
		proxy.initRendering();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}
}