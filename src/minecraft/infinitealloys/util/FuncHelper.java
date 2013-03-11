package infinitealloys.util;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class FuncHelper {

	public static Block getBlock(int id) {
		return Block.blocksList[id];
	}

	public static Block getBlock(World world, int x, int y, int z) {
		return getBlock(world.getBlockId(x, y, z));
	}

	public static int intAtPos(int radix, int strlen, int n, int pos) {
		return Character.digit(addLeadingZeros(Integer.toString(n, radix), strlen).charAt(pos), radix);
	}

	public static double logn(int base, double num) {
		return Math.log(num) / Math.log(base);
	}

	public static String addLeadingZeros(String s, int finalSize) {
		s.trim();
		int length = s.length();
		for(int i = 0; i < finalSize - length; i++)
			s = "0" + s;
		return s;
	}

	public static String getLoc(String key) {
		return LanguageRegistry.instance().getStringLocalization(key);
	}

	// TODO: Fix this
	/** Convert a Vanilla MC block face int to a ForgeDirection, NOT WORKING YET */
	public static ForgeDirection sideToFD(int side) {
		switch(side) {
			case 0:
				return ForgeDirection.getOrientation(0);
			case 1:
				return ForgeDirection.getOrientation(1);
			case 2:
				return ForgeDirection.getOrientation(2);
			case 3:
				return ForgeDirection.getOrientation(3);
			case 4:
				return ForgeDirection.getOrientation(4);
			case 5:
				return ForgeDirection.getOrientation(5);
		}
		return null;
	}

	// TODO: Fix this
	/** Convert a Vanilla MC block face int to a ForgeDirection, NOT WORKING YET */
	public static int fdToSide(ForgeDirection fd) {
		return fd.ordinal();
	}
}