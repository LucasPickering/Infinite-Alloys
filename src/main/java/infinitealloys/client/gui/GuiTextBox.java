package infinitealloys.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Arrays;

/**
 * A text box with one or more lines of text, each with its own color
 */
final class GuiTextBox extends GuiScreen {

  private static final int DEFAULT_TEXT_COLOR = 0xffffff;
  private static final int X_OFFSET = 12;
  private static final int Y_OFFSET = -12;

  private int x;
  private int y;

  /**
   * The lines of text
   */
  private ColoredText[] lines;

  /**
   * Create a new text box, with each line being white.
   *
   * @param x    the x-pos for the box
   * @param y    the y-pos for the box
   * @param text each line to be drawn
   */
  GuiTextBox(int x, int y, String... text) {
    this(x, y,
         Arrays.stream(text).map(s -> new ColoredText(s, DEFAULT_TEXT_COLOR))
             .toArray(ColoredText[]::new));
  }

  /**
   * Create a new text box.
   *
   * @param x    the x-pos for the box
   * @param y    the y-pos for the box
   * @param text each line to be drawn
   */
  GuiTextBox(int x, int y, ColoredText... text) {
    final Minecraft mc = Minecraft.getMinecraft();
    setWorldAndResolution(mc, mc.displayWidth, mc.displayHeight);
    this.x = x + X_OFFSET;
    this.y = y + Y_OFFSET;
    this.lines = text;
  }

  void draw() {
    // Set the width of the box to the length of the longest line
    final int boxWidth = Arrays.stream(lines)
        .mapToInt(line -> fontRendererObj.getStringWidth(line.text)).max().orElse(0);
    final int boxHeight = lines.length <= 1 ? 8 : lines.length * 10;

    final int leftX = x - 3;
    final int rightX = x + boxWidth + 3;
    final int topY = y - 3;
    final int bottomY = y + boxHeight + 3;

    final int bgColor = 0xf0100010;
    drawRect(leftX, topY - 1, rightX, topY, bgColor); // Top
    drawRect(leftX, bottomY, rightX, bottomY + 1, bgColor); // Bottom
    drawRect(leftX, topY, rightX, bottomY, bgColor); // Middle
    drawRect(leftX - 1, topY, leftX, bottomY, bgColor); // Left
    drawRect(rightX, topY, rightX + 1, bottomY, bgColor); // Right

    int fgColor1 = 0x505000ff;
    int fgColor2 = 0x5028007f;
    drawGradientRect(leftX, y - 3, rightX, y - 3 + 1, fgColor1, fgColor1); // Top
    drawGradientRect(leftX, bottomY - 1, rightX, bottomY, fgColor2, fgColor2); // Bottom
    drawGradientRect(leftX, topY + 1, leftX + 1, bottomY - 1, fgColor1, fgColor2); // Left
    drawGradientRect(rightX - 1, topY + 1, rightX, bottomY - 1, fgColor1, fgColor2); // Right

    // Draw each line
    for (int i = 0; i < lines.length; i++) {
      fontRendererObj.drawStringWithShadow(lines[i].text,
                                           x, y + i * 10 + (i == 0 ? 0 : 2),
                                           lines[i].color);
    }

    zLevel = 0F;
    itemRender.zLevel = 0F;
  }

  void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  void setText(int lineNum, String text) {
    lines[lineNum].text = text;
  }

  void setColor(int lineNum, int color) {
    lines[lineNum].color = color;
  }
}
