package infinitealloys;

import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy {

	public void initBlocks() {
		InfiniteAlloys.ore = new BlockOre(InfiniteAlloys.oreID, 0).setBlockName("IAOre");
		InfiniteAlloys.machine = new BlockMachine(InfiniteAlloys.machineID, 6).setBlockName("IAMachine");
		GameRegistry.registerBlock(InfiniteAlloys.ore, ItemBlockIA.class);
		GameRegistry.registerBlock(InfiniteAlloys.machine, ItemBlockIA.class);
		for(int i = 0; i < IAValues.metalCount; i++)
			LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 0, i), IAValues.metalNames[i] + " Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "Computer");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 1), "Metal Forge");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 2), "Crafter");
	}

	public void initItems() {
		InfiniteAlloys.ingot = new ItemIngot(InfiniteAlloys.ingotID, 0).setItemName("IAIngot");
		InfiniteAlloys.alloyIngot = new ItemAlloyIngot(InfiniteAlloys.alloyIngotID, 0).setItemName("IAAlloyIngot");
		InfiniteAlloys.upgrade = new ItemUpgrade(InfiniteAlloys.upgradeID, 1).setItemName("IAUpgrade");
		for(int i = 0; i < IAValues.metalCount; i++)
			LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 0, i), IAValues.metalNames[i] + " Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.alloyIngot), "Alloy Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.upgrade), "Upgrade");
	}

	public void initRecipes() {
		FurnaceRecipes.smelting().addSmelting(InfiniteAlloys.ore.blockID, 0, new ItemStack(InfiniteAlloys.ingot, 1, 0));
		FurnaceRecipes.smelting().addSmelting(InfiniteAlloys.ore.blockID, 1, new ItemStack(InfiniteAlloys.ingot, 1, 1));
		FurnaceRecipes.smelting().addSmelting(InfiniteAlloys.ore.blockID, 2, new ItemStack(InfiniteAlloys.ingot, 1, 2));
		FurnaceRecipes.smelting().addSmelting(InfiniteAlloys.ore.blockID, 3, new ItemStack(InfiniteAlloys.ingot, 1, 3));
		FurnaceRecipes.smelting().addSmelting(InfiniteAlloys.ore.blockID, 4, new ItemStack(InfiniteAlloys.ingot, 1, 4));
		FurnaceRecipes.smelting().addSmelting(InfiniteAlloys.ore.blockID, 5, new ItemStack(InfiniteAlloys.ingot, 1, 5));
		FurnaceRecipes.smelting().addSmelting(InfiniteAlloys.ore.blockID, 6, new ItemStack(InfiniteAlloys.ingot, 1, 6));
		FurnaceRecipes.smelting().addSmelting(InfiniteAlloys.ore.blockID, 7, new ItemStack(InfiniteAlloys.ingot, 1, 7));
	}

	public void initTileEntities() {
		GameRegistry.registerTileEntity(TileEntityComputer.class, "Computer");
		GameRegistry.registerTileEntity(TileEntityMetalForge.class, "MetalForge");
	}

	public void initRendering() {
	}
}
