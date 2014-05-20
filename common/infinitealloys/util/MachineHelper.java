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
import infinitealloys.tile.TEEEnergyStorage;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.OreDictionary;

public class MachineHelper {

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

	/** The blocks that the x-ray can detect and their worths */
	private static HashMap<String, Integer> detectables = new HashMap<String, Integer>();

	/** When a player joins a world, all the TEs in that dimension that are hosting a network are added to this list. The TEs go through and check to see if any
	 * need to be synced.
	 * The player's name and the location of the host are stored. */
	private static HashMap<Point, String> networksToSync = new HashMap<Point, String>();

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
						return itemstack.itemID == Items.ingot.itemID && itemstack.getItemDamage() == index;
				}
			case XRAY:
				return MachineHelper.isDetectable(itemstack);
			case ENERGY_STORAGE:
				return TileEntityFurnace.getItemBurnTime(itemstack) > 0;
		}
		return false;
	}

	/** Is the machine at x, y, z capable of connecting to an ESU or computer? */
	public static boolean isClient(World world, int x, int y, int z) {
		return world.getBlockTileEntity(x, y, z) instanceof TileEntityMachine && ((TileEntityMachine)world.getBlockTileEntity(x, y, z)).hasUpgrade(EnumUpgrade.WIRELESS) ||
				world.getBlockTileEntity(x, y, z) instanceof TileEntityElectric;
	}

	/** Add all network host TEs in the specified dimension to networksToSync */
	public static void populateNetworksToSync(EntityPlayer player) {
		for(TileEntity te : (ArrayList<TileEntity>)DimensionManager.getWorld(player.dimension).loadedTileEntityList)
			if(te instanceof IHost)
				networksToSync.put(new Point(te.xCoord, te.yCoord, te.zCoord), player.username);

	}

	/** A host TE calls to see if any players need that TE's client data
	 * 
	 * @return null if no players need data, otherwise the players that need data */
	public static EntityPlayer networkSyncCheck(int dimensionID, Point host) {
		if(networksToSync.containsKey(host)) {
			EntityPlayer player = Funcs.getPlayerForUsername(networksToSync.get(host));
			if(dimensionID == player.dimension) {
				networksToSync.remove(host);
				return player;
			}
		}
		return null;
	}
}
