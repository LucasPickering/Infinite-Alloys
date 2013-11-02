package infinitealloys.tile;

import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRKStorage extends TileEntityUpgradable {

	/** The amount of time, in ticks (20 ticks = 1 second), between each regular search for new machines to connect to. */
	private final int SEARCH_INTERVAL = 200;

	/** The maximum amount of RK that this machine can store */
	private int maxRK = 10000000;

	private int currentRK;

	/** The range that is searched for new machines */
	private int autoSearchRange;

	/** The max range that machines can be added at with the Internet Wand */
	private int maxRange;

	/** A list of the locations of TileEntityUpgradables that provide power to or consume power from this storage unit */
	private List<Point> connectedMachines = new ArrayList<Point>();

	/** How many ticks have passed since the last search for machines. When this reaches {@link #SEARCH_INTERVAL the search interval time}, it resets to 0 and a
	 * search begins. */
	private int currentSearchTicks;

	/** The last point that was checked for a machine in the previous iteration of {@link #search}. The coords are relative to this TE block. */
	private Point lastSearch;

	/** Should searching continue, or is it complete. Set this to true to begin a search. */
	public boolean shouldSearch;

	// ---BEGIN GENERAL FUNCTIONS---

	public TileEntityRKStorage(int facing) {
		this();
		front = facing;
	}

	public TileEntityRKStorage() {
		super();
		inventoryStacks = new ItemStack[1];
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		// Increment the ticks since last search and if it reaches the interval, reset it and run a search
		if(++currentSearchTicks >= SEARCH_INTERVAL) {
			currentSearchTicks = 0;
			shouldSearch = true;
		}

		// Run a search
		if(shouldSearch)
			search();

		// If a connected machine no longer exists, remove it from the network
		for(Point p : connectedMachines)
			if(!(worldObj.getBlockTileEntity(p.x, p.y, p.z) instanceof TileEntityMachine))
				connectedMachines.remove(p);
	}

	/** Perform a search for machines that produce/consume power. This checks {@link infinitealloys.tile.TEHelper#SEARCH_PER_TICK a set amount of} blocks in a
	 * tick, then saves its place and picks up where it left off next tick, which eliminates stutter during searches. */
	private void search() {
		// The amount of blocks that have been iterated over this tick. When this reaches TEHelper.SEARCH_PER_TICK, the loops break
		int blocksSearched = 0;

		// Iterate over each block that is within the given range in all three dimensions. The searched area will be a cube with each side being (2 * range + 1)
		// blocks long.
		for(int x = lastSearch.x; x <= autoSearchRange; x++) {
			for(int y = lastSearch.y; y <= autoSearchRange; y++) {
				for(int z = lastSearch.z; z <= autoSearchRange; z++) {

					// If the block at the given coords (which have been converted to absolute coordinates) is a machine and it is not already connected to a
					// power storage unit, add it to the power network.
					TileEntity te = worldObj.getBlockTileEntity(xCoord + x, yCoord + y, zCoord + z);
					if(te instanceof TileEntityMachine && ((TileEntityMachine)te).powerStorageUnit == null)
						connectedMachines.add(new Point(xCoord + x, yCoord + y, zCoord + z));

					// If the amounts of blocks search this tick has reached the limit, save our place and end the function. The search will be
					// continued next tick.
					if(++blocksSearched >= TEHelper.SEARCH_PER_TICK) {
						lastSearch.set(x, y, z);
						return;
					}
				}
				lastSearch.z = -autoSearchRange; // If we've search all the z values, reset the z position.
			}
			lastSearch.y = -autoSearchRange; // If we've search all the y values, reset the y position.
		}
		lastSearch.x = -autoSearchRange; // If we've search all the x values, reset the x position.

		shouldSearch = false; // The search is done. Stop running the function until another search is initiated.
	}

	/** Will the unit support the specified change in RK, i.e. if changeInRK is added to currentRK, will the result be less than zero or overflow the machine? If
	 * this condition is true, make said change, i.e. actually add changeInRK to currentRK
	 * 
	 * @param changeInRK the specified change in RK
	 * @return True if changeInRK plus currentRK is between 0 and maxRK, False otherwise */
	public boolean consumeRK(int changeInRK) {
		if(0 <= currentRK + changeInRK && currentRK + changeInRK <= maxRK) {
			currentRK += changeInRK;
			return true;
		}
		return false;
	}

	// ---END GENERAL FUNCTIONS---
	// ---BEGIN INVENTORY FUNCTIONS---

	@Override
	public String getInvName() {
		return "RK Storage"; // TODO: Change this one I figure out a better name for this TE
	}

	// ---END INVENTORY FUNCTIONS---
	// ---BEGIN UPGRADE FUNCTIONS---

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.CAPACITY2))
			maxRK = 40000000;
		else if(hasUpgrade(TEHelper.CAPACITY1))
			maxRK = 20000000;
		else
			maxRK = 10000000;

		if(hasUpgrade(TEHelper.RANGE2)) {
			autoSearchRange = 20;
			maxRange = 60;
		}
		else if(hasUpgrade(TEHelper.RANGE1)) {
			autoSearchRange = 15;
			maxRange = 45;
		}
		else {
			autoSearchRange = 10;
			maxRange = 30;
		}
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(TEHelper.CAPACITY1);
		validUpgrades.add(TEHelper.CAPACITY2);
		validUpgrades.add(TEHelper.RANGE1);
		validUpgrades.add(TEHelper.RANGE2);
	}

	// ---END UPGRADE FUNCTIONS---
}
