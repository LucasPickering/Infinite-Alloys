package infinitealloys.util;

import infinitealloys.item.IAItems;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;

public class MachineHelper {

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
