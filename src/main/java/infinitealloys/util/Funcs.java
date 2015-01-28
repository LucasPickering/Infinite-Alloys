package infinitealloys.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.awt.Rectangle;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.registry.LanguageRegistry;
import infinitealloys.network.NetworkHandler;

public class Funcs {

  /**
   * Translate a number n into a radix, then get the digit at pos. The right-most position is 0 and
   * it increases to the left.
   *
   * @param n     the number that is being used
   * @param radix the radix of the number being given, e.g. 10 (decimal) or 2 (binary)
   * @param pos   the position of the digit to be found
   * @return the digit at pos
   */
  public static int intAtPos(int n, int radix, int pos) {
    return n / (int) (Math.pow(radix, pos) + 0.5F)
           % radix; // Adding 0.5F makes the cast round to the nearest int
  }

  /**
   * Get a localization or series of localization via keys. Add '/' to the start of a key to have it
   * added to the final string without being localized. e.g. getLoc("general.off", "/is not",
   * "general.on") would return "Off is not On" (If the language is English)
   *
   * @param keys the list of keys to be localized and spliced together into a final string
   * @return the final string of one or more concatenated literal and/or localized strings
   */
  public static String getLoc(String... keys) {
    String finalKey = "";
    for (String key : keys) {
      if (key.length() == 0) {
        continue;
      } else if (key.charAt(0) == '/') {
        finalKey += key.substring(1);
      } else {
        finalKey += LanguageRegistry.instance().getStringLocalization(key);
      }
    }
    return finalKey;
  }

  /**
   * Create a texture resource for an IA GUI based on the given texture name
   */
  public static ResourceLocation getGuiTexture(String texture) {
    return new ResourceLocation(Consts.MOD_ID, "textures/gui/" + texture + ".png");
  }

  /**
   * Bind the texture with the given resource to the render engine so that it can be used.
   * Convenience method for {@link net.minecraft.client.renderer.texture.TextureManager#bindTexture
   * TextureManager.bindTexture}
   */
  public static void bindTexture(ResourceLocation texture) {
    Minecraft.getMinecraft().renderEngine.bindTexture(texture);
  }

  /**
   * Convenience method for {@link net.minecraft.client.gui.Gui#drawTexturedModalRect
   * Gui.drawTecturedModalRect} with a Rectangle for texture's location and size.
   */
  public static void drawTexturedModalRect(Gui gui, int x, int y, Rectangle rect) {
    gui.drawTexturedModalRect(x, y, rect.x, rect.y, rect.width, rect.height);
  }

  /**
   * Given a mouse X and Y, is the mouse within a zone that starts at xStart, yStart
   */
  public static boolean mouseInZone(int mouseX, int mouseY, int xStart, int yStart, int width,
                                    int height) {
    return mouseX >= xStart && mouseY >= yStart && mouseX < xStart + width
           && mouseY < yStart + height;
  }

  /**
   * Reduce the values within an alloy, i.e. 44442222 becomes 22221111 Rightmost digits are the
   * lesser metals
   *
   * @param alloy the raw alloy data, before reduction
   * @return an alloy with reduced digits
   */
  public static int reduceAlloy(int alloy) {
    int gcf = 1;
    factors:
    for (int i = 2; i < Consts.ALLOY_RADIX;
         i++) { // Iterate over every integer in [2, Consts.ALLOY_RADIX)
      for (int j = 0; j < Consts.METAL_COUNT; j++) { // Iterate over every digit in the number
        final int metalAmt = intAtPos(alloy, Consts.ALLOY_RADIX, j);
        if (metalAmt == 0) {
          continue; // Go to the next metal if this one is 0
        } else if (i > metalAmt) {
          break factors; // Break the whole loop if the factors have exceeded one of the digits
        } else if (metalAmt % i != 0) {
          continue factors; // If i is not a factor of the digit of alloy at j, skip to the next factor
        }
      }
      gcf = i;
    }
    return alloy / gcf;
  }

  /**
   * Convenience method for getting a TE in a specific world when the coordinates are stored in a
   * {@link Point3}
   */
  public static TileEntity getTileEntity(World world, Point3 p) {
    return world.getTileEntity(p.x, p.y, p.z);
  }

  /**
   * Send a packet over the network to the server
   */
  public static void sendPacketToServer(IMessage message) {
    NetworkHandler.simpleNetworkWrapper.sendToServer(message);
  }

  /**
   * Send a packet over the network to a specific client
   */
  public static void sendPacketToPlayer(IMessage message, EntityPlayer player) {
    NetworkHandler.simpleNetworkWrapper.sendTo(message, (EntityPlayerMP) player);
  }

  /**
   * Send a packet over the network to all clients
   */
  public static void sendPacketToAllPlayers(IMessage message) {
    NetworkHandler.simpleNetworkWrapper.sendToAll(message);
  }

  /**
   * Shorten a full number to 3 digits with K, M, and B suffixes, e.g. 1411 become 1.41K and
   * 67,000,000 becomes 67.0M
   */
  public static String abbreviateNum(int n) {
    if (n >= 1000000000) // Billions
    {
      return String.format("%.3G", n / 1000000000F) + "B";
    } else if (n >= 1000000) // Millions
    {
      return String.format("%.3G", n / 1000000F) + "M";
    } else if (n >= 1000) // Thousands
    {
      return String.format("%.3G", n / 1000F) + "K";
    }
    return String.valueOf(n);
  }

  // @formatter:off
  /**
   * Convert an entity's yaw, on the range [-180, 180] to an EnumFacing value.
   * The mapping works as follows:
   * [-45, 45)   -> {@link net.minecraft.util.EnumFacing#SOUTH SOUTH}
   * [45, 135)   -> {@link net.minecraft.util.EnumFacing#WEST  WEST}
   * [135, -135) -> {@link net.minecraft.util.EnumFacing#NORTH NORTH}
   * [-135, -45) -> {@link net.minecraft.util.EnumFacing#EAST  EAST}
   *
   * @param yaw entity's yaw
   * @return the yaw as a compass direction
   * */
  //@formatter:on
  public static EnumFacing yawToFacing(float yaw) {
    int i = (int) Math.floor(yaw / 90F + 0.5F) & 3;
    switch (i) {
      case 0:
        return EnumFacing.SOUTH;
      case 1:
        return EnumFacing.WEST;
      case 2:
        return EnumFacing.NORTH;
      default:
        return EnumFacing.EAST;
    }
  }

  // @formatter:off
  /**
   * Convert an EnumFacing value to a yaw value.
   * The mapping works as follows:
   * {@link net.minecraft.util.EnumFacing#SOUTH SOUTH} -> 0
   * {@link net.minecraft.util.EnumFacing#WEST  WEST}  -> 90
   * {@link net.minecraft.util.EnumFacing#NORTH NORTH} -> 180
   * {@link net.minecraft.util.EnumFacing#EAST  EAST}  -> -90
   *
   * @param facing given compass direction
   * @return -90, 0, 90, or 180
   * */
  //@formatter:on
  public static float facingToYaw(EnumFacing facing) {
    switch (facing) {
      case WEST:
        return 90;
      case NORTH:
        return 180;
      case EAST:
        return -90;
      default:
        return 0;
    }
  }
}
