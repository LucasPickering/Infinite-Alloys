package infinitealloys.tile;

import infinitealloys.handlers.PacketHandler;
import infinitealloys.util.Point;
import infinitealloys.util.Consts;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityXray extends TileEntityMachine {

	/** A list of the detected blocks, x and z are relative to the machine, y is absolute */
	private ArrayList<Point> detectedBlocks = new ArrayList<Point>();
	public int range;
	private Point lastSearch;

	/** The selected button for the user, client-side only */
	@SideOnly(Side.CLIENT)
	public int selectedButton = -1;

	/** The selected button on the gui for each player */
	public HashMap<String, Short> selectedButtons = new HashMap<String, Short>();

	/** Is it searching client-side. Does not necessarily mean the x-ray is running a search, only that the user sees a loading progress bar */
	public boolean searching;

	public TileEntityXray(int facing) {
		this();
		front = facing;
	}

	public TileEntityXray() {
		super(1);
		inventoryStacks = new ItemStack[2];
		stackLimit = 1;
		lastSearch = new Point(0, 0, 0);
	}

	@Override
	public String getInvName() {
		return "X-ray";
	}

	@Override
	public int func_94128_d(int side) {
		if(side != front && (side == Consts.NORTH || side == Consts.SOUTH || side == Consts.EAST || side == Consts.WEST))
			return 1;
		return 0;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(inventoryStacks[0] == null)
			selectedButtons.clear();
		else if(!lastSearch.equals(0, 0, 0))
			search();
		if(searching && ++processProgress >= ticksToProcess) {
			processProgress = 0;
			searching = false;
			PacketDispatcher.sendPacketToAllPlayers(PacketHandler.getTEPacketToClient(this));
		}
	}

	public void search() {
		if(inventoryStacks[0] == null)
			return;
		int targetID = inventoryStacks[0].itemID;
		int targetMetadata = inventoryStacks[0].getItemDamage();
		int blocksSearched = 0;
		if(lastSearch.equals(0, 0, 0))
			detectedBlocks.clear();
		for(int y = lastSearch.y; y >= -yCoord; y--) {
			for(int x = Math.abs(lastSearch.x); x <= range; x++) {
				for(int z = Math.abs(lastSearch.z); z <= range; z++) {
					for(int i = x == 0 ? 1 : 0; i < 2; i++) {
						for(int j = z == 0 ? 1 : 0; j < 2; j++) {
							int xRel = i == 0 ? x : -x;
							int zRel = j == 0 ? z : -z;
							if(worldObj.getBlockId(xCoord + xRel, yCoord + y, zCoord + zRel) == targetID && worldObj.getBlockMetadata(xCoord + xRel, yCoord + y, zCoord + zRel) == targetMetadata)
								detectedBlocks.add(new Point(xRel, yCoord + y, zRel));
							if(++blocksSearched >= TEHelper.SEARCH_PER_TICK) {
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

	public void clearDetectedBlocks() {
		detectedBlocks.clear();
	}

	public void addDetectedBlock(Point p) {
		detectedBlocks.add(p);
	}

	public void handlePacketDataFromClient(boolean searching, String playerName, short selectedButton) {
		this.searching = searching;
		if(selectedButton != -1)
			selectedButtons.put(playerName, selectedButton);
		else
			selectedButtons.remove(playerName);
	}

	@Override
	public boolean shouldProcess() {
		return false;
	}

	@Override
	public void finishProcessing() {
	}

	@Override
	public int getJoulesUsed() {
		if(searching && inventoryStacks[0] != null)
			return joulesUsedPerTick * TEHelper.getDetectableWorth(inventoryStacks[0]);
		return 0;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.SPEED2))
			ticksToProcess = 12000;
		else if(hasUpgrade(TEHelper.SPEED1))
			ticksToProcess = 18000;
		else
			ticksToProcess = 24; // TODO: Change this back to 24000

		if(hasUpgrade(TEHelper.EFFICIENCY2))
			joulesUsedPerTick = 1800;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			joulesUsedPerTick = 2700;
		else
			joulesUsedPerTick = 0; // TODO: Change this back to 3600

		if(hasUpgrade(TEHelper.RANGE2))
			range = 10;
		else if(hasUpgrade(TEHelper.RANGE1))
			range = 8;
		else
			range = 5;

		canNetwork = hasUpgrade(TEHelper.WIRELESS);

		if(hasUpgrade(TEHelper.ELECCAPACITY2))
			maxJoules = 1000000;
		else if(hasUpgrade(TEHelper.ELECCAPACITY1))
			maxJoules = 750000;
		else
			maxJoules = 500000;
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
