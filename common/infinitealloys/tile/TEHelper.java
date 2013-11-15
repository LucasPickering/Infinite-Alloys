package infinitealloys.tile;

import infinitealloys.client.gui.GuiAnalyzer;
import infinitealloys.client.gui.GuiComputer;
import infinitealloys.client.gui.GuiEnergyStorage;
import infinitealloys.client.gui.GuiGenerator;
import infinitealloys.client.gui.GuiMetalForge;
import infinitealloys.client.gui.GuiPasture;
import infinitealloys.client.gui.GuiPrinter;
import infinitealloys.client.gui.GuiUpgradable;
import infinitealloys.client.gui.GuiXray;
import infinitealloys.inventory.ContainerAnalyzer;
import infinitealloys.inventory.ContainerGenerator;
import infinitealloys.inventory.ContainerMetalForge;
import infinitealloys.inventory.ContainerPrinter;
import infinitealloys.inventory.ContainerUpgradable;
import infinitealloys.inventory.ContainerXray;
import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.oredict.OreDictionary;

public class TEHelper {

	public static final int COMPUTER = 0;
	public static final int METAL_FORGE = 1;
	public static final int ANALYZER = 2;
	public static final int PRINTER = 3;
	public static final int XRAY = 4;
	public static final int PASTURE = 5;
	public static final int ENERGY_STORAGE = 6;
	public static final int GENERATOR = 7;

	public static final int SPEED1 = 1;
	public static final int SPEED2 = 2;
	public static final int EFFICIENCY1 = 4;
	public static final int EFFICIENCY2 = 8;
	public static final int CAPACITY1 = 16;
	public static final int CAPACITY2 = 32;
	public static final int RANGE1 = 64;
	public static final int RANGE2 = 128;
	public static final int WIRELESS = 256;

	/** The TileEntityUpgradable class for each TEU block */
	public static final Class[] TEU_CLASSES = { TEUComputer.class, TEMMetalForge.class, TEMAnalyzer.class, TEMPrinter.class, TEMXray.class, TEMPasture.class,
			TEUEnergyStorage.class, TEMGenerator.class };

	public static final String[] TEU_NAMES = { "computer", "metalforge", "analyzer", "printer", "xray", "pasture", "energystorage", "generator" };

	/** How many blocks are searched per tick. Used to limit lag. Currently only used by the x-ray. */
	public static final int SEARCH_PER_TICK = 2000;

	/** The controlling computer for each player */
	public static HashMap<String, Point> controllers = new HashMap<String, Point>();

	/** A list of upgrades that are prerequisites for other upgrades */
	public static ArrayList<Integer> prereqUpgrades = new ArrayList<Integer>();

	/** A list of upgrades that require other upgrades to work */
	public static ArrayList<Integer> prereqNeedingUpgrades = new ArrayList<Integer>();

	/** The blocks that the x-ray can detect and their worths */
	private static HashMap<String, Integer> detectables = new HashMap<String, Integer>();

	static {
		prereqUpgrades.add(SPEED1);
		prereqUpgrades.add(EFFICIENCY1);
		prereqUpgrades.add(CAPACITY1);
		prereqUpgrades.add(RANGE1);
		prereqNeedingUpgrades.add(SPEED2);
		prereqNeedingUpgrades.add(EFFICIENCY2);
		prereqNeedingUpgrades.add(CAPACITY2);
		prereqNeedingUpgrades.add(RANGE2);
	}

	/** Add a block to the list of blocks that can be detected by the x-ray
	 * 
	 * @param block the block to be added to the list with a metadata of 0
	 * @param worth the amount the block is worth, higher worth requires more energy to detect */
	public static void addDetectable(Block block, int worth) {
		addDetectable(block, 0, worth);
	}

	/** Add a block with metadata to the list of blocks that can be detected by the x-ray
	 * 
	 * @param block the block to be added to the list
	 * @param metadata the metadata of the block to be added to the list
	 * @param worth the amount the block is worth, higher worth requires more energy to detect */
	public static void addDetectable(Block block, int metadata, int worth) {
		detectables.put(block.blockID + "@" + metadata, worth);
	}

	/** Add a block or blocks to the list of blocks that can be detected by the x-ray with an ore dictionary string
	 * 
	 * @param dictName the ore dictionary string from which the block(s) is/are retrieved
	 * @param worth the amount the block(s) is/are worth, higher worth requires more energy to detect */
	public static void addDictDetectables(String dictName, int worth) {
		for(ItemStack block : OreDictionary.getOres(dictName))
			detectables.put(block.itemID + "@" + block.getItemDamage(), worth);
	}

	public static boolean isDetectable(ItemStack stack) {
		return detectables.containsKey(stack.itemID + "@" + stack.getItemDamage());
	}

	public static int getDetectableWorth(int id, int metadata) {
		return detectables.get(id + "@" + metadata);
	}

	public static int getDetectableWorth(ItemStack stack) {
		return detectables.get(stack.itemID + "@" + stack.getItemDamage());
	}

	public static boolean isAlloyBook(ItemStack stack) {
		return stack.itemID == Items.alloyBook.itemID && stack.hasTagCompound();
	}

