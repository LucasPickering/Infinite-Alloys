package infinitealloys.client.gui;

import infinitealloys.block.IABlocks;
import infinitealloys.client.EnumHelp;
import infinitealloys.client.gui.GuiMachine.ColoredLine;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.network.PacketWand;
import infinitealloys.tile.IHost;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.common.Loader;

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
		buttonList.add(new GuiButton(Consts.WAND_SIZE, width - 20, 0, 20, 20, "?")); // Help button
		buttonList.add(addToWand = new GuiButton(Consts.WAND_SIZE + 1, width / 2 - 83, height / 2 - 116, 70, 20, Funcs.getLoc("wand.addToWand")));
		buttonList.add(addSelected = new GuiButton(Consts.WAND_SIZE + 2, width / 2 - 10, height / 2 - 116, 70, 20, Funcs.getLoc("wand.addSelected")));

		// Reset button states
		addToWand.enabled = false;
		addSelected.enabled = false;

		ItemStack heldItem = mc.thePlayer.getHeldItem();
		if(heldItem.getItem() instanceof ItemInternetWand && heldItem.hasTagCompound()) {
			NBTTagCompound tagCompound = heldItem.getTagCompound();

			// Create each button for the machines
			for(int i = 0; i < Consts.WAND_SIZE; i++) { // For each button in the array
				machineButtons[i] = null; // Reset the button
				if(tagCompound.hasKey("Coords" + i)) { // If there is a machine that corresponds to this button
					int[] client = tagCompound.getIntArray("Coords" + i); // Variables for this machine's data
					if(!MachineHelper.isClient(DimensionManager.getWorld(client[0]).getTileEntity(client[1], client[2], client[3]))) { // If the block is no longer valid
						Funcs.sendPacketToServer(new PacketWand((byte)i)); // Remove it
						((ItemInternetWand)heldItem.getItem()).removeMachine(heldItem, i);
						i--; // Decrement i so that it repeats this number for the new button
					}
					else
						machineButtons[i] = new MachineButton(i, width / 2 - 82, height / 2 + i * 21 - 80, client[0], client[1], client[2], client[3]); // Create a button
				}
			}

			if(tagCompound.hasKey("CoordsCurrent")) {
				int[] a = tagCompound.getIntArray("CoordsCurrent");
				addToWand.enabled = ((ItemInternetWand)heldItem.getItem()).isMachineValid(DimensionManager.getWorld(a[0]), heldItem, a[1], a[2], a[3]);

				TileEntity te = DimensionManager.getWorld(a[0]).getTileEntity(a[1], a[2], a[3]);

				addSelected.enabled = selectedButtons != 0;
				// Go over each machine button
				for(MachineButton button : machineButtons)
					if(button != null && (selectedButtons & 1 << button.buttonID) != 0) // If this button is selected
						// If the selected machine is not valid for the block that was clicked
						if(!(te instanceof IHost) || button.dimensionID != te.getWorldObj().provider.dimensionId ||
								!((IHost)te).isClientValid(new Point(button.machineX, button.machineY, button.machineZ)))
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
		for(MachineButton button : machineButtons)
			if(button != null)
				button.drawButton();

		GL11.glPushMatrix();
		GL11.glTranslatef(topLeft.x, topLeft.y, 0);

		mc.fontRenderer.drawStringWithShadow("D", 30, 32, 0xffffff);
		mc.fontRenderer.drawStringWithShadow("X", 62, 32, 0xffffff);
		mc.fontRenderer.drawStringWithShadow("Y", 86, 32, 0xffffff);
		mc.fontRenderer.drawStringWithShadow("Z", 110, 32, 0xffffff);

		// Draw the help dialogue and shade the help zone if help is enabled and the mouse is over a help zone
		if(helpEnabled) {
			EnumHelp hoveredZone = null; // The help zone that the mouse is over to render to dialogue later, null if mouse is not over a zone\
			for(EnumHelp help : EnumHelp.getBoxes(Consts.MACHINE_COUNT)) {
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
				MachineButton button = machineButtons[i];
				if(button != null && Funcs.mouseInZone(mouseX, mouseY, button.xPos, button.yPos, button.width, button.height)) { // If this button was clicked
					if(!isCtrlKeyDown() && !isShiftKeyDown()) // If the CTRL or Shift key wasn't held, set all buttons to 0
						selectedButtons = 0;
					selectedButtons ^= 1 << i; // Invert its selected state

					if(isShiftKeyDown()) {
						int firstOne = -1; // The index of the position of the rightmost 1 in the binary integer
						int lastOne = -1; // The index of the position of the leftmost 1 in the binary integer
						for(int j = 0; j < Consts.WAND_SIZE; j++) { // Go over each bit in the integer
							if((selectedButtons & 1 << j) != 0) { // If the bit is 1
								if(firstOne == -1) // If the rightmost 1 has not been found already
									firstOne = j; // This is the rightmost 1
								lastOne = j; // This is the leftmost 1 (so far)
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
			case Consts.WAND_SIZE: // Help button
				helpEnabled = !helpEnabled;
				if(Loader.isModLoaded("mcp"))
					InfiniteAlloys.instance.proxy.initLocalization(); // Debug line, reloads localization to update edits
				break;

			case Consts.WAND_SIZE + 1: // Add the block that was clicked to the wand's list
				ItemStack heldItem = mc.thePlayer.getHeldItem();
				int[] a = heldItem.getTagCompound().getIntArray("CoordsCurrent");
				Funcs.sendPacketToServer(new PacketWand(a[1], a[2], a[3]));
				((ItemInternetWand)heldItem.getItem()).addMachine(mc.theWorld, heldItem, a[1], a[2], a[3]);
				break;

			case Consts.WAND_SIZE + 2: // Add selected machines to a host
				heldItem = mc.thePlayer.getHeldItem();
				int[] host = heldItem.getTagCompound().getIntArray("CoordsCurrent");

				if(mc.theWorld.getTileEntity(host[1], host[2], host[3]) instanceof IHost) { // If this is a host
					for(MachineButton machineButton : machineButtons) { // Go over each button
						if(machineButton != null && (selectedButtons & 1 << machineButton.buttonID) != 0) { // If this button is selected
							// Add the selected machine to the host
							int[] client = heldItem.getTagCompound().getIntArray("Coords" + machineButton.buttonID);
							if(host[0] == client[0]) // They're in the same dimension
								((IHost)mc.theWorld.getTileEntity(host[1], host[2], host[3])).addClient(mc.thePlayer, new Point(client[1], client[2], client[3]), true);
						}
					}
				}
				break;

			default:
				heldItem = mc.thePlayer.getHeldItem();
				Funcs.sendPacketToServer(new PacketWand((byte)button.id));
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
		for(ColoredLine line : lines)
			boxWidth = Math.max(boxWidth, fontRendererObj.getStringWidth(line.text));

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
			fontRendererObj.drawStringWithShadow(lines[i].text, mouseX, mouseY + i * 10 + (i == 0 ? 0 : 2), lines[i].color);
		zLevel = 0F;
		itemRenderer.zLevel = 0F;
	}

	/** A button that represents a machine with its texture and coordinates */
	private class MachineButton {

		final int width = 118;
		final int height = 17;

		int buttonID;
		int xPos;
		int yPos;

		int machineID;
		int dimensionID;
		int machineX;
		int machineY;
		int machineZ;

		MachineButton(int buttonID, int xPos, int yPos, int dimensionID, int machineX, int machineY, int machineZ) {
			super();
			this.buttonID = buttonID;
			this.xPos = xPos;
			this.yPos = yPos;
			this.dimensionID = dimensionID;
			machineID = DimensionManager.getWorld(dimensionID).getBlockMetadata(machineX, machineY, machineZ);
			this.machineX = machineX;
			this.machineY = machineY;
			this.machineZ = machineZ;
			buttonList.add(new GuiButton(buttonID, xPos + 122, yPos - 1, 20, 20, "X"));
		}

		void drawButton() {
			Funcs.bindTexture(GuiMachine.extras);

			if((1 << buttonID & selectedButtons) != 0) { // If this button is selected, draw a box around it
				int yPosBox = yPos;
				int heightBox = height;

				// If there is a button before this one and it is selected, extend this box to connect to that one
				if(buttonID != 0 && (1 << buttonID - 1 & selectedButtons) != 0) {
					yPosBox -= 3;
					heightBox += 3;
				}

				// Only draw the top line if this box isn't connecting to the one above it
				else
					drawHorizontalLine(xPos - 1, xPos + width + 1, yPosBox - 1, 0xff22aa22); // Top

				// If this is the last button or if the button below this isn't selected, draw the bottom line
				if(buttonID == machineButtons.length - 1 || (1 << buttonID + 1 & selectedButtons) == 0)
					drawHorizontalLine(xPos - 1, xPos + width + 1, yPosBox + heightBox + 1, 0xff22aa22); // Bottom

				drawVerticalLine(xPos - 1, yPosBox + heightBox + 1, yPosBox - 1, 0xff22aa22);// Left
				drawVerticalLine(xPos + width + 1, yPosBox + heightBox + 1, yPosBox - 1, 0xff22aa22);// Right
				drawRect(xPos, yPosBox, xPos + width + 1, yPosBox + heightBox + 1, 0x6022aa22); // Shading
			}

			GL11.glColor3f(1F, 1F, 1F); // Reset the color

			// Draw the string for the coordinates
			mc.fontRenderer.drawStringWithShadow(dimensionID + "", xPos + 26 - (mc.fontRenderer.getStringWidth(dimensionID + "") / 2), yPos + 5, 0xffffff);
			mc.fontRenderer.drawStringWithShadow(machineX + "", xPos + 58 - (mc.fontRenderer.getStringWidth(machineX + "") / 2), yPos + 5, 0xffffff);
			mc.fontRenderer.drawStringWithShadow(machineY + "", xPos + 82 - (mc.fontRenderer.getStringWidth(machineY + "") / 2), yPos + 5, 0xffffff);
			mc.fontRenderer.drawStringWithShadow(machineZ + "", xPos + 106 - (mc.fontRenderer.getStringWidth(machineZ + "") / 2), yPos + 5, 0xffffff);

			itemRender.renderItemIntoGUI(fontRendererObj, mc.getTextureManager(), new ItemStack(IABlocks.machine, 1, machineID), xPos, yPos + 1);
		}
	}
}
