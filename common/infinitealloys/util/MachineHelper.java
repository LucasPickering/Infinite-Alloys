package infinitealloys.util;

import infinitealloys.client.gui.GuiAnalyzer;
import infinitealloys.client.gui.GuiComputer;
import infinitealloys.client.gui.GuiEnergyStorage;
import infinitealloys.client.gui.GuiMachine;
import infinitealloys.client.gui.GuiMetalForge;
import infinitealloys.client.gui.GuiPasture;
import infinitealloys.client.gui.GuiXray;
import infinitealloys.inventory.ContainerAnalyzer;
import infinitealloys.inventory.ContainerESU;
import infinitealloys.inventory.ContainerMachine;
import infinitealloys.inventory.ContainerMetalForge;
import infinitealloys.inventory.ContainerXray;
import infinitealloys.item.Items;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TEMEnergyStorage;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class MachineHelper {

	public static final int COMPUTER = 0;
	public static final int METAL_FORGE = 1;
	public static final int ANALYZER = 2;
	public static final int XRAY = 3;
	public static final int PASTURE = 4;
	public static final int ENERGY_STORAGE = 5;

	public static final int SPEED1 = 1;
	public static final int SPEED2 = 2;
	public static final int EFFICIENCY1 = 4;
	public static final int EFFICIENCY2 = 8;
	public static final int CAPACITY1 = 16;
	public static final int CAPACITY2 = 32;
	public static final int RANGE1 = 64;
	public static final int RANGE2 = 128;
	public static final int WIRELESS = 256;

	/** The TileEntityMachine class for each machine */
	public static final Class[] MACHINE_CLASSES = { TEMComputer.class, TEEMetalForge.class, TEEAnalyzer.class, TEEXray.class, TEEPasture.class, TEMEnergyStorage.class };

	public static final String[] MACHINE_NAMES = { "computer", "metalforge", "analyzer", "xray", "pasture", "energystorage" };

	/** How many blocks are searched per tick. Used to limit lag on the x-ray. */
	public static final int SEARCH_PER_TICK = 2000;

	/** The controlling computer for each player */
	public static HashMap<String, Point> controllers = new HashMap<String, Point>();

	/** A list of upgrades that are prerequisites for other upgrades, e.g. Speed I is a prereq for Speed II */
	public static final ArrayList<Integer> prereqUpgrades = new ArrayList<Integer>();

	/** A list of upgrades that require other upgrades to work, e.g. Speed II because it requires Speed I */
	public static final ArrayList<Integer> prereqNeedingUpgrades = new ArrayList<Integer>();

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
		for(final ItemStack block : OreDictionary.getOres(dictName))
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

	public static boolean isBook(ItemStack stack) {
		return (stack.itemID == Items.alloyBook.itemID || stack.itemID == Item.writableBook.itemID || stack.itemID == Item.writtenBook.itemID) && stack.hasTagCompound();
	}

	/** Is the upgrade a prerequisite for another
	 * 
	 * @param upgrade
	 * @return true if it is a prereq */
	public static boolean isPrereqUpgrade(int upg) {
		return MachineHelper.prereqUpgrades.contains(upg);
	}

	/** Does the upgrade require another to work?
	 * 
	 * @param upgrade
	 * @return true if it has a prereq */
	public static boolean hasPrereqUpgrade(int upg) {
		return MachineHelper.prereqNeedingUpgrades.contains(upg);
	}

	public static int getIngotNum(ItemStack ingot) {
		if(ingot.itemID == Items.ingot.itemID && ingot.getItemDamage() < Consts.METAL_COUNT)
			return ingot.getItemDamage();
		return -1;
	}

	/** Get a Container instance for a specific machine
	 * 
	 * @param temID The numerical ID for the machine, see the machine ID constants in this class
	 * @param inventoryPlayer An instance of InventoryPlayer (not used, just passed through)
	 * @param tem A TileEntityMachine instance to be casted and passed to the Container constructor
	 * @return A new instance of a Container class that extends ContainerMachine */
	public static ContainerMachine getContainerForMachine(InventoryPlayer inventoryPlayer, TileEntityMachine tem) {
		switch(tem.getID()) {
			case COMPUTER:
				return new ContainerMachine(inventoryPlayer, tem, 8, 84, 140, 43);
			case METAL_FORGE:
				return new ContainerMetalForge(inventoryPlayer, (TEEMetalForge)tem);
			case ANALYZER:
				return new ContainerAnalyzer(inventoryPlayer, (TEEAnalyzer)tem);
			case XRAY:
				return new ContainerXray(inventoryPlayer, (TEEXray)tem);
			case PASTURE:
				return new ContainerMachine(inventoryPlayer, tem, 13, 94, 141, 44);
			case ENERGY_STORAGE:
				return new ContainerESU(inventoryPlayer, (TEMEnergyStorage)tem);
		}
		return null;
	}

	/** Get a GUI instance for a specific machine
	 * 
	 * @param temID The numerical ID for the machine, see the machine ID constants in this class
	 * @param inventoryPlayer An instance of InventoryPlayer (not used, just passed through)
	 * @param tem A TileEntityMachine instance to be casted and passed to the GUI constructor
	 * @return A new instance of a GUI class that extends GuiMachine */
	public static GuiMachine getGuiForMachine(InventoryPlayer inventoryPlayer, TileEntityMachine tem) {
		switch(tem.getID()) {
			case COMPUTER:
				return new GuiComputer(inventoryPlayer, (TEMComputer)tem);
			case METAL_FORGE:
				return new GuiMetalForge(inventoryPlayer, (TEEMetalForge)tem);
			case ANALYZER:
				return new GuiAnalyzer(inventoryPlayer, (TEEAnalyzer)tem);
			case XRAY:
				return new GuiXray(inventoryPlayer, (TEEXray)tem);
			case PASTURE:
				return new GuiPasture(inventoryPlayer, (TEEPasture)tem);
			case ENERGY_STORAGE:
				return new GuiEnergyStorage(inventoryPlayer, (TEMEnergyStorage)tem);
		}
		return null;
	}

	public static boolean stackValidForSlot(int temID, int index, ItemStack itemstack) {
		// Switch first based on the type of machine, then based on the index of the slot. If a machine is not in this switch, it always returns false
		switch(temID) {
			case METAL_FORGE:
				switch(index) {
					case 0:
						return itemstack.itemID == Items.alloyBook.itemID;
					case 1:
						return false;
					default:
						return getIngotNum(itemstack) != -1;
				}
			case ANALYZER:
				switch(index) {
					case 8:
						return itemstack.itemID == Items.alloyBook.itemID;
					default:
						return itemstack.itemID == Items.ingot.itemID && itemstack.getItemDamage() == index;
				}
			case XRAY:
				return MachineHelper.isDetectable(itemstack);
			case ENERGY_STORAGE:
				return TileEntityFurnace.getItemBurnTime(itemstack) > 0;
		}
		return false;
	}

	/** Is the machine at x, y, z capable of hosting other machines? */
	public static boolean isHost(World world, int x, int y, int z) {
		return world.getBlockTileEntity(x, y, z) instanceof IHost;
	}

	/** Is the machine capable of connecting to an ESU? */
	public static boolean isElectric(World world, int x, int y, int z) {
		return world.getBlockTileEntity(x, y, z) instanceof TileEntityElectric;
	}

	/** Is the machine at x, y, z capable of connecting to a computer? */
	public static boolean isWireless(World world, int x, int y, int z) {
		return world.getBlockTileEntity(x, y, z) instanceof TileEntityMachine && ((TileEntityMachine)world.getBlockTileEntity(x, y, z)).hasUpgrade(WIRELESS);
	}

	/** Is the machine at x, y, z capable of connecting to an ESU or computer? */
	public static boolean isClient(World world, int x, int y, int z) {
		return isElectric(world, x, y, z) || isWireless(world, x, y, z);
	}
}
