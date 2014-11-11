package infinitealloys.client.gui;

import java.text.DecimalFormat;
import org.lwjgl.opengl.GL11;
import infinitealloys.client.gui.GuiMachine.ColoredLine;
import infinitealloys.tile.TEIASummoner;
import infinitealloys.util.Funcs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiSummoner extends GuiScreen {

	private final int WIDTH = 122;
	private final int HEIGHT = 73;
	private final int XP_BAR_X = 7;
	private final int XP_BAR_Y = 31;

	/** The background texture */
	private ResourceLocation background = Funcs.getGuiTexture("summoner");

	/** Coordinates of the top-left corner of the GUI */
	private java.awt.Point topLeft = new java.awt.Point();

	private TEIASummoner tes;

	/** When help is enabled, slots get a colored outline and a mouse-over description */
	private boolean helpEnabled;

	public GuiSummoner(TEIASummoner tes) {
		this.tes = tes;
	}

	@Override
	public void initGui() {
		super.initGui();
		topLeft.setLocation((width - WIDTH) / 2, (height - HEIGHT) / 2);
		buttonList.add(new GuiButton(0, width - 20, 0, 20, 20, "?")); // The button to enable/disable help
		buttonList.add(new GuiButton(1, topLeft.x + 31, topLeft.y + 7, 60, 20, "Add XP")); // The button to add XP to the machine
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		Funcs.bindTexture(background);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, WIDTH, HEIGHT);
		super.drawScreen(mouseX, mouseY, partialTick);

		Funcs.bindTexture(GuiMachine.extraIcons);
		// Draw the XP bar
		drawTexturedModalRect(topLeft.x + XP_BAR_X, topLeft.y + XP_BAR_Y, GuiMachine.PROGRESS_BAR.x, GuiMachine.PROGRESS_BAR.y,
				(int)((float)tes.getXPTowardsNextLevel() / (float)tes.getXPIntervalForNextLevel() * GuiMachine.PROGRESS_BAR.width), GuiMachine.PROGRESS_BAR.height);

		// Draw the stored XP info if help is not enabled and the mouse is over the XP bar
		if(!helpEnabled && Funcs.mouseInZone(mouseX, mouseY, topLeft.x + XP_BAR_X, topLeft.y + XP_BAR_Y, GuiMachine.PROGRESS_BAR.width, GuiMachine.PROGRESS_BAR.height)) {
			// Draw all the information
			drawTextBox(mouseX, mouseY, new ColoredLine(tes.getXPTowardsNextLevel() + "/" + tes.getXPIntervalForNextLevel() + " XP", 0xffffff),
					new ColoredLine(Funcs.getLoc("general.need", "/ ") + tes.getXPNeededForNextLevel() + " XP", 0xffffff));
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
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
			case 1:
				tes.addLevel(mc.thePlayer);
				tes.syncToServer();
				break;
		}
	}
}
