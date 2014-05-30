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
import infinitealloys.item.IAItems;
import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.tile.TEEEnergyStorage;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.oredict.OreDictionary;

public class MachineHelper {

	// Machine types
	public static final int COMPUTER = 0;
	public static final int METAL_FORGE = 1;
	public static final int ANALYZER = 2;
	public static final int XRAY = 3;
	public static final int PASTURE = 4;
	public static final int ENERGY_STORAGE = 5;

	/** The TileEntityMachine class for each machine */
	public static final Class[] MACHINE_CLASSES = { TEMComputer.class, TEEMetalForge.class, TEEAnalyzer.class, TEEXray.class, TEEPasture.class, TEEEnergyStorage.class };

	public static final String[] MACHINE_NAMES = { "computer", "metalforge", "analyzer", "xray", "pasture", "energystorage" };

	/** How many blocks are searched per tick. Used to limit lag on the x-ray. */
	public static final int SEARCH_PER_TICK = 2000;

	/** The controlling computer for each player */
	public static HashMap<String, Point> controllers = new HashMap<String, Point>();

	/** The blocks that the x-ray can detect and their values */
	private static ArrayList<DetectableBlock> detectables = new ArrayList<DetectableBlock>();

	/** A list of the players who still need network information for the machines to be synced. This sync is done when they first activate a machine. */
	public static ArrayList<String> playersToSync = new ArrayList<String>();

	/** Add a block to the list of blocks that can be detected by the x-ray
	 * 
	 * @param block the item that corresponds to the block that will be detected
	 * @param color the color of the outline that will be used for this block
	 * @param value the amount the block is worth, higher value requires more energy to detect */
	public static void addDetectable(Item block, int metadata, int color, int value) {
		detectables.add(new DetectableBlock(block, metadata, color, value));
	}

	/** Add a block or blocks to the list of blocks that can be detected by the x-ray with an ore dictionary string
	 * 
	 * @param dictName the ore dictionary string from which the block(s) is/are retrieved
	 * @param color the color of the outline that will be used for this block
	 * @param value the amount the block(s) is/are worth, higher value requires more energy to detect */
	public static void addDictDetectable(String dictName, int color, int value) {
		for(ItemStack itemstack : OreDictionary.getOres(dictName))
			addDetectable(itemstack.getItem(), itemstack.getItemDamage(), color, value);
	}

	public static boolean isDetectable(ItemStack stack) {
		return getDetectableValue(stack.getItem(), stack.getItemDamage()) > 0;
	}

	/** Get the detectable value of the given ItemStack
	 * 
	 * @return value of the block if it is detectable, otherwise 0 */
	public static int getDetectableValue(Item block, int metadata) {
		for(DetectableBlock detectable : detectables)
			if(detectable.block == block && detectable.metadata == metadata)
				return detectable.value;
		return 0;
	}

	/** Get the detectable color of the given ItemStack
	 * 
	 * @return color of the block's outline if it is detectable, otherwise 0 */
	public static int getDetectableColor(Item block, int metadata) {
		for(DetectableBlock detectable : detectables)
			if(detectable.block == block && detectable.metadata == metadata)
				return detectable.color;
		return 0;
	}

	public static int getIngotNum(ItemStack ingot) {
		if(ingot.getItem() == IAItems.ingot && ingot.getItemDamage() < Consts.METAL_COUNT)
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
				return new ContainerESU(inventoryPlayer, (TEEEnergyStorage)tem);
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
				return new GuiEnergyStorage(inventoryPlayer, (TEEEnergyStorage)tem);
		}
		return null;
	}

	public static boolean stackValidForSlot(int temID, int index, ItemStack itemstack) {
		// Switch first based on the type of machine, then based on the index of the slot. If a machine is not in this switch, it always returns false
		switch(temID) {
			case METAL_FORGE:
				switch(index) {
					case 0:
						return false;
					default:
						return getIngotNum(itemstack) != -1;
				}
			case ANALYZER:
				switch(index) {
					default:
						return itemstack.getItem() == IAItems.ingot && itemstack.getItemDamage() == index;
				}
			case XRAY:
				return MachineHelper.isDetectable(itemstack);
			case ENERGY_STORAGE:
				return TileEntityFurnace.getItemBurnTime(itemstack) > 0;
		}
		return false;
	}

	/** Is the machine at x, y, z capable of connecting to an ESU or computer? */
	public static boolean isClient(TileEntity te) {
		return te instanceof TileEntityMachine && ((TileEntityMachine)te).hasUpgrade(EnumUpgrade.WIRELESS) || te instanceof TileEntityElectric;
	}

	/** A block that the x-ray can detect and identify */
	private static class DetectableBlock {

		private final Item block;
		private final int metadata;
		private final int color;
		private final int value;

		private DetectableBlock(Item block, int metadata, int color, int value) {
			this.block = block;
			this.metadata = metadata;
			this.color = color;
			this.value = value;
		}
	}
}
