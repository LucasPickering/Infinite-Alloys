package infinitealloys;

import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy {

	public void initBlocks() {
		InfiniteAlloys.ore = new BlockOre(InfiniteAlloys.oreID, 0).setBlockName("IAOre");
		InfiniteAlloys.machine = new BlockMachine(InfiniteAlloys.machineID, 6).setBlockName("IAMachine");
		GameRegistry.registerBlock(InfiniteAlloys.ore, ItemBlockIA.class);
		GameRegistry.registerBlock(InfiniteAlloys.machine, ItemBlockIA.class);
		for(int i = 0; i < IAValues.oreCount; i++)
			LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 0, i), IAValues.metalNames[i + 1] + " Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "Computer");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 1), "Metal Forge");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 2), "Crafter");
	}

	public void initItems() {
		InfiniteAlloys.ingot = new ItemIngot(InfiniteAlloys.ingotID, 0).setItemName("IAIngot");
		InfiniteAlloys.alloyIngot = new ItemAlloyIngot(InfiniteAlloys.alloyIngotID, 0).setItemName("IAAlloyIngot");
		InfiniteAlloys.upgrade = new ItemUpgrade(InfiniteAlloys.upgradeID, 1).setItemName("IAUpgrade");
		for(int i = 0; i < IAValues.oreCount; i++)
			LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 0, i), IAValues.metalNames[i + 1] + " Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.alloyIngot), "Alloy Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.upgrade), "Upgrade");
	}

	public void initTileEntities() {
		GameRegistry.registerTileEntity(TileEntityComputer.class, "Computer");
		GameRegistry.registerTileEntity(TileEntityMetalForge.class, "MetalForge");
	}

	public void initRendering() {
	}
}
