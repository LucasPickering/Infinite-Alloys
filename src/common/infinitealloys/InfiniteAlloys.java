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

	public static int intAtPositionOctal(int strlen, int n, int pos) {
		String octal = Integer.toOctalString(n);
		int length = octal.length();
		for(int i = 0; i < strlen - length; i++)
			octal = "0" + octal;
		return new Integer(String.valueOf(octal.charAt(pos)));
	}

	public static double logn(int base, double num) {
		return Math.log(num) / Math.log(base);
	}
}
