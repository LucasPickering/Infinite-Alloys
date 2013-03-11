package infinitealloys.block;

import infinitealloys.core.References;
import net.minecraft.block.Block;
import net.minecraft.util.Icon;

public class Blocks {

	public static int oreID;
	public static int machineID;

	public static Block ore;
	public static Block machine;

	public static Icon oreIcon;
	public static Icon[][] machineIcons = new Icon[References.MACHINE_COUNT][3];
}
