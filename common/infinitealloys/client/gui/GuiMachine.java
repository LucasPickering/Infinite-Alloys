package infinitealloys.client.gui;

import infinitealloys.block.BlockMachine;
import infinitealloys.client.EnumHelp;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.NetworkManager;
import infinitealloys.network.PacketOpenGui;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class GuiMachine extends GuiContainer {

	// The position for each item in the texture sheet extras.png
	static final Rectangle TAB_LEFT_OFF = new Rectangle(0, 0, 24, 24);
	static final Rectangle TAB_LEFT_ON = new Rectangle(24, 0, 28, 24);
	static final Rectangle TAB_RIGHT_OFF = new Rectangle(52, 0, 29, 24);
	static final Rectangle TAB_RIGHT_ON = new Rectangle(81, 0, 28, 24);
	static final Rectangle PROGRESS_BAR = new Rectangle(109, 0, 108, 18);
	static final Rectangle SCROLL_ON = new Rectangle(217, 0, 12, 15);
	static final Rectangle SCROLL_OFF = new Rectangle(229, 0, 12, 15);
	static final Rectangle UP_ARROW = new Rectangle(0, 24, 16, 16);
	static final Rectangle DOWN_ARROW = new Rectangle(16, 24, 16, 16);
	static final Rectangle CHECK = new Rectangle(32, 24, 16, 16);
	static final Rectangle BLOCK_BG_OFF = new Rectangle(48, 24, 36, 18);
	static final Rectangle SELECTED_OVERLAY = new Rectangle(68, 24, 36, 18);
	static final Rectangle BLOCK_BG_ON = new Rectangle(84, 24, 36, 18);
	static final Rectangle NETWORK_ICON = new Rectangle(0, 40, 16, 16);
	static final Rectangle SCROLL_BAR = new Rectangle(172, 51, 12, 96);

	/** The texture resource for the texture item */
	static final ResourceLocation extras = Funcs.getGuiTexture("extras");
	/** The background texture */
	protected ResourceLocation background;

	/** Coordinates of the top-left corner of the GUI */
	protected java.awt.Point topLeft = new java.awt.Point();

	protected TileEntityMachine tem;
	protected infinitealloys.util.Point controllingComputer = new infinitealloys.util.Point();
	protected GuiMachineTab controllerTab;
	protected final List<GuiMachineTab> machineTabs = new ArrayList<GuiMachineTab>();
	/** Coordinates of the network icon, which shows network statuses when hovered over */
	protected java.awt.Point networkIcon;
	/** When help is enabled, slots get a colored outline and a mouse-over description */
	private boolean helpEnabled;

	public GuiMachine(int xSize, int ySize, InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
		super(MachineHelper.getContainerForMachine(inventoryPlayer, tileEntity));
		this.xSize = xSize;
		this.ySize = ySize;
		tem = tileEntity;
		background = Funcs.getGuiTexture(MachineHelper.MACHINE_NAMES[tileEntity.getID()]);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(0, width - 20, 0, 20, 20, "?")); // The button to enable/disable help
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		topLeft.setLocation((width - xSize) / 2, (height - ySize) / 2);
		super.drawScreen(mouseX, mouseY, partialTick);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// Draw the upgrade list if the mouse is over the upgrade slot
		Slot slot = inventorySlots.getSlot(tem.upgradeSlotIndex);
		if(Funcs.mouseInZone(mouseX, mouseY, slot.xDisplayPosition + topLeft.x, slot.yDisplayPosition + topLeft.y, 16, 16)) {
			List<ColoredLine> lines = new ArrayList<ColoredLine>();
			lines.add(new ColoredLine(Funcs.getLoc("upgrade.name"), 0xffffff));
			for(int i = 0; i < Consts.UPGRADE_COUNT; i++) {
				int upg = 1 << i; // upg = 2^i
				if(MachineHelper.isPrereqUpgrade(upg) && tem.hasUpgrade(upg << 1) || !tem.hasUpgrade(upg))
					continue;
				lines.add(new ColoredLine(Funcs.getLoc("upgrade." + Consts.UPGRADE_NAMES[i] + ".name"), 0xaaaaaa));
			}
			drawTextBox(mouseX, mouseY, lines.toArray(new ColoredLine[lines.size()]));
		}

		// Draw the network info if the mouse is over the network icon
		if(networkIcon != null && Funcs.mouseInZone(mouseX, mouseY, topLeft.x + networkIcon.x, topLeft.y + networkIcon.y, NETWORK_ICON.width, NETWORK_ICON.height))
			// Draw a text box with a line for each network show its status and information
			drawTextBox(mouseX, mouseY, getNetworkStatuses());

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		Funcs.bindTexture(background);
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Funcs.bindTexture(extras);
		GL11.glPushMatrix();

		// Draw the network icon if this GUI has one
		if(networkIcon != null)
			Funcs.drawTexturedModalRect(this, networkIcon.x, networkIcon.y, NETWORK_ICON); // Draw the network icon

		// Draw the tabs of other machines on the network if this machine is connected to a computer
		machineTabs.clear();
		Point controller = MachineHelper.controllers.get(mc.thePlayer.username);
		if(controller != null) {
			TEMComputer tec = ((TEMComputer)Funcs.getBlockTileEntity(mc.theWorld, controller));
			controllerTab = new GuiMachineTab(mc, itemRenderer, -24, 6, tec, true, tem.coords().equals(controller));
			controllerTab.drawButton();
			Point[] clients = NetworkManager.getClients(tec.getComputerNetworkID());
			for(int i = 0; i < clients.length; i++) {
				machineTabs.add(new GuiMachineTab(mc, itemRenderer, i / 5 * 197 - 24, i % 5 * 25 + 36, (TileEntityElectric)Funcs.getBlockTileEntity(mc.theWorld, clients[i]),
						i / 5 == 0, clients[i].equals(tem.coords())));
				machineTabs.get(i).drawButton();
			}
		}

		// Draw the help dialogue and shade the help zone if help is enabled and the mouse is over a help zone
		if(helpEnabled) {
			EnumHelp hoveredZone = null; // The help zone that the mouse is over to render to dialogue later, null if mouse is not over a zone\
			for(EnumHelp help : EnumHelp.getBoxes(tem.getID())) {
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

				// Draw text box with help info
				List<ColoredLine> lines = new ArrayList<ColoredLine>();
				lines.add(new ColoredLine(Funcs.getLoc("machineHelp." + hoveredZone.name + ".title"), 0xffffff));
				for(String s : Funcs.getLoc("machineHelp." + hoveredZone.name + ".info").split("/n"))
					lines.add(new ColoredLine(s, 0xaaaaaa));
				drawTextBox(-topLeft.x - 8, -topLeft.y + 17, lines.toArray(new ColoredLine[lines.size()]));
			}
		}
		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	protected void drawTextBox(int mouseX, int mouseY, ColoredLine... lines) {
		// Set the width of the box to the length of the longest line
		int boxWidth = 0;
		for(ColoredLine line : lines)
			boxWidth = Math.max(boxWidth, fontRenderer.getStringWidth(line.text));

		// This is from vanilla, I have no idea what it does, other than make it work
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

	@Override
	public void actionPerformed(GuiButton button) {
		if(button.id == 0) {
			helpEnabled = !helpEnabled;
			if(Loader.isModLoaded("mcp"))
				InfiniteAlloys.instance.proxy.initLocalization(); // Debug line, reloads localization to update edits
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		// Was the network tab of the controlling computer clicked? Go to that computer
		if(controllerTab != null && controllerTab.mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
			int x = controllerTab.tem.xCoord;
			int y = controllerTab.tem.yCoord;
			int z = controllerTab.tem.zCoord;
			if(!tem.coords().equals(x, y, z)) {
				((BlockMachine)Funcs.getBlock(world, x, y, z)).openGui(world, player, controllerTab.tem, false);
				PacketDispatcher.sendPacketToServer(PacketOpenGui.getPacket(x, (short)y, z, false));
			}
			return;
		}

		// Was the network tab of another machine clicked? Go to that machine
		for(GuiMachineTab tab : machineTabs) {
			if(tab.mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
				int x = tab.tem.xCoord;
				int y = tab.tem.yCoord;
				int z = tab.tem.zCoord;
				if(!tem.coords().equals(tab.tem.coords())) {
					((BlockMachine)Funcs.getBlock(world, x, y, z)).openGui(world, player, tab.tem, true);
					PacketDispatcher.sendPacketToServer(PacketOpenGui.getPacket(x, (short)y, z, false));
				}
				return;
			}
		}
	}

	protected abstract ColoredLine[] getNetworkStatuses();

	public static class ColoredLine {
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
