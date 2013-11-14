package infinitealloys.tile;

import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TEMXray extends TileEntityMachine {

	/** A list of the detected blocks, x and z are relative to the machine, y is absolute */
	private ArrayList<Point> detectedBlocks = new ArrayList<Point>();
	public int range;

	/** The selected button for the user, client-side only */
	@SideOnly(Side.CLIENT)
	public int selectedButton = -1;

	/** The last point that was checked for the target block in the previous iteration of {@link #search}. The x and z coords are relative to the x-ray block;
	 * the y coord is absolute */
	private Point lastSearch = new Point();

	/** Should searching continue, or is it complete. Set this to true to begin a search. */
	public boolean shouldSearch;

	/** Is it searching client-side. Does not necessarily mean the x-ray is running a search, only that the user sees a loading progress bar */
	public boolean searchingClient;

	public TEMXray(int facing) {
		this();
		front = facing;
	}

	public TEMXray() {
		super(1);
		inventoryStacks = new ItemStack[2];
		stackLimit = 1;
		ticksToProcess = 24000;
		baseRKPerTick = -360;
	}

	@Override
	public int getID() {
		return TEHelper.XRAY;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return super.isItemValidForSlot(slot, itemstack) || TEHelper.stackValidForSlot(TEHelper.XRAY, slot, itemstack);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		// True if there were blocks before to be restored, false if it was empty
		shouldSearch = tagCompound.getBoolean("ShouldSearch");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		// True if there are blocks on the GUI, false if there are no blocks
		tagCompound.setBoolean("ShouldSearch", getDetectedBlocks().size() > 0);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(inventoryStacks[0] == null) {
			clearDetectedBlocks();
			shouldSearch = false;
			searchingClient = false;
		}
		else if(shouldSearch)
			search();
	}

	/** Perform a search for the target block. This checks {@link infinitealloys.tile.TEHelper#SEARCH_PER_TICK a set amount of} blocks in a tick, then saves its
	 * place and picks up where it left off next tick. This eliminates stutter during searches. */
	private void search() {
		// If there is no target block to search for, return
		if(inventoryStacks[0] == null)
			return;

		// Enable the process of searching that appears on the client. Note that this is a just a progress bar and does not actually look for blocks.
		searchingClient = true;

		// Convenience variables for the data pertaining to the target block that is being searched for
		int targetID = inventoryStacks[0].itemID;
		int targetMetadata = inventoryStacks[0].getItemDamage();

		// The amount of blocks that have been iterated over this tick. When this reaches TEHelper.SEARCH_PER_TICK, the loops break
		int blocksSearched = 0;

		// (-range, 0, -range) is the start point for the search. When lastSearch == (-range, 0, -range), this is the first tick of the search. The block list
		// is cleared to be repopulated in this search.
		if(lastSearch.equals(-range, 0, -range))
			clearDetectedBlocks();

		// Iterate over each block that is within the given range horizontally. Note that it searches all blocks below x-ray within that horizontal range, which
		// is why the y loop comes first and why it looks a bit different from the x and z loops.
		for(int y = lastSearch.y; y <= yCoord; y++) {
			for(int x = lastSearch.x; x <= range; x++) {
				for(int z = lastSearch.z; z <= range; z++) {

					// If the block at the given coords (which have been converted to absolute coordinates) is of the target block's type, add it to the
					// list of blocks that have been found.
					if(Funcs.blocksEqual(worldObj, targetID, targetMetadata, xCoord + x, y, zCoord + z))
						addDetectedBlock(new Point(x, y, z));

					// If the amounts of blocks search this tick has reached the limit, save our place and end the function. The search will be
					// continued next tick.
					if(++blocksSearched >= TEHelper.SEARCH_PER_TICK) {
						lastSearch.set(x, y, z);
						return;
					}
				}
				// If we've search all the z values, reset the z position.
				lastSearch.z = -range;
			}
			// If we've search all the x values, reset the x position.
			lastSearch.x = -range;
		}
		// If we've search all the y values, reset the y position.
		lastSearch.y = 0;

		// The search is done. Stop running the function until another search is initiated.
		shouldSearch = false;
	}

	public ArrayList<Point> getDetectedBlocks() {
		return detectedBlocks;
	}

	public void clearDetectedBlocks() {
		detectedBlocks.clear();
	}

	public void addDetectedBlock(Point p) {
		detectedBlocks.add(p);
	}

	public void handlePacketDataFromClient(boolean searching) {
		this.searchingClient = searching;
	}

	@Override
	public boolean shouldProcess() {
		return searchingClient;
	}

	@Override
	public void finishProcess() {
		searchingClient = false;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(TEHelper.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(TEHelper.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 1.0F;

		if(hasUpgrade(TEHelper.RANGE2))
			range = 10;
		else if(hasUpgrade(TEHelper.RANGE1))
			range = 8;
		else
			range = 5;
		lastSearch.set(-range, 0, -range);
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
