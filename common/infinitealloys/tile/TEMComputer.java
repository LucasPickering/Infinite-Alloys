package infinitealloys.tile;

import infinitealloys.block.Blocks;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TEMComputer extends TileEntityMachine {

	/** The amount of time, in ticks (20 ticks = 1 second), between each regular search for new machines to connect to. */
	private final int SEARCH_INTERVAL = 200;

	/** That amount of machines that the computer can control */
	private int networkCapacity;

	/** The range that the computer searches for new machines */
	private int autoSearchRange;

	/** The max range that machines can be added at with the Internet Wand */
	private int maxRange;

	/** 3D coords for each machine that is connected to the computer */
	public List<Point> connectedMachines;

	/** How many ticks have passed since the last search for machines. When this reaches {@link #SEARCH_INTERVAL the search interval time}, it resets to 0 and a
	 * search begins. */
	private int currentSearchTicks;

	/** Should the machine periodically search for machines within a certain range? */
	public boolean autoSearch;

	/** The last point that was checked for a machine in the previous iteration of {@link #search}. The coords are relative to this TE block. */
	private Point lastSearch;

	/** Should searching continue, or is it complete. Set this to true to begin a search. */
	public boolean shouldSearch;

	public TEMComputer(int facing) {
		this();
		front = facing;
	}

	public TEMComputer() {
		super();
		inventoryStacks = new ItemStack[1];
		networkCapacity = 3;
		connectedMachines = new ArrayList<Point>(networkCapacity);
	}

	@Override
	public int getID() {
		return MachineHelper.COMPUTER;
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

		// If a connected machine no longer exists or its networking upgrade was removed, remove it from the network list
		for(Point p : connectedMachines)
			if(!(worldObj.getBlockTileEntity(p.x, p.y, p.z) instanceof TileEntityMachine && ((TileEntityMachine)worldObj.getBlockTileEntity(p.x, p.y, p.z)).hasUpgrade(MachineHelper.WIRELESS)))
				connectedMachines.remove(p);
	}

	/** Perform a search for machines that can be controlled. This checks {@link infinitealloys.util.MachineHelper#SEARCH_PER_TICK a set amount of} blocks in a
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
					if(te instanceof TileEntityMachine && !(te instanceof TEMComputer) && hasUpgrade(MachineHelper.WIRELESS))
						connectedMachines.add(new Point(xCoord + x, yCoord + y, zCoord + z));
	
					// If the amounts of blocks search this tick has reached the limit, save our place and end the function. The search will be
					// continued next tick.
					if(++blocksSearched >= MachineHelper.SEARCH_PER_TICK) {
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

	public boolean addMachine(EntityPlayer player, int temX, int temY, int temZ) {
		for(Point coords : connectedMachines) {
			if(coords.x == temX && coords.y == temY && coords.z == temZ) {
				if(worldObj.isRemote)
					player.addChatMessage("Error: Machine is already in network");
				return false;
			}
		}
		if(connectedMachines.size() >= networkCapacity) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Network full");
		}
		else if(worldObj.getBlockId(temX, temY, temZ) != Blocks.machine.blockID) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Block is not capable of networking");
		}
		else if(temX == xCoord && temY == yCoord && temZ == zCoord) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Cannot add self to network");
		}
		else if(new Point(temX, temY, temZ).distanceTo(xCoord, yCoord, zCoord) > maxRange) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Block out of range");
		}
		else if(!((TileEntityMachine)worldObj.getBlockTileEntity(temX, temY, temZ)).hasUpgrade(MachineHelper.WIRELESS)) {
			if(worldObj.isRemote)
				player.addChatMessage("Error: Block does not have a networking upgrade");
		}
		else {
			connectedMachines.add(new Point(temX, temY, temZ));
			return true;
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		for(int i = 0; i < tagCompound.getInteger("NetworkSize"); i++) {
			int[] coords = tagCompound.getIntArray("Coords" + i);
			connectedMachines.add(new Point(coords[0], coords[1], coords[2]));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("NetworkSize", connectedMachines.size());
		for(int i = 0; i < connectedMachines.size(); i++) {
			Point coords = connectedMachines.get(i);
			tagCompound.setIntArray("Coords" + i, new int[] { coords.x, coords.y, coords.z });
		}
	}

	public void handlePacketDataFromServer(ArrayList networkCoords) {
		this.connectedMachines = networkCoords;
	}

	public void handlePacketDataFromClient(boolean autoSearch) {
		this.autoSearch = autoSearch;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(MachineHelper.CAPACITY2))
			networkCapacity = 10;
		else if(hasUpgrade(MachineHelper.CAPACITY1))
			networkCapacity = 6;
		else
			networkCapacity = 3;

		if(hasUpgrade(MachineHelper.RANGE2)) {
			autoSearchRange = 20;
			maxRange = 60;
		}
		else if(hasUpgrade(MachineHelper.RANGE1)) {
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
		validUpgrades.add(MachineHelper.CAPACITY1);
		validUpgrades.add(MachineHelper.CAPACITY2);
		validUpgrades.add(MachineHelper.RANGE1);
		validUpgrades.add(MachineHelper.RANGE2);
		validUpgrades.add(MachineHelper.WIRELESS);
	}
}
