package infinitealloys.client.gui;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.network.PacketXraySearch;
import infinitealloys.tile.TEEXray;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiXray extends GuiElectric {

	private final TEEXray tex;

	/** The scroll bar (width is for the scrolling block) */

	/** TileEntityXray.searchingClient, used to checking if searching just finished */
	private boolean wasSearching;

	/** The number of the first displayed line of blocks. Min is 0, max is num of rows - number on screen (5) */
	private int scrollPos = 0;

	private BlockButton[] blockButtons = new BlockButton[0];
	private GuiButton searchButton;
	private boolean initialized;

	public GuiXray(InventoryPlayer inventoryPlayer, TEEXray tileEntity) {
		super(196, 240, inventoryPlayer, tileEntity);
		tex = tileEntity;
		progressBar.setLocation(54, 5);
		energyIcon.setLocation(93, 25);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(searchButton = new GuiButton(1, width / 2 - 30, height / 2 - 90, 80, 20, Funcs.getLoc("machine.xray.search")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if(!initialized) {
			initialized = true;
			setButtons();
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		searchButton.enabled = tex.inventoryStacks[0] != null;
		if(blockButtons.length <= 20)
			Funcs.drawTexturedModalRect(this, SCROLL_BAR.x, SCROLL_BAR.y, SCROLL_OFF);
		else
			Funcs.drawTexturedModalRect(this, SCROLL_BAR.x, SCROLL_BAR.y + (int)((float)(SCROLL_BAR.height - 15) / (float)(blockButtons.length / 4 - 4) * scrollPos), SCROLL_ON);
		if(wasSearching && tex.getProcessProgress() == 0)
			setButtons();
		wasSearching = tex.getProcessProgress() > 0;

		Funcs.bindTexture(GuiMachine.extras);
		for(int i = scrollPos * 4; i < blockButtons.length && i < scrollPos * 4 + 20; i++)
			blockButtons[i].drawButton();

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 0) { // Was the left mouse button clicked?
			for(int i = 0; i < blockButtons.length; i++) { // Iterate though each block button
				// Was this button clicked?
				if(Funcs.mouseInZone(mouseX - topLeft.x, mouseY - topLeft.y,
						blockButtons[i].xPos, blockButtons[i].yPos, blockButtons[i].width, blockButtons[i].height)) {
					// Was there already a selected button? If so, deselect it.
					if(tex.selectedButton >= 0)
						blockButtons[tex.selectedButton].selected = false;

					// Clear the highlighted blocks from the last selected button
					InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();

					// Was this button already selected? If so, none of the buttons are selected now
					if(tex.selectedButton == i)
						tex.selectedButton = -1;

					// This button wasn't already selected
					else {
						// This button is now selected
						tex.selectedButton = i;
						blockButtons[i].selected = true;

						// The blocks that are represented by the newly selected button get highlighted
						for(final Point block : tex.detectedBlocks)
							// Is this block represented by the newly selected button?
							if(block.y == blockButtons[i].getYValue())
								// If so, add this block to the list of blocks to be highlighted. Convert the x and z coords from relative to absolute
								InfiniteAlloys.proxy.gfxHandler.xrayBlocks.add(new Point(tex.xCoord + block.x, block.y, tex.zCoord + block.z));
					}
				}
			}

			setButtons();
			// Was the scroll up button clicked?
			if(Funcs.mouseInZone(mouseX, mouseY, topLeft.x + 172, topLeft.y + 40, 14, 8))
				// Scroll up
				scroll(-1);
			// Was the scroll down button clicked?
			else if(Funcs.mouseInZone(mouseX, mouseY, topLeft.x + 172, topLeft.y + 147, 14, 8))
				// Scroll down
				scroll(1);
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		final int scrollAmt = Mouse.getEventDWheel();
		// Scroll one line up or down based on the movement, if the list is long enough to need scrolling
		if(blockButtons.length > 20)
			scroll(scrollAmt > 0 ? -1 : scrollAmt < 0 ? 1 : 0);
	}

	private void setButtons() {
		if(tex.inventoryStacks[0] == null || tem.getProcessProgress() > 0)
			blockButtons = new BlockButton[0];
		else {
			final int[] blockCounts = new int[tem.yCoord];
			final List<Integer> levels = new ArrayList<Integer>();
			// Go through each detected block
			for(final Point block : tex.detectedBlocks) {
				// For each block if there hasn't been a block for that y-level yet, at that y to the list
				if(blockCounts[block.y]++ == 0)
					levels.add(block.y);
			}
			blockButtons = new BlockButton[levels.size()];
			for(int i = 0; i < blockButtons.length; i++)
				blockButtons[i] = new BlockButton(i % 4 * 40 + 9, (i / 4 - scrollPos) * 20 + 52, tex.inventoryStacks[0].itemID, blockCounts[levels.get(i)],
						tex.inventoryStacks[0].getItemDamage(), levels.get(i));
			if(tex.selectedButton != -1)
				blockButtons[tex.selectedButton].selected = true;
		}
	}

	/** Scroll the block list the specified amount of lines. Positive is down, negative is up. */
	private void scroll(int lines) {
		if(lines > 0 && scrollPos < blockButtons.length / 4 - 4 || lines < 0 && scrollPos > 0)
			scrollPos += lines;
		setButtons();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if(button.id == 1) {
			tex.selectedButton = -1;
			setButtons();
			InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
			tex.shouldSearch = true;
			PacketDispatcher.sendPacketToServer(PacketXraySearch.getPacket(tex.xCoord, (short)tex.yCoord, tex.zCoord));
		}
	}

	/** A button that represents a type of a block, its y-level, and the quantity of that block in the y-level */
	private class BlockButton extends GuiScreen {

		/** The position of the button within the GUI */
		final int xPos, yPos;

		final int blockID, blockAmount, blockMeta;
		/** The yValue of blocks that this button represents */
		final int yValue;
		Background background;
		boolean selected;

		BlockButton(int xPos, int yPos, int blockID, int blockAmount, int blockMeta, int yValue) {
			this.xPos = xPos;
			this.yPos = yPos;
			this.blockID = blockID;
			this.blockAmount = blockAmount;
			this.blockMeta = blockMeta;
			this.yValue = yValue;
			width = 36;
			height = 18;

			// Set the background of the button based on its y-value
			for(final Background bg : Background.values()) {
				if(bg.start <= yValue && yValue <= bg.end) {
					background = bg;
					break;
				}
			}
		}

		void drawButton() {
			if(blockAmount > 0) {
				// Draw the background texture for the button
				Funcs.drawTexturedModalRect(this, xPos, yPos, background.texture);
				// If this button is selected, draw an overlay to indicate that
				if(selected)
					Funcs.drawTexturedModalRect(this, xPos - 1, yPos - 1, SELECTED_OVERLAY);

				// Draw the yValue string
				final String display = Integer.toString(yValue);
				mc.fontRenderer.drawStringWithShadow(display, xPos + 9 - (mc.fontRenderer.getStringWidth(display) / 2), yPos + 5, 0xffffff);

				itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(blockID, 1, blockMeta), xPos + 18, yPos);
				itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(blockID, blockAmount, blockMeta), xPos + 19, yPos + 1);
			}
		}

		int getYValue() {
			return yValue;
		}
	}

	private enum Background {

		BEDROCK(0, 5, 84, 24), STONE(6, 50, 118, 24), DIRT(51, 60, 152, 24), GRASS(61, 85, 186, 24), SKY(86, Short.MAX_VALUE, 220, 24);

		/** The y-value of the start of the texture's range (inclusive) */
		int start;
		/** The y-value of the end of the texture's range (inclusive) */
		int end;
		/** The texture's location and size in the texture sheet (extras.png) */
		Rectangle texture;

		Background(int start, int end, int u, int v) {
			this.start = start;
			this.end = end;
			texture = new Rectangle(u, v, 34, 16);
		}
	}
}
