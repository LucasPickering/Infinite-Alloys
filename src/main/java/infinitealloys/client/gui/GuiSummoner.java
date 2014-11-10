package infinitealloys.client.gui;

import infinitealloys.client.EnumHelp;
import infinitealloys.client.gui.GuiMachine.ColoredLine;
import infinitealloys.item.IAItems;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.network.MessageWand;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiSummoner extends GuiScreen {

	/** The background texture */
	protected ResourceLocation background = Funcs.getGuiTexture("summoner");

	/** Coordinates of the top-left corner of the GUI */
	protected java.awt.Point topLeft = new java.awt.Point();

	/** When help is enabled, slots get a colored outline and a mouse-over description */
	private boolean helpEnabled;

	private final int WIDTH = 100;
	private final int HEIGHT = 100;

	@Override
	public void initGui() {
		super.initGui();
		topLeft.setLocation((width - WIDTH) / 2, (height - HEIGHT) / 2);
		buttonList.add(new GuiButton(0, width - 20, 0, 20, 20, "?")); // The button to enable/disable help
		buttonList.add(new GuiButton(1, topLeft.x, topLeft.y, 60, 20, "Add XP")); // The button to add XP to the machine
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		Funcs.bindTexture(background);
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, WIDTH, HEIGHT);
		super.drawScreen(mouseX, mouseY, partialTick);

		Funcs.bindTexture(GuiMachine.extraIcons);
	}

	protected void drawTextBox(int x, int y, ColoredLine... lines) {
		// Set the width of the box to the length of the longest line
		int boxWidth = 0;
		for(ColoredLine line : lines)
			boxWidth = Math.max(boxWidth, fontRendererObj.getStringWidth(line.text));

		// This is from vanilla, I have no idea how it works, but it does
		x += 12;
		y -= 12;
		int var9 = 8;
		if(lines.length > 1)
			var9 += 2 + (lines.length - 1) * 10;
		int var10 = -267386864;
		drawGradientRect(x - 3, y - 4, x + boxWidth + 3, y - 3, var10, var10);
		drawGradientRect(x - 3, y + var9 + 3, x + boxWidth + 3, y + var9 + 4, var10, var10);
		drawGradientRect(x - 3, y - 3, x + boxWidth + 3, y + var9 + 3, var10, var10);
		drawGradientRect(x - 4, y - 3, x - 3, y + var9 + 3, var10, var10);
		drawGradientRect(x + boxWidth + 3, y - 3, x + boxWidth + 4, y + var9 + 3, var10, var10);
		int var11 = 1347420415;
		int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
		drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + var9 + 3 - 1, var11, var12);
		drawGradientRect(x + boxWidth + 2, y - 3 + 1, x + boxWidth + 3, y + var9 + 3 - 1, var11, var12);
		drawGradientRect(x - 3, y - 3, x + boxWidth + 3, y - 3 + 1, var11, var11);
		drawGradientRect(x - 3, y + var9 + 2, x + boxWidth + 3, y + var9 + 3, var12, var12);

		for(int i = 0; i < lines.length; i++)
			fontRendererObj.drawStringWithShadow(lines[i].text, x, y + i * 10 + (i == 0 ? 0 : 2), lines[i].color);
		zLevel = 0F;
		itemRender.zLevel = 0F;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch(button.id) {
			case 0: // Help button
				helpEnabled = !helpEnabled;
				break;
		}
	}
}
