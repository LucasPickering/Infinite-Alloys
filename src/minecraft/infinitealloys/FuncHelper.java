package infinitealloys;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class FuncHelper {

	public static Block getBlock(int id) {
		return Block.blocksList[id];
	}

	public static Block getBlock(World world, int x, int y, int z) {
		return getBlock(world.getBlockId(x, y, z));
	}
}
