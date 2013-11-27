package infinitealloys.client.gui;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.network.PacketXraySearch;
import infinitealloys.tile.TEEXray;
import infinitealloys.util.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiXray extends GuiElectric {

	private final TEEXray tex;

	/** The scroll bar (width is for the scrolling block) */
	private final Rectangle SCROLL_BAR = new Rectangle(172, 51, 12, 96);

	/** TileEntityXray.searchingClient, used to checking if searching just finished */
	private boolean wasSearching;

	/** The number of the first displayed line of blocks. Min is 0, max is num of rows - number on screen (5) */
	private int scrollPos = 0;

	private GuiBlockButton[] blockButtons = new GuiBlockButton[0];
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
		buttonList.add(searchButton = new GuiButton(1, width / 2 - 30, height / 2 - 90, 80, 20, "Search"));
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
			drawTexturedModalRect(SCROLL_BAR.x, SCROLL_BAR.y, SCROLL_OFF.x, SCROLL_OFF.y, SCROLL_OFF.width, SCROLL_OFF.height);
		else
			drawTexturedModalRect(SCROLL_BAR.x, SCROLL_BAR.y + (int)((float)(SCROLL_BAR.height - 15) / (float)(blockButtons.length / 4 - 4) * scrollPos),
					SCROLL_ON.x, SCROLL_ON.y, SCROLL_ON.width, SCROLL_ON.height);
		if(wasSearching && tex.getProcessProgress() == 0)
			setButtons();
		wasSearching = tex.getProcessProgress() > 0;
		for(int i = scrollPos * 4; i < blockButtons.length && i < scrollPos * 4 + 20; i++)
			blockButtons[i].drawButton();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		// Was the left mouse button clicked?
		if(mouseButton == 0) {
			// Iterate though each block button
			for(int i = 0; i < blockButtons.length; i++) {
				// Was this button clicked?
				if(blockButtons[i].mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
					// Was there already a selected button? If so, deselect it.
					if(tex.selectedButton >= 0)
						blockButtons[tex.selectedButton].activated = false;

					// Clear the highlighted blocks from the last selected button
					InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();

					// Was this button already selected? If so, none of the buttons are selected now
					if(tex.selectedButton == i)
						tex.selectedButton = -1;

					// This button wasn't already selected
					else {
						// This button is now selected
						tex.selectedButton = i;
						blockButtons[i].activated = true;

						// The blocks that are represented by the newly selected button get highlighted
						for(Point block : tex.detectedBlocks)
							// Is this block represented by the newly selected button?
							if(block.y == blockButtons[i].getYValue())
								// If so, add this block to the list of blocks to be highlighted. Convert the x and z coords from relative to absolute
								InfiniteAlloys.proxy.gfxHandler.xrayBlocks.add(new Point(tex.xCoord + block.x, block.y, tex.zCoord + block.z));
					}
				}
			}

			setButtons();
			// Was the scroll up button clicked?
			if(mouseInZone(mouseX, mouseY, topLeft.x + 172, topLeft.y + 40, 14, 8))
				// Scroll up
				scroll(-1);
			// Was the scroll down button clicked?
			else if(mouseInZone(mouseX, mouseY, topLeft.x + 172, topLeft.y + 147, 14, 8))
				// Scroll down
				scroll(1);
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int scrollAmt = Mouse.getEventDWheel();
		// Scroll one line up or down based on the movement, if the list is long enough to need scrolling
		if(blockButtons.length > 20)
			scroll(scrollAmt > 0 ? -1 : scrollAmt < 0 ? 1 : 0);
	}

	private void setButtons() {
		if(tex.inventoryStacks[0] == null || tem.getProcessProgress() > 0)
			blockButtons = new GuiBlockButton[0];
		else {
			int[] blockCounts = new int[tem.yCoord];
			List<Integer> levels = new ArrayList<Integer>();
			// Go through each detected block
			for(Point block : tex.detectedBlocks) {
				// For each block if there hasn't been a block for that y-level yet, at that y to the list
				if(blockCounts[block.y]++ == 0)
					levels.add(block.y);
			}
			blockButtons = new GuiBlockButton[levels.size()];
			for(int i = 0; i < blockButtons.length; i++)
				blockButtons[i] = new GuiBlockButton(mc, itemRenderer, i % 4 * 40 + 9, (i / 4 - scrollPos) * 20 + 52, tex.inventoryStacks[0].itemID, blockCounts[levels.get(i)],
						tex.inventoryStacks[0].getItemDamage(), levels.get(i));
			if(tex.selectedButton != -1)
				blockButtons[tex.selectedButton].activated = true;
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
}
