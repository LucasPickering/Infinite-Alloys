package infinitealloys.block;

import infinitealloys.util.Consts;
import javax.swing.Icon;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class Blocks {

	public static int oreID;
	public static int machineID;

	public static Block ore;
	public static Block machine;

	public static IIcon[][] machineIcons = new IIcon[Consts.MACHINE_COUNT][4];
	public static IIcon oreForegroundIcon;
	public static IIcon oreBackgroundIcon;
}
