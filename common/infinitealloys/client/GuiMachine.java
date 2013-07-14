package infinitealloys.client;

import infinitealloys.block.BlockMachine;
import infinitealloys.handlers.PacketHandler;
import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TileEntityComputer;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.electricity.ElectricityDisplay;
import universalelectricity.core.electricity.ElectricityPack;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class GuiMachine extends GuiContainer {

	// The position for each item in the texture sheet extras.png
	public static final Rectangle ENERGY_METER = new Rectangle(0, 0, 10, 32);
	public static final Rectangle TAB_LEFT_OFF = new Rectangle(10, 0, 24, 24);
	public static final Rectangle TAB_LEFT_ON = new Rectangle(34, 0, 28, 24);
	public static final Rectangle TAB_RIGHT_OFF = new Rectangle(62, 0, 29, 24);
	public static final Rectangle TAB_RIGHT_ON = new Rectangle(91, 0, 28, 24);
	public static final Rectangle PROGRESS_BAR = new Rectangle(119, 0, 108, 18);
	public static final Rectangle SCROLL_ON = new Rectangle(227, 0, 12, 15);
	public static final Rectangle SCROLL_OFF = new Rectangle(239, 0, 12, 15);
	public static final Rectangle UP_ARROW = new Rectangle(10, 24, 16, 16);
	public static final Rectangle DOWN_ARROW = new Rectangle(26, 24, 16, 16);
	public static final Rectangle CHECK = new Rectangle(42, 24, 16, 16);
	public static final Rectangle BLOCK_BG_OFF = new Rectangle(58, 24, 36, 18);
	public static final Rectangle BLOCK_BG_ON = new Rectangle(94, 24, 36, 18);

	/** The background texture */
	protected ResourceLocation background;
	/** The texture resource for the texture item */
	public static ResourceLocation extras;

	/** Coordinates of the top-left corner of the GUI */
	protected java.awt.Point topLeft = new java.awt.Point();
	/** Coordinates of the meter texture, shifts up and down with energy level, same for all machines */
	protected java.awt.Point energyMeter = new java.awt.Point();
	/** Coordinates of the progress bar texture, changes by machine but still otherwise */
	protected java.awt.Point progressBar = new java.awt.Point();
	/** The button to enable and disable the help overlay */
	private GuiButton helpButton;

	protected TileEntityMachine tem;
	protected infinitealloys.util.Point controllingComputer = new infinitealloys.util.Point();
	protected GuiMachineTab controllerTab;
	protected ArrayList<GuiMachineTab> machineTabs = new ArrayList<GuiMachineTab>();
	/** When help is enabled, slots get a colored outline and a mouse-over description */
	private boolean helpEnabled;

	public GuiMachine(int xSize, int ySize, TileEntityMachine tileEntity, Container container, String texture) {
		super(container);
		this.xSize = xSize;
		this.ySize = ySize;
		tem = tileEntity;
		background = createTexture(texture);
		extras = createTexture(texture);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(helpButton = new GuiButton(0, width - 20, 0, 20, 20, "?"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		topLeft.setLocation((width - xSize) / 2, (height - ySize) / 2);
		int joulesScaled = tem.getJoulesScaled(ENERGY_METER.height);
		if(joulesScaled > 0)
			energyMeter.setLocation(13, 7 + ENERGY_METER.height - tem.getJoulesScaled(ENERGY_METER.height));
		super.drawScreen(mouseX, mouseY, partialTick);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// Draw the upgrade list if the mouse is over the upgrade slot
		Slot slot = inventorySlots.getSlot(tem.upgradeSlotIndex);
		if(mouseInZone(mouseX, mouseY, slot.xDisplayPosition + topLeft.x, slot.yDisplayPosition + topLeft.y, 16, 16)) {
			ArrayList<ColoredLine> lines = new ArrayList<ColoredLine>();
			lines.add(new ColoredLine(Funcs.getLoc("upgrade.name"), 0xffffff));
			for(int i = 0; i < Consts.UPGRADE_COUNT; i++) {
				int upg = (int)Math.pow(2, i);
				if(TEHelper.isPrereqUpgrade(upg) && tem.hasUpgrade(upg << 1) || !tem.hasUpgrade(upg))
					continue;
				lines.add(new ColoredLine(Funcs.getLoc("upgrade." + Consts.UPGRADE_NAMES[i] + ".name"), 0xaaaaaa));
			}
			drawTextBox(mouseX, mouseY, lines.toArray(new ColoredLine[lines.size()]));
		}

		// Draw the energy numbers if the mouse is over the energy meter
		if(joulesScaled > 0 && mouseInZone(mouseX, mouseY, topLeft.x + energyMeter.x,
				topLeft.y + energyMeter.y + joulesScaled - ENERGY_METER.height, ENERGY_METER.width, ENERGY_METER.height))
			drawTextBox(mouseX, mouseY,
					new ColoredLine(ElectricityDisplay.getDisplayShort(tem.getEnergyStored(), ElectricityDisplay.ElectricUnit.JOULES), 0xffffff),
					new ColoredLine(ElectricityDisplay.getDisplayShort(ElectricityPack.getWattsFromJoules(tem.joulesGained, 0.05F), ElectricityDisplay.ElectricUnit.WATT) + " IN", 0x00ff00),
					new ColoredLine(ElectricityDisplay.getDisplayShort(ElectricityPack.getWattsFromJoules(tem.getJoulesUsed(), 0.05F), ElectricityDisplay.ElectricUnit.WATT) + " OUT", 0xff0000));

		// Draw the progress info if the mouse is over the progress bar
		if(tem.ticksToProcess > 0 &&
				mouseInZone(mouseX, mouseY, topLeft.x + progressBar.x, topLeft.y + progressBar.y, PROGRESS_BAR.width, PROGRESS_BAR.height))
			drawTextBox(mouseX, mouseY, new ColoredLine(tem.getProcessProgressScaled(100) + "%", 0xffffff));

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		bindTexture(background);
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// Draw the help dialogue and shade the help zone if help is enabled and the mouse is over a help zone
		if(helpEnabled) {
			MachineHelp hoveredZone = null; // The help zone that the mouse is over to render to dialogue later, null if mouse is not over a zone\
			GL11.glPushMatrix();
			GL11.glTranslatef(topLeft.x, topLeft.y, 0F);
			for(MachineHelp help : MachineHelp.getBoxesForTEM(tem)) {
				// Draw zone outline, add alpha to make the rectangles opaque
				drawRect(help.x, help.y, help.x + help.w, help.y + 1, 0xff000000 + help.color); // Top of outline box
				drawRect(help.x, help.y + help.h, help.x + help.w, help.y + help.h - 1, 0xff000000 + help.color); // Bottom of outline box
				drawRect(help.x, help.y, help.x + 1, help.y + help.h - 1, 0xff000000 + help.color); // Left side of outline box
				drawRect(help.x + help.w - 1, help.y, help.x + help.w, help.y + help.h, 0xff000000 + help.color); // Right side of outline box

				// Set hoveredZone to this zone if it hasn't been set already and the mouse is over this zone
				if(hoveredZone == null && mouseInZone(mouseX, mouseY, topLeft.x + help.x, topLeft.y + help.y, help.w, help.h))
					hoveredZone = help;
			}

			if(hoveredZone != null) {
				// Fill in the zone with an smaller 4th hex pair for less alpha
				drawRect(hoveredZone.x, hoveredZone.y, hoveredZone.x + hoveredZone.w, hoveredZone.y + hoveredZone.h, 0x60000000 + hoveredZone.color);
				GL11.glPopMatrix();

				// Draw text box with help info
				ArrayList<ColoredLine> lines = new ArrayList<ColoredLine>();
				lines.add(new ColoredLine(Funcs.getLoc("machineHelp." + hoveredZone.name + ".title"), 0xffffff));
				for(String s : Funcs.getLoc("machineHelp." + hoveredZone.name + ".info").split("/n"))
					lines.add(new ColoredLine(s, 0xaaaaaa));
				drawTextBox(-5, topLeft.y + 16, lines.toArray(new ColoredLine[lines.size()]));
			}
			else
				GL11.glPopMatrix();
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(extras);
		if(tem.getMaxEnergyStored() > 0) {
			int joulesScaled = tem.getJoulesScaled(ENERGY_METER.height);
			drawTexturedModalRect(energyMeter.x, energyMeter.y, ENERGY_METER.x, ENERGY_METER.y + ENERGY_METER.height - joulesScaled, ENERGY_METER.width,
					joulesScaled);
		}

		if(tem.ticksToProcess > 0) {
			int progressScaled = tem.getJoulesScaled(PROGRESS_BAR.height);
			drawTexturedModalRect(progressBar.x, progressBar.y, PROGRESS_BAR.x, PROGRESS_BAR.y, tem.getProcessProgressScaled(PROGRESS_BAR.width),
					PROGRESS_BAR.height);
		}

		machineTabs.clear();
		Point cont = TEHelper.controllers.get(mc.thePlayer.username);
		if(cont != null) {
			TileEntityComputer tec = ((TileEntityComputer)mc.theWorld.getBlockTileEntity(cont.x, cont.y, cont.z));
			controllerTab = new GuiMachineTab(mc, itemRenderer, -24, 6, (TileEntityMachine)mc.theWorld.getBlockTileEntity(cont.x, cont.y, cont.z), true,
					tem.coordsEquals(cont.x, cont.y, cont.z));
			controllerTab.drawButton();
			for(int i = 0; i < tec.networkCoords.size(); i++) {
				Point coords = tec.networkCoords.get(i);
				machineTabs.add(new GuiMachineTab(mc, itemRenderer, i / 5 * 197 - 24, i % 5 * 25 + 36, (TileEntityMachine)mc.theWorld.getBlockTileEntity(
						coords.x, coords.y, coords.z), i / 5 == 0, tem.coordsEquals(coords.x, coords.y, coords.z)));
				machineTabs.get(i).drawButton();
			}
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	protected void drawTextBox(int mouseX, int mouseY, ColoredLine... lines) {
		// Set the width of the box to the length of the longest line
		int boxWidth = 0;
		for(ColoredLine line : lines)
			boxWidth = Math.max(boxWidth, fontRenderer.getStringWidth(line.text));

		// This is from vanilla, I have *NO* idea what it does, other than make it work
		mouseX += 12;
		mouseY -= 12;
		int var9 = 8;
		if(lines.length > 1)
			var9 += 2 + (lines.length - 1) * 10;
		int var10 = -267386864;
		drawGradientRect(mouseX - 3, mouseY - 4, mouseX + boxWidth + 3, mouseY - 3, var10, var10);
		drawGradientRect(mouseX - 3, mouseY + var9 + 3, mouseX + boxWidth + 3, mouseY + var9 + 4, var10, var10);
		drawGradientRect(mouseX - 3, mouseY - 3, mouseX + boxWidth + 3, mouseY + var9 + 3, var10, var10);
		drawGradientRect(mouseX - 4, mouseY - 3, mouseX - 3, mouseY + var9 + 3, var10, var10);
		drawGradientRect(mouseX + boxWidth + 3, mouseY - 3, mouseX + boxWidth + 4, mouseY + var9 + 3, var10, var10);
		int var11 = 1347420415;
		int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
		drawGradientRect(mouseX - 3, mouseY - 3 + 1, mouseX - 3 + 1, mouseY + var9 + 3 - 1, var11, var12);
		drawGradientRect(mouseX + boxWidth + 2, mouseY - 3 + 1, mouseX + boxWidth + 3, mouseY + var9 + 3 - 1, var11, var12);
		drawGradientRect(mouseX - 3, mouseY - 3, mouseX + boxWidth + 3, mouseY - 3 + 1, var11, var11);
		drawGradientRect(mouseX - 3, mouseY + var9 + 2, mouseX + boxWidth + 3, mouseY + var9 + 3, var12, var12);

		for(int i = 0; i < lines.length; i++)
			fontRenderer.drawStringWithShadow(lines[i].text, mouseX, mouseY + i * 10 + (i == 0 ? 0 : 2), lines[i].color);
		zLevel = 0F;
		itemRenderer.zLevel = 0F;
	}

	public static ResourceLocation createTexture(String texture) {
		return new ResourceLocation(Consts.TEXTURE_PATH + "gui/" + texture + ".png");
	}

	public static void bindTexture(ResourceLocation texture) {
		Minecraft.getMinecraft().renderEngine.func_110577_a(texture);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button.id == 0)
			helpEnabled = !helpEnabled;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(controllerTab != null && controllerTab.mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
			int x = controllerTab.tem.xCoord;
			int y = controllerTab.tem.yCoord;
			int z = controllerTab.tem.zCoord;
			if(!tem.coordsEquals(x, y, z)) {
				((BlockMachine)Funcs.getBlock(world, x, y, z)).openGui(world, player, controllerTab.tem, false);
				PacketDispatcher.sendPacketToServer(PacketHandler.getPacketOpenGui(x, y, z, false));
			}
			return;
		}
		for(GuiMachineTab tab : machineTabs) {
			if(tab.mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
				int x = tab.tem.xCoord;
				int y = tab.tem.yCoord;
				int z = tab.tem.zCoord;
				if(!tem.coordsEquals(x, y, z)) {
					((BlockMachine)Funcs.getBlock(world, x, y, z)).openGui(world, player, tab.tem, true);
					PacketDispatcher.sendPacketToServer(PacketHandler.getPacketOpenGui(x, y, z, true));
				}
				return;
			}
		}
	}

	protected boolean mouseInZone(int mouseX, int mouseY, int xStart, int yStart, int width, int height) {
		return mouseX >= xStart && mouseY >= yStart && mouseX < xStart + width && mouseY < yStart + height;
	}

	protected class ColoredLine {
		/** The line's text */
		String text;
		/** The line's hexadecimal color */
		int color;

		protected ColoredLine(String text, int color) {
			this.text = text;
			this.color = color;
		}
	}
}
