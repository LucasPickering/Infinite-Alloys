package infinitealloys.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

import java.awt.*;

import infinitealloys.network.NetworkHandler;
import io.netty.buffer.ByteBuf;

public final class Funcs {

  public static void registerBlock(Block block, Class<? extends ItemBlock> itemClass,
                                   String unlocalizedName) {
    block.setUnlocalizedName(unlocalizedName);
    GameRegistry.registerBlock(block, itemClass, unlocalizedName);
  }

  public static void registerBlockModel(Block block, String modelName) {
    registerItemModel(Item.getItemFromBlock(block), 0, modelName);
  }

  public static void registerBlockModel(Block block, int subType, String modelName) {
    registerItemModel(Item.getItemFromBlock(block), subType, modelName);
  }

  public static void registerItem(Item item, String unlocalizedName) {
    item.setUnlocalizedName(unlocalizedName);
    GameRegistry.registerItem(item, unlocalizedName);
  }

  public static void registerItemModel(Item item, String modelName) {
    registerItemModel(item, 0, modelName);
  }

  public static void registerItemModel(Item item, int subType, String modelName) {
    Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
        .register(item, subType,
                  new ModelResourceLocation(Consts.MOD_ID + ":" + modelName, "inventory"));
  }

  /**
   * Translate a number n into a radix, then get the digit at pos. The right-most position is 0 and it
   * increases to the left. If the given number is negative, the output will be negative.
   *
   * @param n     the number that is being used
   * @param radix the radix of the number being given, e.g. 10 (decimal) or 2 (binary)
   * @param pos   the position of the digit to be found
   * @return the digit at pos
   */
  public static int intAtPos(int n, int radix, int pos) {
    // Adding 0.5F makes the cast round to the nearest int
    return n / (int) (Math.pow(radix, pos) + 0.5F) % radix;
  }

  /**
   * Get a localization or series of localizations via keys. Add '/' to the start of a key to have it
   * added to the final string without being localized. e.g. getLoc("general.off", "/is not",
   * "general.on") would return "Off is not On" if the language is English.
   *
   * @param keys the list of keys to be localized and spliced together into a final string
   * @return the final string of one or more concatenated literal and/or localized strings
   */
  public static String getLoc(String... keys) {
    String finalKey = "";
    for (String key : keys) {
      if (key.length() > 0 && key.charAt(0) == '/') {
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
   * Convenience method for {@link net.minecraft.client.gui.Gui#drawTexturedModalRect
   * Gui.drawTecturedModalRect} with a Rectangle for texture's location and size.
   */
  public static void drawTexturedModalRect(Gui gui, int x, int y, Rectangle rect) {
    gui.drawTexturedModalRect(x, y, rect.x, rect.y, rect.width, rect.height);
  }

  /**
   * Given a mouse X and Y, is the mouse within a zone that starts at {@code xStart}, {@code yStart},
   * is {@code width} wide and {@code height} high? All four bounds are inclusive.
   *
   * @param mouseX the x-coordinate of the mouse
   * @param mouseY the y-coordinate of the mouse
   * @param xStart the left edge of the zone
   * @param yStart the top edge of the zone
   * @param width  the width of the zone
   * @param height the height of the zone
   * @return is (mouseX, mouseY) inclusively within the zone?
   */
  public static boolean mouseInZone(int mouseX, int mouseY, int xStart, int yStart, int width,
                                    int height) {
    return xStart <= mouseX && mouseX <= xStart + width
           && yStart <= mouseY && mouseY <= yStart + height;
  }

  /**
   * Reduce the values within an alloy, i.e. 44442222 becomes 22221111 Rightmost digits are the lesser
   * metals.
   *
   * @param alloy the raw alloy data, before reduction
   * @return an alloy with reduced digits
   */
  public static int reduceAlloy(int alloy) {
    int gcf = 1;

    // Iterate over every integer in [2, Consts.ALLOY_RADIX), i.e. every digit that can be reduced.
    factors:
    for (int i = 2; i < Consts.ALLOY_RADIX; i++) {
      // Iterate over every digit in the alloy
      for (int j = 0; j < Consts.METAL_COUNT; j++) {
        final int metalAmt = intAtPos(alloy, Consts.ALLOY_RADIX, j);
        if (0 < metalAmt && metalAmt < i) {
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
   * Shorten a full number to 3 digits and add K, M, or B as an appropriate suffix, with the
   * right-most digit always being rounded down. Abbreviation uses the following rules:
   *
   * <ul> <li>For {@code n < 1000}, nothing changes</li> <li>For {@code 1000 <= n < 1,000,000}, the
   * suffix is "K"</li> <li>For {@code 1, 000, 000 <= n < 1,000,000,000}, the suffix is "M"</li>
   * <li>For {@code n > 1,000,000,000}, the suffix is "B"</li> <li>Rounding is applied according to
   * {@link String#format}'s 'g' specifier</li> </ul>
   *
   * @param n the number to be abbreviated
   * @return the abbreviated number, represented as a {@link String}
   */
  public static String abbreviateNum(int n) {
    if (n >= 1000000000) { // Billions
      return String.format("%.3gB", n / 1000000000F);
    } else if (n >= 1000000) { // Millions
      return String.format("%.3gM", n / 1000000F);
    } else if (n >= 1000) { // Thousands
      return String.format("%.3gK", n / 1000F);
    }
    return String.valueOf(n);
  }

  /**
   * Convert an entity's yaw, on the range [-180, 180] to an EnumFacing value. The mapping works as
   * follows: [-45, 45)   -> {@link net.minecraft.util.EnumFacing#SOUTH SOUTH} [45, 135)   -> {@link
   * net.minecraft.util.EnumFacing#WEST  WEST} [135, -135) -> {@link net.minecraft.util.EnumFacing#NORTH
   * NORTH} [-135, -45) -> {@link net.minecraft.util.EnumFacing#EAST  EAST}
   *
   * @param yaw entity's yaw
   * @return the yaw as a compass direction
   */
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

  /**
   * Convert an EnumFacing value to a yaw value. The mapping works as follows: {@link
   * net.minecraft.util.EnumFacing#SOUTH SOUTH} -> 0 {@link net.minecraft.util.EnumFacing#WEST WEST}
   * -> 90 {@link net.minecraft.util.EnumFacing#NORTH NORTH} -> 180 {@link
   * net.minecraft.util.EnumFacing#EAST  EAST}  -> -90
   *
   * @param facing given compass direction
   * @return -90, 0, 90, or 180
   */
  public static int facingToYaw(EnumFacing facing) {
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

  /**
   * Create a new {@code BlockPos} instance from the next three values in the given {@link ByteBuf}
   *
   * @param bytes the {@link ByteBuf} to be read from
   * @return a new {@code BlockPos} with the values from {@code bytes}
   */
  public static BlockPos readBlockPosFromByteBuf(ByteBuf bytes) {
    return new BlockPos(bytes.readInt(), bytes.readInt(), bytes.readInt());
  }

  /**
   * Write this {@code BlockPos}'s data to the given {@link io.netty.buffer.ByteBuf}
   *
   * @param bytes the {@link io.netty.buffer.ByteBuf} to be written to
   */
  public static void writeBlockPosToByteBuf(ByteBuf bytes, BlockPos pos) {
    bytes.writeInt(pos.getX());
    bytes.writeInt(pos.getY());
    bytes.writeInt(pos.getZ());
  }
}
