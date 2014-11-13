package infinitealloys.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

/** A text box with one or more lines of text, each with its own color */
class GuiTextBox extends GuiScreen {

	private int x;
	private int y;
	/** The lines of text */
	private final ColoredText[] lines;

	/** Create a new text box, with all lines of text being black. */
	public GuiTextBox(FontRenderer fontRenderer, int x, int y, String... strs) {
		this.fontRendererObj = fontRenderer;
		ColoredText[] lines = new ColoredText[strs.length];
		for(int i = 0; i < strs.length; i++)
			lines[i] = new ColoredText(strs[i], 0xffffff);
		this.x = x;
		this.y = y;
		this.lines = lines;
	}

	/** Create a new text box. */
	public GuiTextBox(FontRenderer fontRenderer, int x, int y, ColoredText... text) {
		this.fontRendererObj = fontRenderer;
		this.x = x;
		this.y = y;
		this.lines = text;
	}

	void draw() {
		int drawingX = x;
		int drawingY = y;
		// Set the width of the box to the length of the longest line
		int boxWidth = 0;
		for(ColoredText line : lines)
			boxWidth = Math.max(boxWidth, fontRendererObj.getStringWidth(line.text));

		// This is from vanilla, I have no idea what it does, other than make it work
		drawingX += 12;
		drawingY -= 12;
		int var9 = 8;
		if(lines.length > 1)
			var9 += 2 + (lines.length - 1) * 10;
		int var10 = -267386864;
		drawGradientRect(drawingX - 3, drawingY - 4, drawingX + boxWidth + 3, drawingY - 3, var10, var10);
		drawGradientRect(drawingX - 3, drawingY + var9 + 3, drawingX + boxWidth + 3, drawingY + var9 + 4, var10, var10);
		drawGradientRect(drawingX - 3, drawingY - 3, drawingX + boxWidth + 3, drawingY + var9 + 3, var10, var10);
		drawGradientRect(drawingX - 4, drawingY - 3, drawingX - 3, drawingY + var9 + 3, var10, var10);
		drawGradientRect(drawingX + boxWidth + 3, drawingY - 3, drawingX + boxWidth + 4, drawingY + var9 + 3, var10, var10);
		int var11 = 1347420415;
		int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
		drawGradientRect(drawingX - 3, drawingY - 3 + 1, drawingX - 3 + 1, drawingY + var9 + 3 - 1, var11, var12);
		drawGradientRect(drawingX + boxWidth + 2, drawingY - 3 + 1, drawingX + boxWidth + 3, drawingY + var9 + 3 - 1, var11, var12);
		drawGradientRect(drawingX - 3, drawingY - 3, drawingX + boxWidth + 3, drawingY - 3 + 1, var11, var11);
		drawGradientRect(drawingX - 3, drawingY + var9 + 2, drawingX + boxWidth + 3, drawingY + var9 + 3, var12, var12);
		// The vanilla stuff stops here

		// Draw each line
		for(int i = 0; i < lines.length; i++)
			fontRendererObj.drawStringWithShadow(lines[i].text, drawingX, drawingY + i * 10 + (i == 0 ? 0 : 2), lines[i].color);

		zLevel = 0F;
		itemRender.zLevel = 0F;
	}

	void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

}

class ColoredText {
	/** The line's text */
	String text;
	/** The line's hexadecimal color */
	int color;

	ColoredText(String text, int color) {
		this.text = text;
		this.color = color;
	}
}
