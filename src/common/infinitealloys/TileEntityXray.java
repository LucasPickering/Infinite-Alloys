package infinitealloys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import net.minecraft.src.ItemStack;

public class TileEntityXray extends TileEntityMachine {

	private static HashMap<ItemStack, Integer> detectables = new HashMap<ItemStack, Integer>();
	private ArrayList<Point> detectedBlocks = new ArrayList<Point>();
	public int range;
	private Point lastSearch;

	public TileEntityXray(byte facing) {
		this();
		orientation = facing;
	}

	public TileEntityXray() {
		super(1);
		inventoryStacks = new ItemStack[2];
		range = 3;
		lastSearch = new Point(0, 0, 0);
	}

	public static void addDetectable(ItemStack detectable, int value) {
		detectables.put(detectable, new Integer(value));
	}

	public static boolean isDetectable(ItemStack stack) {
		return detectables.containsKey(stack);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!lastSearch.equals(0, 0, 0))
			search();
	}

	public void search() {
		if(inventoryStacks[0] == null) return;
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
	public String getInvName() {
		return "Xray";
	}

	@Override
	protected void updateUpgrades() {}
}
