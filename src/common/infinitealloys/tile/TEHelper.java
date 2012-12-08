package infinitealloys.tile;

import infinitealloys.Point;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.src.Block;

public class TEHelper {

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

	public static boolean isDetectable(int id, int metadata) {
		return detectables.containsKey(id + "@" + metadata);
	}
}
