package infinitealloys;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = "InfiniteAlloys", name = "InfiniteAlloys", version = "0.0.0")
@NetworkMod(channels = { "InfiniteAlloys" }, clientSideRequired = true, serverSideRequired = false, packetHandler = CommonProxy.class)
public class InfiniteAlloys {

	@Instance("InfiniteAlloys")
	public static InfiniteAlloys instance;
	@SidedProxy(clientSide = "infinitealloys.client.ClientProxy", serverSide = "infinitealloys.CommonProxy")
	public static CommonProxy proxy;
	public static int oreCount = 8;
	public static int metalCount = 9;
	public static int machineCount = 3;
	public static int upgradeCount = 13;
	public static float[] densities = { 7.874F,8.96F, 7.365F, 2.7F, 1.738F, 7.14F, 4.506F, 16.69F, 1 };
	public static int[] validAlloyIngots = { 0, 1, 2, 3, 4, 5 };
	public static int alloyPossibilities = 16777208;
	public static int oreID;
	public static int machineID;
	public static int ingotID;
	public static int alloyIngotID;
	public static int upgradeID;
	public static Block ore;
	public static Block machine;
	public static Item ingot;
	public static Item alloyIngot;
	public static Item upgrade;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		oreID = config.getBlock("Ore", 200).getInt();
		machineID = config.getBlock("Machine", 201).getInt();
		ingotID = config.getItem("Ingot", Configuration.CATEGORY_ITEM, 14000).getInt();
		alloyIngotID = config.getItem("Alloy Ingot", Configuration.CATEGORY_ITEM, 14001).getInt();
		upgradeID = config.getItem("Upgrade", Configuration.CATEGORY_ITEM, 14002).getInt();
		config.save();
	}

	@Init
	public void load(FMLInitializationEvent event) {
		proxy.initBlocks();
		proxy.initItems();
		proxy.initTileEntities();
		proxy.initRendering();
		NetworkRegistry.instance().registerGuiHandler(instance, proxy);
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}

	public static int intAtPositionOctal(long l, int pos) {
		String octal = Long.toOctalString(l);
		int length=octal.length();
		for(int i = 0; i < metalCount - length; i++)
			octal = "0" + octal;
		return new Integer(String.valueOf(octal.charAt(pos)));
	}

	public static int log2(long l) {
		if(l <= 0) throw new IllegalArgumentException();
		return 63 - Long.numberOfLeadingZeros(l);
	}
}
