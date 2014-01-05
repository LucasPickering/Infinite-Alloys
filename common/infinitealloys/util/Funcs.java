package infinitealloys.util;

import infinitealloys.core.InfiniteAlloys;
import java.awt.Rectangle;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Funcs {

	public static Block getBlock(int id) {
		return Block.blocksList[id];
	}

	public static Block getBlock(World world, int x, int y, int z) {
		return getBlock(world.getBlockId(x, y, z));
	}

	/** Translate a number n into a radix, then get the digit at pos. The right-most position is 0 and the index increases to the left.
	 * 
	 * @param n the number that is being used
	 * @param radix the radix of the number being given, e.g. 10 (decimal) or 2 (binary)\
	 * @param pos the position of the digit to be found
	 * @return the digit at pos, after adding leading zeros to make it length strlen */
	public static int intAtPos(int n, int radix, int pos) {
		if(pos != 0)
			n /= (Math.pow(radix, pos));
		return n % radix;
	}

	/** Take the log-base-b of x, using the change of base formula: log-base-b(x) = ln(x)/ln(b)
	 * 
	 * @param b the base of the logarithm
	 * @param x the number to be used */
	public static double logn(int b, double x) {
		return Math.log(x) / Math.log(b);
	}

	/** Get a localization or series of localization with keys. Add '/' to the start of a key to have it added to the final string without being localized. e.g.
	 * getLoc("general.off", "/is not", "general.on") would return "Off is not On"
	 * 
	 * @param keys the list of keys to be localized and spliced together into a final string */
	public static String getLoc(String... keys) {
		String finalKey = "";
		for(final String key : keys) {
			if(key.length() == 0)
				continue;
			if(key.charAt(0) == '/')
				finalKey += key.substring(1);
			else
				finalKey += LanguageRegistry.instance().getStringLocalization(key);
		}
		return finalKey;
	}

	/** Convert a Vanilla MC block face number to a ForgeDirection */
	public static ForgeDirection numToFDSide(byte num) {
		switch(num) {
			case Consts.TOP:
				return ForgeDirection.UP;
			case Consts.BOTTOM:
				return ForgeDirection.DOWN;
			case Consts.EAST:
				return ForgeDirection.EAST;
			case Consts.WEST:
				return ForgeDirection.WEST;
			case Consts.NORTH:
				return ForgeDirection.NORTH;
			case Consts.SOUTH:
				return ForgeDirection.SOUTH;
			default:
				return ForgeDirection.UNKNOWN;
		}
	}

	/** Convert a ForgeDirection to a Vanilla MC block face number */
	public static byte fdToNumSide(ForgeDirection fd) {
		switch(fd) {
			case UP:
				return Consts.TOP;
			case DOWN:
				return Consts.BOTTOM;
			case EAST:
				return Consts.EAST;
			case WEST:
				return Consts.WEST;
			case NORTH:
				return Consts.NORTH;
			case SOUTH:
				return Consts.SOUTH;
			default:
				return -1;
		}
	}

	/** Convert an entity's yaw to a Vanilla MC block face number */
	public static byte yawToNumSide(int rotation) {
		switch(rotation) {
			case 0:
				return Consts.SOUTH;
			case 1:
				return Consts.WEST;
			case 2:
				return Consts.NORTH;
			case 3:
				return Consts.EAST;
			default:
				return -1;
		}
	}

	/** See if the running side is client
	 * 
	 * @return true if the running side is client */
	public static boolean isClient() {
		return FMLCommonHandler.instance().getEffectiveSide().isClient();
	}

	/** See if the running side is server
	 * 
	 * @return true if the running side is server */
	public static boolean isServer() {
		return FMLCommonHandler.instance().getEffectiveSide().isServer();
	}

	/** Check if the block at x, y, z is of a certain type
	 * 
	 * @return true if block at x, y, z is equals id and metadata */
	public static boolean blocksEqual(World world, int id, int metadata, int x, int y, int z) {
		return world.getBlockId(x, y, z) == id && world.getBlockMetadata(x, y, z) == metadata;
	}

	/** Get the array of ints that represents the valid alloys */
	public static int[] getValidAlloys() {
		return InfiniteAlloys.instance.worldData.getValidAlloys();
	}

	/** Get an instance of a player from their name */
	public static Player getPlayerForUsername(String name) {
		return (Player)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(name);
	}

	/** Create a texture resource for an IA GUI based on the given texture name */
	public static ResourceLocation getGuiTexture(String texture) {
		return new ResourceLocation(Consts.TEXTURE_DOMAIN, "textures/gui/" + texture + ".png");
	}

	/** Bind the texture with the given resource to the render engine so that it can be used Convenience method for
	 * {@link net.minecraft.client.renderer.texture.TextureManager#bindTexture TextureManager.bindTexture} */
	public static void bindTexture(ResourceLocation texture) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}

	/** Convenience method for {@link net.minecraft.client.gui.Gui#drawTexturedModalRect(int, int, int, int, int, int) Gui.drawTecturedModalRect} with a
	 * Rectangle for the last four args */
	public static void drawTexturedModalRect(Gui gui, int x, int y, Rectangle rect) {
		gui.drawTexturedModalRect(x, y, rect.x, rect.y, rect.width, rect.height);
	}

	/** Given a mouse X and Y, is the mouse within a zone that starts at xStart, yStart */
	public static boolean mouseInZone(int mouseX, int mouseY, int xStart, int yStart, int width, int height) {
		return mouseX >= xStart && mouseY >= yStart && mouseX < xStart + width && mouseY < yStart + height;
	}

	/** Reduce the values within an alloy, i.e. 44442222 becomes 22221111
	 * Rightmost digits are the lesser metals
	 * 
	 * @param alloy the raw alloy data, before reduction
	 * @return an alloy with reduced digits */
	public static int reduceAlloy(int alloy) {
		int gcf = 1;
		factors:
		for(int i = 2; i < Consts.ALLOY_RADIX; i++) { // Iterate over every integer in [2, Consts.ALLOY_RADIX)
			for(int j = 0; j < Consts.METAL_COUNT; j++) { // Iterate over every digit in the number
				final int metalAmt = intAtPos(alloy, Consts.ALLOY_RADIX, j);
				if(metalAmt == 0)
					continue; // Go to the next metal if this one is 0
				else if(i > metalAmt)
					break factors; // Break the whole loop if the factors have exceeded one of the digits
				else if(metalAmt % i != 0)
					continue factors; // If i is not a factor of the digit of alloy at j, skip to the next factor
			}
			gcf = i;
		}
		return alloy / gcf;
	}
}
