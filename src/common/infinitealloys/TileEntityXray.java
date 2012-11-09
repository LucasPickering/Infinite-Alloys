package infinitealloys;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.src.ItemStack;

public class TileEntityXray extends TileEntityMachine {

	private static HashMap<ItemStack, Integer> detectables = new HashMap<ItemStack, Integer>();
	private ArrayList<Point> scannedBlocks = new ArrayList<Point>();

	public TileEntityXray(byte facing) {
		this();
		orientation = facing;
	}

	public TileEntityXray() {
		super(1);
		inventoryStacks = new ItemStack[2];
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

	@Override
	public String getInvName() {
		return "Xray";
	}

	@Override
	protected void updateUpgrades() {}
}
