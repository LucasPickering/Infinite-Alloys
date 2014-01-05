package infinitealloys.tile;

import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TEEXray extends TileEntityElectric {

	/** A list of the detected blocks, x and z are relative to the machine, y is absolute */
	public final ArrayList<Point> detectedBlocks = new ArrayList<Point>();
	public int range;

	/** The selected button for the user, client-side only */
	@SideOnly(Side.CLIENT)
	public int selectedButton = -1;

	/** The last point that was checked for the target block in the previous iteration of {@link #search}. The x and z coords are relative to the x-ray block;
	 * the y coord is absolute */
	private Point lastSearch;

	/** Should searching continue, or is it complete. Set this to true to begin a search. */
	public boolean shouldSearch;

	public TEEXray(byte front) {
		this();
		this.front = front;
	}

	public TEEXray() {
		super(2);
		stackLimit = 1;
		ticksToProcess = 24000;
		baseRKPerTick = -360;
	}

	@Override
	public int getID() {
		return MachineHelper.XRAY;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(inventoryStacks[0] == null) {
			detectedBlocks.clear();
			shouldSearch = false;
		}
		else if(shouldSearch && Funcs.isServer())
			search();
	}

	@Override
	public boolean shouldProcess() {
		return shouldSearch || getProcessProgress() > 0;
	}

	@Override
	protected boolean shouldResetProgress() {
		return inventoryStacks[0] == null;
	}

	/** Perform a search for the target block. This checks {@link infinitealloys.util.MachineHelper#SEARCH_PER_TICK a set amount of} blocks in a tick, then saves
	 * its place and picks up where it left off next tick. This eliminates stutter during searches. */
	private void search() {
		// Convenience variables for the data pertaining to the target block that is being searched for
		final int targetID = inventoryStacks[0].itemID;
		final int targetMetadata = inventoryStacks[0].getItemDamage();

		// The amount of blocks that have been iterated over this tick. When this reaches TEHelper.SEARCH_PER_TICK, the loops break
		int blocksSearched = 0;

		// (-range, 0, -range) is the start point for the search. When lastSearch == (-range, 0, -range), this is the first tick of the search. The block list
		// is cleared to be repopulated in this search.
		if(lastSearch.equals(-range, 0, -range))
			detectedBlocks.clear();

		// Iterate over each block that is within the given range horizontally. Note that it searches all blocks below x-ray within that horizontal range, which
		// is why the y loop comes first and why it looks a bit different from the x and z loops.
		for(int y = lastSearch.y; y <= yCoord; y++) {
			for(int x = lastSearch.x; x <= range; x++) {
				for(int z = lastSearch.z; z <= range; z++) {

					// If the block at the given coords (which have been converted to absolute coordinates) is of the target block's type, add it to the
					// list of blocks that have been found.
					if(Funcs.blocksEqual(worldObj, targetID, targetMetadata, xCoord + x, y, zCoord + z))
						detectedBlocks.add(new Point(x, y, z));

					// If the amounts of blocks search this tick has reached the limit, save our place and end the function. The search will be
					// continued next tick.
					if(++blocksSearched >= MachineHelper.SEARCH_PER_TICK) {
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
		tagCompound.setBoolean("ShouldSearch", detectedBlocks.size() > 0);
	}

	@Override
	public Object[] getSyncDataToClient() {
		final List<Object> coords = new ArrayList<Object>();
		for(final Point point : detectedBlocks) {
			coords.add(point.x);
			coords.add((short)point.y);
			coords.add(point.z);
		}
		return ArrayUtils.addAll(super.getSyncDataToClient(), (byte)detectedBlocks.size(), coords.toArray());
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(MachineHelper.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(MachineHelper.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(MachineHelper.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(MachineHelper.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 1.0F;

		if(hasUpgrade(MachineHelper.RANGE2))
			range = 10;
		else if(hasUpgrade(MachineHelper.RANGE1))
			range = 8;
		else
			range = 5;
		if(lastSearch == null)
			lastSearch = new Point(-range, 0, -range);
		else
			lastSearch.set(-range, 0, -range);
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(MachineHelper.SPEED1);
		validUpgrades.add(MachineHelper.SPEED2);
		validUpgrades.add(MachineHelper.EFFICIENCY1);
		validUpgrades.add(MachineHelper.EFFICIENCY2);
		validUpgrades.add(MachineHelper.RANGE1);
		validUpgrades.add(MachineHelper.RANGE2);
		validUpgrades.add(MachineHelper.WIRELESS);
	}
}
