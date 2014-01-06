package infinitealloys.client.gui;

import java.util.ArrayList;
import java.util.List;
import infinitealloys.block.Blocks;
import infinitealloys.client.EnumHelp;
import infinitealloys.client.gui.GuiMachine.ColoredLine;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.network.PacketAddMachine;
import infinitealloys.network.PacketWand;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TEMEnergyStorage;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiInternetWand extends GuiScreen {

	private final RenderItem itemRenderer = new RenderItem();
	private final ResourceLocation background;
	private final int xSize = 178;
	private final int ySize = 245;

	/** Coordinates of the top-left corner of the GUI */
	protected java.awt.Point topLeft = new java.awt.Point();

	/** If the GUI was opened by clicking on a machine, this button adds the machine that was clicked to the wand */
	private GuiButton addToWand;

	/** If the GUI was opened by clicking on a machine, this button adds the selected machine to the machine that was clicked */
	private GuiButton addSelected;

	/** The array of buttons that applies to each machine. It is a fixed length and buttons that do not exist are null */
	private final MachineButton[] machineButtons = new MachineButton[Consts.WAND_SIZE];

	/** When help is enabled, slots get a colored outline and a mouse-over description */
	private boolean helpEnabled;

	/** Binary integer to represent the buttons that are selected. Right-most digit is the top of the list.
	 * There is one bit for each button. 0 is not-selected. 1 is selected. */
	private int selectedButtons;

	public GuiInternetWand() {
		background = Funcs.getGuiTexture("wand");
	}

	@Override
	public void initGui() {
		buttonList.clear();
		buttonList.add(new GuiButton(Consts.WAND_SIZE, width - 20, 0, 20, 20, "?")); // The button to enable/disable help
		buttonList.add(addToWand = new GuiButton(Consts.WAND_SIZE + 1, width / 2 - 83, height / 2 - 116, 70, 20, Funcs.getLoc("wand.addToWand")));
		buttonList.add(addSelected = new GuiButton(Consts.WAND_SIZE + 2, width / 2 - 10, height / 2 - 116, 70, 20, Funcs.getLoc("wand.addSelected")));

		addToWand.enabled = false; // Reset button states
		addSelected.enabled = false;

		final ItemStack heldItem = mc.thePlayer.getHeldItem();
		if(heldItem.getItem() instanceof ItemInternetWand && heldItem.hasTagCompound()) {
			final NBTTagCompound tagCompound = heldItem.getTagCompound();

			for(int i = 0; i < Consts.WAND_SIZE; i++) { // For each button in the array
				machineButtons[i] = null; // Reset the button
				if(tagCompound.hasKey("Coords" + i)) { // If there is a machine that corresponds to this button
					final int[] client = tagCompound.getIntArray("Coords" + i); // Variables for this machine's data
					if(!MachineHelper.isClient(mc.theWorld, client[0], client[1], client[2])) { // If the block is no longer valid for the wand
						PacketDispatcher.sendPacketToServer(PacketWand.getPacketRemove((byte)i)); // Remove it
						((ItemInternetWand)heldItem.getItem()).removeMachine(heldItem, i);
						i--; // Decrement i so that it repeats this number again with the new button
					}
					else
						machineButtons[i] = new MachineButton(i, width / 2 - 82, height / 2 + i * 21 - 91, client[0], client[1], client[2]); // Create a button
				}
			}

			if(tagCompound.hasKey("CoordsCurrent")) {
				final int[] a = tagCompound.getIntArray("CoordsCurrent");
				addToWand.enabled = ((ItemInternetWand)heldItem.getItem()).isMachineValid(mc.theWorld, heldItem, a[0], a[1], a[2]);

				final TileEntity te = mc.theWorld.getBlockTileEntity(a[0], a[1], a[2]);

				addSelected.enabled = selectedButtons != 0;
				// Go over each machine button
				for(final MachineButton button : machineButtons)
					if(button != null && (selectedButtons & 1 << button.buttonID) != 0) // If this button is selected
						// If the selected machine is not valid for the block that was clicked
						if(!(te instanceof TEMEnergyStorage && button.isElectric || te instanceof TEMComputer && button.isWireless))
							addSelected.enabled = false; // Set the button to false
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		topLeft.setLocation((width - xSize) / 2, (height - ySize) / 2);
		Funcs.bindTexture(background);
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
		super.drawScreen(mouseX, mouseY, partialTick);
		for(final MachineButton button : machineButtons)
			if(button != null)
				button.drawButton();

		GL11.glPushMatrix();
		GL11.glTranslatef(topLeft.x, topLeft.y, 0);
		// Draw the help dialogue and shade the help zone if help is enabled and the mouse is over a help zone
		if(helpEnabled) {
			EnumHelp hoveredZone = null; // The help zone that the mouse is over to render to dialogue later, null if mouse is not over a zone\
			for(final EnumHelp help : EnumHelp.getBoxes(Consts.MACHINE_COUNT)) {
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
				final List<ColoredLine> lines = new ArrayList<ColoredLine>();
				lines.add(new ColoredLine(Funcs.getLoc("machineHelp." + hoveredZone.name + ".title"), 0xffffff));
				for(final String s : Funcs.getLoc("machineHelp." + hoveredZone.name + ".info").split("/n"))
					lines.add(new ColoredLine(s, 0xaaaaaa));
				drawTextBox(-8 - topLeft.x, 17 - topLeft.y, lines.toArray(new ColoredLine[lines.size()]));
			}
		}
		GL11.glPopMatrix();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 0) { // If it was a left-click
			for(int i = 0; i < Consts.WAND_SIZE; i++) {
				final MachineButton button = machineButtons[i];
				if(button != null && Funcs.mouseInZone(mouseX, mouseY, button.xPos, button.yPos, button.width, button.height)) { // If this button was clicked
					if(!isCtrlKeyDown() && !isShiftKeyDown()) // If the CTRL or Shift key wasn't held, set all buttons to 0
						selectedButtons = 0;
					selectedButtons ^= 1 << i; // Invert its selected state

					if(isShiftKeyDown()) {
						int firstOne = -1; // The index of the position of the rightmost 1 in the binary integer
						int lastOne = -1; // The index of the position of the leftmost 1 in the binary integer
						for(int j = 0; j < Consts.WAND_SIZE; j++) { // Go over each bit in the integer
							if((selectedButtons & 1 << j) != 0) { // If the bit is 1
								if(firstOne == -1) // If the leftmost 1 has not been found already
									firstOne = j; // This is the leftmost 1
								lastOne = j; // This is the rightmost 1 (so far)
							}
						}
						if(firstOne != -1) // If there is a 1 in the integer
							// Loop from the first 1 to the last 1
							for(int j = firstOne; j <= lastOne; j++)
								selectedButtons |= 1 << j; // Set each bit to 1
					}
					break;
				}
			}
			initGui();
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch(button.id) {
			case Consts.WAND_SIZE:
				InfiniteAlloys.instance.proxy.initLocalization(); // TODO: Remove this line, it is for debug only!!!!
				helpEnabled = !helpEnabled;
				break;
			case Consts.WAND_SIZE + 1:
				ItemStack heldItem = mc.thePlayer.getHeldItem();
				final int[] a = heldItem.getTagCompound().getIntArray("CoordsCurrent");
				PacketDispatcher.sendPacketToServer(PacketWand.getPacketAdd(a[0], (short)a[1], a[2]));
				((ItemInternetWand)heldItem.getItem()).addMachine(mc.theWorld, heldItem, a[0], a[1], a[2]);
				break;
			case Consts.WAND_SIZE + 2:
				heldItem = mc.thePlayer.getHeldItem();
				final int[] host = heldItem.getTagCompound().getIntArray("CoordsCurrent");

				if(MachineHelper.isHost(mc.theWorld, host[0], host[1], host[2])) { // If this is a host
					for(final MachineButton machineButton : machineButtons) { // Go over each button
						if(machineButton != null && (selectedButtons & 1 << machineButton.buttonID) != 0) { // If this button is selected
							// Add the selected machine to the host
							final int[] client = heldItem.getTagCompound().getIntArray("Coords" + machineButton.buttonID);
							PacketDispatcher.sendPacketToServer(PacketAddMachine.getPacket(host[0], host[1], host[2], client[0], client[1], client[2]));
							((IHost)mc.theWorld.getBlockTileEntity(host[0], host[1], host[2])).addMachine(mc.thePlayer, client[0], client[1], client[2]);
						}
					}
				}
				break;
			default:
				heldItem = mc.thePlayer.getHeldItem();
				PacketDispatcher.sendPacketToServer(PacketWand.getPacketRemove((byte)button.id));
				((ItemInternetWand)heldItem.getItem()).removeMachine(heldItem, (byte)button.id);
				break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	protected void drawTextBox(int mouseX, int mouseY, ColoredLine... lines) {
		// Set the width of the box to the length of the longest line
		int boxWidth = 0;
		for(final ColoredLine line : lines)
			boxWidth = Math.max(boxWidth, fontRenderer.getStringWidth(line.text));

		// This is from vanilla, I have no idea what it does, other than make it work
		mouseX += 12;
		mouseY -= 12;
		int var9 = 8;
		if(lines.length > 1)
			var9 += 2 + (lines.length - 1) * 10;
		final int var10 = -267386864;
		drawGradientRect(mouseX - 3, mouseY - 4, mouseX + boxWidth + 3, mouseY - 3, var10, var10);
		drawGradientRect(mouseX - 3, mouseY + var9 + 3, mouseX + boxWidth + 3, mouseY + var9 + 4, var10, var10);
		drawGradientRect(mouseX - 3, mouseY - 3, mouseX + boxWidth + 3, mouseY + var9 + 3, var10, var10);
		drawGradientRect(mouseX - 4, mouseY - 3, mouseX - 3, mouseY + var9 + 3, var10, var10);
		drawGradientRect(mouseX + boxWidth + 3, mouseY - 3, mouseX + boxWidth + 4, mouseY + var9 + 3, var10, var10);
		final int var11 = 1347420415;
		final int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
		drawGradientRect(mouseX - 3, mouseY - 3 + 1, mouseX - 3 + 1, mouseY + var9 + 3 - 1, var11, var12);
		drawGradientRect(mouseX + boxWidth + 2, mouseY - 3 + 1, mouseX + boxWidth + 3, mouseY + var9 + 3 - 1, var11, var12);
		drawGradientRect(mouseX - 3, mouseY - 3, mouseX + boxWidth + 3, mouseY - 3 + 1, var11, var11);
		drawGradientRect(mouseX - 3, mouseY + var9 + 2, mouseX + boxWidth + 3, mouseY + var9 + 3, var12, var12);

		for(int i = 0; i < lines.length; i++)
			fontRenderer.drawStringWithShadow(lines[i].text, mouseX, mouseY + i * 10 + (i == 0 ? 0 : 2), lines[i].color);
		zLevel = 0F;
		itemRenderer.zLevel = 0F;
	}

	/** A button that represents a machine with its texture and coordinates */
	private class MachineButton {

		final int width = 122;
		final int height = 17;

		int buttonID;
		int xPos;
		int yPos;
		GuiButton remove;

		int machineID;
		int machineX;
		int machineY;
		int machineZ;
		boolean isElectric;
		boolean isWireless;

		MachineButton(int buttonID, int xPos, int yPos, int machineX, int machineY, int machineZ) {
			super();
			this.buttonID = buttonID;
			this.xPos = xPos;
			this.yPos = yPos;
			machineID = mc.theWorld.getBlockMetadata(machineX, machineY, machineZ);
			this.machineX = machineX;
			this.machineY = machineY;
			this.machineZ = machineZ;
			isElectric = MachineHelper.isElectric(mc.theWorld, machineX, machineY, machineZ);
			isWireless = MachineHelper.isWireless(mc.theWorld, machineX, machineY, machineZ);
			buttonList.add(remove = new GuiButton(buttonID, xPos + 126, yPos - 1, 16, 20, "X"));
		}

		void drawButton() {
			Funcs.bindTexture(GuiMachine.extras);

			if((selectedButtons & 1 << buttonID) != 0) { // If this button is selected, draw a box around it
				drawHorizontalLine(xPos - 1, xPos + width + 1, yPos - 1, 0xff22aa22);
				drawHorizontalLine(xPos - 1, xPos + width + 1, yPos + height + 1, 0xff22aa22);
				drawVerticalLine(xPos - 1, yPos + height + 1, yPos - 1, 0xff22aa22);
				drawVerticalLine(xPos + width + 1, yPos + height + 1, yPos - 1, 0xff22aa22);
				drawRect(xPos, yPos, xPos + width + 1, yPos + height + 1, 0x6022aa22);
			}

			GL11.glColor3f(1F, 1F, 1F); // Reset the color

			GL11.glPushMatrix();
			GL11.glTranslatef(xPos + 100, yPos + 3, 0);
			GL11.glScalef(0.75F, 0.75F, 1F);
			// If the machine is electrical
			if(isElectric)
				Funcs.drawTexturedModalRect(GuiInternetWand.this, 0, 0, GuiMachine.ENERGY_ICON_OFF); // Render an electricity icon on the button

			// If the machine is wireless
			if(isWireless)
				Funcs.drawTexturedModalRect(GuiInternetWand.this, 15, 0, GuiMachine.WIRELESS_ICON); // Render a wireless icon on the button
			GL11.glPopMatrix();

			// Draw the string for the coordinates
			final String display = machineX + "   " + machineY + "   " + machineZ;
			mc.fontRenderer.drawStringWithShadow(display, xPos + 60 - (mc.fontRenderer.getStringWidth(display) / 2), yPos + 5, 0xffffff);

			itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(Blocks.machineID, 1, machineID), xPos, yPos + 1);
		}
	}
}
