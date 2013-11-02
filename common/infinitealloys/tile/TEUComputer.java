package infinitealloys.tile;

import infinitealloys.block.Blocks;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TEUComputer extends TileEntityUpgradable {

	/** The amount of time, in ticks (20 ticks = 1 second), between each regular search for new machines to connect to. */
	private final int SEARCH_INTERVAL = 200;

	/** That amount of machines that the computer can control */
	private int networkCapacity;

	/** The range that the computer searches for new TEUs */
	private int autoSearchRange;

	/** The max range that TEUs can be added at with the Internet Wand */
	private int maxRange;

	/** 3D coords for each TileEntityUpgradable that is connected to the computer */
	public List<Point> connectedTEUs;

	/** How many ticks have passed since the last search for machines. When this reaches {@link #SEARCH_INTERVAL the search interval time}, it resets to 0 and a
	 * search begins. */
	private int currentSearchTicks;

	/** Should the machine periodically search for machines within a certain range? */
	public boolean autoSearch;

	/** The last point that was checked for a machine in the previous iteration of {@link #search}. The coords are relative to this TE block. */
	private Point lastSearch;

	/** Should searching continue, or is it complete. Set this to true to begin a search. */
	public boolean shouldSearch;

	// ---BEGIN GENERAL FUNCTIONS---

	public TEUComputer(int facing) {
		this();
		front = facing;
	}

	public TEUComputer() {
		super();
		inventoryStacks = new ItemStack[1];
		networkCapacity = 3;
		connectedTEUs = new ArrayList<Point>(networkCapacity);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		// If the computer should be auto-searching, increment the ticks since last search and if it reaches the interval, reset it and run a search
		if(autoSearch && ++currentSearchTicks >= SEARCH_INTERVAL) {
			currentSearchTicks = 0;
			shouldSearch = true;
		}

		// Run a search
		if(shouldSearch)
			search();

		// If a connected TEU no longer exists or its networking upgrade was removed, remove it from the network list
		for(Point p : connectedTEUs)
			if(!(worldObj.getBlockTileEntity(p.x, p.y, p.z) instanceof TileEntityUpgradable && ((TileEntityUpgradable)worldObj.getBlockTileEntity(p.x, p.y, p.z)).hasUpgrade(TEHelper.WIRELESS)))
				connectedTEUs.remove(p);
	}

	public void handlePacketDataFromServer(ArrayList networkCoords) {
		this.connectedTEUs = networkCoords;
	}

	public void handlePacketDataFromClient(boolean autoSearch) {
		this.autoSearch = autoSearch;
	}

	/** Perform a search for TEUs that can be controlled. This checks {@link infinitealloys.tile.TEHelper#SEARCH_PER_TICK a set amount of} blocks in a tick, then
	 * saves its place and picks up where it left off next tick, which eliminates stutter during searches. */
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
					if(te instanceof TileEntityUpgradable && !(te instanceof TEUComputer) && hasUpgrade(TEHelper.WIRELESS))
						connectedTEUs.add(new Point(xCoord + x, yCoord + y, zCoord + z));

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

	public boolean addMachine(EntityPlayer player, int machX, int machY, int machZ) {
		for(Point coords : connectedTEUs) {
			if(coords.x == machX && coords.y == machY && coords.z == machZ) {
				if(worldObj.isRemote)
					player.addChatMessage("Error: Machine is already in network");
				return false;
			}
		}
		if(connectedTEUs.size() >= networkCapacity) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Network full");
		}
		else if(worldObj.getBlockId(machX, machY, machZ) != Blocks.machine.blockID) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Can only add machines");
		}
		else if(machX == xCoord && machY == yCoord && machZ == zCoord) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Cannot add self to network");
		}
		else if(new Point(machX, machY, machZ).distanceTo(xCoord, yCoord, zCoord) > maxRange) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Machine out of range");
		}
		else if(!((TileEntityUpgradable)worldObj.getBlockTileEntity(machX, machY, machZ)).hasUpgrade(TEHelper.WIRELESS)) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Machine not capable of networking");
		}
		else {
			connectedTEUs.add(new Point(machX, machY, machZ));
			return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		for(int i = 0; i < tagCompound.getInteger("NetworkSize"); i++) {
			int[] coords = tagCompound.getIntArray("Coords" + i);
			connectedTEUs.add(new Point(coords[0], coords[1], coords[2]));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("NetworkSize", connectedTEUs.size());
		for(int i = 0; i < connectedTEUs.size(); i++) {
			Point coords = connectedTEUs.get(i);
			tagCompound.setIntArray("Coords" + i, new int[] { coords.x, coords.y, coords.z });
		}
	}

	// ---END GENERAL FUNCTIONS---
	// ---BEGIN GENERAL FUNCTIONS---

	@Override
	public String getInvName() {
		return "Computer";
	}

	// ---END INVENTORY FUNCTIONS---
	// ---BEGIN UPGRADE FUNCTIONS---

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.CAPACITY2))
			networkCapacity = 10;
		else if(hasUpgrade(TEHelper.CAPACITY1))
			networkCapacity = 6;
		else
			networkCapacity = 3;

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
		validUpgrades.add(TEHelper.WIRELESS);
	}

	// ---END UPGRADE FUNCTIONS---
}
