package infinitealloys;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.src.ItemStack;

public class TileEntityXray extends TileEntityMachine {

	private static HashMap<ItemStack, Integer> detectables = new HashMap<ItemStack, Integer>();
	private ArrayList<Point> detectedBlocks = new ArrayList<Point>();
	public int range;

	public TileEntityXray(byte facing) {
		this();
		orientation = facing;
	}

	public TileEntityXray() {
		super(1);
		inventoryStacks = new ItemStack[2];
		range = 10;
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
	}

	public void search() {
		if(inventoryStacks[0] == null)
			return;
		int targetID = inventoryStacks[0].itemID;
		int targetMetadata = inventoryStacks[0].getItemDamage();
		for(int y = yCoord - 1; y >= 0; y++) {
			for(int x = 0; x < range; x++) {
				for(int z = 0; z < range; z++) {
					for(int i = 0; i < 2; i++) {
						for(int j = 0; j < 2; j++) {
							int searchX = xCoord + (i == 0 ? x : -x);
							int searchZ = zCoord + (j == 0 ? z : -z);
							if(worldObj.getBlockId(searchX, y, searchZ) != targetID || worldObj.getBlockMetadata(searchX, y, searchZ) != targetMetadata)
								continue;
							detectedBlocks.add(new Point(searchX, y, searchZ));
						}
					}
				}
			}
		}
	}

	@Override
	public String getInvName() {
		return "Xray";
	}

	@Override
	protected void updateUpgrades() {}
}
