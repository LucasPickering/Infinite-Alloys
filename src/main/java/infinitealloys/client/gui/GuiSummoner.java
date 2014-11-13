package infinitealloys.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import infinitealloys.client.EnumHelp;
import infinitealloys.tile.TEIASummoner;
import infinitealloys.util.Funcs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

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
	private HashMap<String, ColoredText[]> helpText = new HashMap<String, ColoredText[]>();

	/** When help is enabled, slots get a colored outline and a mouse-over description */
	private boolean helpEnabled;

	public GuiSummoner(TEIASummoner tes) {
		this.tes = tes;
		// Make an array with the help title and the lines of help text
		for(EnumHelp help : EnumHelp.getSummonerBoxes()) {
			List<ColoredText> lines = new ArrayList<ColoredText>();
			lines.add(new ColoredText(Funcs.getLoc("machineHelp." + help.name + ".title"), 0xffffff));
			for(String s : Funcs.getLoc("machineHelp." + help.name + ".info").split("/n"))
				lines.add(new ColoredText(s, 0xaaaaaa));
			helpText.put(help.name, lines.toArray(new ColoredText[lines.size()]));
		}
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
			// @formatter:off
			new GuiTextBox(fontRendererObj, mouseX, mouseY, tes.getXPTowardsNextLevel() + "/" + tes.getXPIntervalForNextLevel() + " XP",
															Funcs.getLoc("general.need", "/ ") + tes.getXPNeededForNextLevel() + " XP").draw();
			// @formatter:on
		}

		// Draw the help dialogue and shade the help zone if help is enabled and the mouse is over a help zone
		if(helpEnabled) {
			EnumHelp hoveredZone = null; // The help zone that the mouse is over to render to dialogue later, null if mouse is not over a zone\
			for(EnumHelp help : EnumHelp.getNetworkWandBoxes()) {
				// Draw zone outline, add alpha to make the rectangles opaque
				drawRect(help.x, help.y, help.x + help.w, help.y + 1, 0xff000000 + help.color); // Top of outline box
				drawRect(help.x, help.y + help.h, help.x + help.w, help.y + help.h - 1, 0xff000000 + help.color); // Bottom of outline box
				drawRect(help.x, help.y, help.x + 1, help.y + help.h - 1, 0xff000000 + help.color); // Left side of outline box
				drawRect(help.x + help.w - 1, help.y, help.x + help.w, help.y + help.h, 0xff000000 + help.color); // Right side of outline box

				// Set hoveredZone to this zone if it hasn't been set already and the mouse is over this zone
				if(hoveredZone == null && Funcs.mouseInZone(mouseX, mouseY, topLeft.x + help.x, topLeft.y + help.y, help.w, help.h))
					hoveredZone = help;
			}

			if(hoveredZone != null) {
				// Fill in the zone with an smaller 4th hex pair for less alpha
				drawRect(hoveredZone.x, hoveredZone.y, hoveredZone.x + hoveredZone.w, hoveredZone.y + hoveredZone.h, 0x60000000 + hoveredZone.color);
				new GuiTextBox(fontRendererObj, mouseX - topLeft.x, mouseY - topLeft.y, helpText.get(hoveredZone.name)).draw(); // Draw text box with help info
			}
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
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
