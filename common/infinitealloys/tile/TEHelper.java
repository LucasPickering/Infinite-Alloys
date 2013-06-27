package infinitealloys.tile;

import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class TEHelper {

	public static final int COMPUTER = 0;
	public static final int METAL_FORGE = 1;
	public static final int ANALYZER = 2;
	public static final int PRINTER = 3;
	public static final int XRAY = 4;

	public static final int SPEED1 = 1;
	public static final int SPEED2 = 2;
	public static final int EFFICIENCY1 = 4;
	public static final int EFFICIENCY2 = 8;
	public static final int CAPACITY1 = 16;
	public static final int CAPACITY2 = 32;
	public static final int RANGE1 = 64;
	public static final int RANGE2 = 128;
	public static final int WIRELESS = 256;
	public static final int ELECCAPACITY1 = 512;
	public static final int ELECCAPACITY2 = 1024;

	public static final int AMPS_PER_TICK = 5;
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
		prereqUpgrades.add(ELECCAPACITY1);
		prereqNeedingUpgrades.add(SPEED2);
		prereqNeedingUpgrades.add(EFFICIENCY2);
		prereqNeedingUpgrades.add(CAPACITY2);
		prereqNeedingUpgrades.add(RANGE2);
		prereqNeedingUpgrades.add(ELECCAPACITY2);
	}

	public static void addDetectable(Block block, int worth) {
		addDetectable(block, 0, worth);
	}

	public static void addDetectable(Block block, int metadata, int worth) {
		detectables.put(block.blockID + "@" + metadata, worth);
	}

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

	public static boolean stackValidForSlot(int type, int index, ItemStack itemstack) {
		switch(type) {
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
					default:
						return false;
				}
			case PRINTER:
				switch(index) {
					case 0:
						return itemstack.hasTagCompound()
								&& (itemstack.itemID == Items.alloyBook.itemID || itemstack.itemID == Item.writableBook.itemID || itemstack.itemID == Item.writtenBook.itemID);
					case 1:
						return itemstack.itemID == Item.book.itemID;
					default:
						return false;
				}
			case XRAY:
				return TEHelper.isDetectable(itemstack);
		}
		return false;
	}
}
