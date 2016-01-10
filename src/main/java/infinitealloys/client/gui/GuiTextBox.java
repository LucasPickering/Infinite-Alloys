package infinitealloys.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Arrays;

/**
 * A text box with one or more lines of text, each with its own color
 */
final class GuiTextBox extends GuiScreen {

  private int x;
  private int y;
  /**
   * The lines of text
   */
  private final ColoredText[] lines;

  /**
   * Create a new text box, with each line being white.
   *
   * @param x            the x-pos for the box
   * @param y            the y-pos for the box
   * @param text         each line to be drawn
   */
  public GuiTextBox(int x, int y, String... text) {
    this(x, y,
         Arrays.stream(text).map(s -> new ColoredText(s, 0xffffff)).toArray(ColoredText[]::new));
  }

  /**
   * Create a new text box.
   *
   * @param x            the x-pos for the box
   * @param y            the y-pos for the box
   * @param text         each line to be drawn
   */
  public GuiTextBox(int x, int y, ColoredText... text) {
    final Minecraft mc = Minecraft.getMinecraft();
    setWorldAndResolution(mc, mc.displayWidth, mc.displayHeight);
    this.x = x;
    this.y = y;
    this.lines = text;
  }

  void draw() {
    int drawingX = x;
    int drawingY = y;
    // Set the width of the box to the length of the longest line
    int boxWidth = 0;
    for (ColoredText line : lines) {
      boxWidth = Math.max(boxWidth, fontRendererObj.getStringWidth(line.text));
    }

    // This is from vanilla, I have no idea what it does, other than make it work
    drawingX += 12;
    drawingY -= 12;
    int var9 = 8;
    if (lines.length > 1) {
      var9 += 2 + (lines.length - 1) * 10;
    }
    int var10 = -267386864;
    drawGradientRect(drawingX - 3, drawingY - 4, drawingX + boxWidth + 3, drawingY - 3, var10,
                     var10);
    drawGradientRect(drawingX - 3, drawingY + var9 + 3, drawingX + boxWidth + 3,
                     drawingY + var9 + 4, var10, var10);
    drawGradientRect(drawingX - 3, drawingY - 3, drawingX + boxWidth + 3, drawingY + var9 + 3,
                     var10, var10);
    drawGradientRect(drawingX - 4, drawingY - 3, drawingX - 3, drawingY + var9 + 3, var10, var10);
    drawGradientRect(drawingX + boxWidth + 3, drawingY - 3, drawingX + boxWidth + 4,
                     drawingY + var9 + 3, var10, var10);
    int var11 = 1347420415;
    int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
    drawGradientRect(drawingX - 3, drawingY - 3 + 1, drawingX - 3 + 1, drawingY + var9 + 3 - 1,
                     var11, var12);
    drawGradientRect(drawingX + boxWidth + 2, drawingY - 3 + 1, drawingX + boxWidth + 3,
                     drawingY + var9 + 3 - 1, var11, var12);
    drawGradientRect(drawingX - 3, drawingY - 3, drawingX + boxWidth + 3, drawingY - 3 + 1, var11,
                     var11);
    drawGradientRect(drawingX - 3, drawingY + var9 + 2, drawingX + boxWidth + 3,
                     drawingY + var9 + 3, var12, var12);
    // The vanilla stuff stops here

    // Draw each line
    for (int i = 0; i < lines.length; i++) {
      fontRendererObj
          .drawStringWithShadow(lines[i].text, drawingX, drawingY + i * 10 + (i == 0 ? 0 : 2),
                                lines[i].color);
    }

    zLevel = 0F;
    itemRender.zLevel = 0F;
  }

  void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }
}
