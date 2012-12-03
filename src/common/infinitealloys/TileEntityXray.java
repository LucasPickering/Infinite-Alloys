package infinitealloys;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityXray extends TileEntityMachine {

	private static HashMap<String, Integer> detectables = new HashMap<String, Integer>();
	private ArrayList<Point> detectedBlocks = new ArrayList<Point>();
	public int range;
	private Point lastSearch;

	public TileEntityXray(ForgeDirection facing) {
		this();
		front = facing;
	}

	public TileEntityXray() {
		super(1);
		inventoryStacks = new ItemStack[2];
		stackLimit = 1;
		range = 10;
		lastSearch = new Point(0, 0, 0);
	}

	public static void addDetectable(Block block, int worth) {
		addDetectable(new ItemStack(block), worth);
	}

	public static void addDetectable(ItemStack block, int worth) {
		detectables.put(block.itemID + "@" + block.getItemDamage(), worth);
	}

	public static boolean isDetectable(ItemStack block) {
		return detectables.containsKey(block.itemID + "@" + block.getItemDamage());
	}

	@Override
	public String getInvName() {
		return "X-ray";
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		return 1;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!lastSearch.equals(0, 0, 0))
			search();
	}

	public void search() {
		if(inventoryStacks[0] == null)
			return;
		int targetID = inventoryStacks[0].itemID;
		int targetMetadata = inventoryStacks[0].getItemDamage();
		int blocksSearched = 0;
		for(int y = lastSearch.y; y >= -yCoord; y--) {
			for(int x = Math.abs(lastSearch.x); x <= range; x++) {
				for(int z = Math.abs(lastSearch.z); z <= range; z++) {
					for(int i = lastSearch.x >= 0 ? 0 : 1; i < 2; i++) {
						for(int j = lastSearch.z >= 0 ? 0 : 1; j < 2; j++) {
							int xRel = i == 0 ? x : -x;
							int zRel = j == 0 ? z : -z;
							if(worldObj.getBlockId(xCoord + xRel, yCoord + y, zCoord + zRel) == targetID && worldObj.getBlockMetadata(xCoord + xRel, yCoord + y, zCoord + zRel) == targetMetadata)
								detectedBlocks.add(new Point(xCoord + xRel, yCoord + y, zCoord + zRel));
							if(++blocksSearched == 20) {
								lastSearch.set(xRel, y, zRel);
								return;
							}
						}
					}
				}
				lastSearch.z = 0;
			}
			lastSearch.x = 0;
		}
		lastSearch.y = 0;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(SPEED2))
			ticksToProcess = 12000;
		else if(hasUpgrade(SPEED1))
			ticksToProcess = 18000;
		else
			ticksToProcess = 24000;

		if(hasUpgrade(EFFICIENCY2))
			joulesUsedPerTick = 1800D;
		else if(hasUpgrade(EFFICIENCY1))
			joulesUsedPerTick = 2700D;
		else
			joulesUsedPerTick = 3600D;

		if(hasUpgrade(RANGE2))
			range = 20;
		else if(hasUpgrade(RANGE1))
			range = 15;
		else
			range = 10;

		canNetwork = hasUpgrade(WIRELESS);

		if(hasUpgrade(ELECCAPACITY2))
			maxJoules = 1000000D;
		else if(hasUpgrade(ELECCAPACITY1))
			maxJoules = 750000D;
		else
			maxJoules = 500000D;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(SPEED1);
		validUpgrades.add(SPEED2);
		validUpgrades.add(EFFICIENCY1);
		validUpgrades.add(EFFICIENCY2);
		validUpgrades.add(RANGE1);
		validUpgrades.add(RANGE2);
		validUpgrades.add(WIRELESS);
	}
}