	public static boolean isBook(ItemStack stack) {
		return (stack.itemID == Items.alloyBook.itemID || stack.itemID == Item.writableBook.itemID || stack.itemID == Item.writtenBook.itemID) && stack.hasTagCompound();
	}

	/** Is the upgrade a prerequisite for another
	 * 
	 * @param upgrade
	 * @return true if it is a prereq */
	public static boolean isPrereqUpgrade(int upg) {
		return TEHelper.prereqUpgrades.contains(upg);
	}

	/** Does the upgrade require another to work?
	 * 
	 * @param upgrade
	 * @return true if it has a prereq */
	public static boolean hasPrereqUpgrade(int upg) {
		return TEHelper.prereqNeedingUpgrades.contains(upg);
	}

	public static int getIngotNum(ItemStack ingot) {
		if(ingot.itemID == Items.ingot.itemID && ingot.getItemDamage() < Consts.METAL_COUNT)
			return ingot.getItemDamage();
		return -1;
	}

	/** Get a Container instance for a specific TileEntityUpgradable
	 * 
	 * @param teuID The numerical ID for the TEU, see the TEU ID constants in this class
	 * @param inventoryPlayer An instance of InventoryPlayer (not used, just passed through)
	 * @param teu A TileEntityUpgradable instance to be casted and passed to the Container constructor
	 * @return A new instance of a Container class that extends ContainerUpgradable */
	public static ContainerUpgradable getContainerForTEU(int teuID, InventoryPlayer inventoryPlayer, TileEntityUpgradable teu) {
		switch(teuID) {
			case COMPUTER:
				return new ContainerUpgradable(inventoryPlayer, teu, 8, 84, 140, 43);
			case METAL_FORGE:
				return new ContainerMetalForge(inventoryPlayer, (TEMMetalForge)teu);
			case ANALYZER:
				return new ContainerAnalyzer(inventoryPlayer, (TEMAnalyzer)teu);
			case PRINTER:
				return new ContainerPrinter(inventoryPlayer, (TEMPrinter)teu);
			case XRAY:
				return new ContainerXray(inventoryPlayer, (TEMXray)teu);
			case PASTURE:
				return new ContainerUpgradable(inventoryPlayer, teu, 13, 94, 141, 44);
			case ENERGY_STORAGE:
				return new ContainerUpgradable(inventoryPlayer, teu, 8, 84, 140, 43);
			case GENERATOR:
				return new ContainerGenerator(inventoryPlayer, (TEMGenerator)teu);
		}
		return null;
	}

	/** Get a GUI instance for a specific TileEntityUpgradable
	 * 
	 * @param teuID The numerical ID for the TEU, see the TEU ID constants in this class
	 * @param inventoryPlayer An instance of InventoryPlayer (not used, just passed through)
	 * @param teu A TileEntityUpgradable instance to be casted and passed to the GUI constructor
	 * @return A new instance of a GUI class that extends GuiUpgradable */
	public static GuiUpgradable getGuiForTEU(int teuID, InventoryPlayer inventoryPlayer, TileEntityUpgradable teu) {
		switch(teuID) {
			case COMPUTER:
				return new GuiComputer(inventoryPlayer, (TEUComputer)teu);
			case METAL_FORGE:
				return new GuiMetalForge(inventoryPlayer, (TEMMetalForge)teu);
			case ANALYZER:
				return new GuiAnalyzer(inventoryPlayer, (TEMAnalyzer)teu);
			case PRINTER:
				return new GuiPrinter(inventoryPlayer, (TEMPrinter)teu);
			case XRAY:
				return new GuiXray(inventoryPlayer, (TEMXray)teu);
			case PASTURE:
				return new GuiPasture(inventoryPlayer, (TEMPasture)teu);
			case ENERGY_STORAGE:
				return new GuiEnergyStorage(inventoryPlayer, (TEUEnergyStorage)teu);
			case GENERATOR:
				return new GuiGenerator(inventoryPlayer, (TEMGenerator)teu);
		}
		return null;
	}

	public static boolean stackValidForSlot(int teuID, int index, ItemStack itemstack) {
		// Switch first based on the type of TEU, then based on the index of the slot. If a TEU is not in this switch, it always returns false, e.g. Pasture
		switch(teuID) {
			case METAL_FORGE:
				switch(index) {
					case 0:
						return TEHelper.isAlloyBook(itemstack);
					case 1:
					case 2:
						return false;
					default:
						return getIngotNum(itemstack) != -1;
				}
			case ANALYZER:
				switch(index) {
					case 0:
						return itemstack.itemID == Items.alloyIngot.itemID && itemstack.hasTagCompound();
					case 2:
						return itemstack.itemID == Items.alloyBook.itemID;
				}
				break;
			case PRINTER:
				switch(index) {
					case 0:
						return (itemstack.itemID == Items.alloyBook.itemID || itemstack.itemID == Item.writableBook.itemID || itemstack.itemID == Item.writtenBook.itemID)
								&& itemstack.hasTagCompound();
					case 1:
						return itemstack.itemID == Item.book.itemID;
				}
				break;
			case XRAY:
				return TEHelper.isDetectable(itemstack);
			case GENERATOR:
				return TileEntityFurnace.isItemFuel(itemstack);
		}
		return false;
	}
}
