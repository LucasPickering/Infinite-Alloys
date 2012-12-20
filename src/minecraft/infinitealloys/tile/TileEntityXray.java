package infinitealloys.tile;

import infinitealloys.Point;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityXray extends TileEntityMachine {

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
								detectedBlocks.add(new Point(xRel, y, zRel));
							if(++blocksSearched == TEHelper.SEARCH_PER_TICK) {
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

	public ArrayList<Point> getDetectedBlocks() {
		return detectedBlocks;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.SPEED2))
			ticksToProcess = 12000;
		else if(hasUpgrade(TEHelper.SPEED1))
			ticksToProcess = 18000;
		else
			ticksToProcess = 24000;

		if(hasUpgrade(TEHelper.EFFICIENCY2))
			joulesUsedPerTick = 1800D;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			joulesUsedPerTick = 2700D;
		else
			joulesUsedPerTick = 3600D;

		if(hasUpgrade(TEHelper.RANGE2))
			range = 20;
		else if(hasUpgrade(TEHelper.RANGE1))
			range = 15;
		else
			range = 10;

		canNetwork = hasUpgrade(TEHelper.WIRELESS);

		if(hasUpgrade(TEHelper.ELECCAPACITY2))
			maxJoules = 1000000D;
		else if(hasUpgrade(TEHelper.ELECCAPACITY1))
			maxJoules = 750000D;
		else
			maxJoules = 500000D;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(TEHelper.SPEED1);
		validUpgrades.add(TEHelper.SPEED2);
		validUpgrades.add(TEHelper.EFFICIENCY1);
		validUpgrades.add(TEHelper.EFFICIENCY2);
		validUpgrades.add(TEHelper.RANGE1);
		validUpgrades.add(TEHelper.RANGE2);
		validUpgrades.add(TEHelper.WIRELESS);
	}
}
