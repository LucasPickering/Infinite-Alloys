package infinitealloys.core;

import net.minecraft.block.Block;
import net.minecraft.world.World;
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
}