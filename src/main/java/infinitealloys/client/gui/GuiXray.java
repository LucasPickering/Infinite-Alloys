package infinitealloys.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.tile.TEEXray;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point3;

public class GuiXray extends GuiElectric {

  private final TEEXray tex;

  /** The scroll bar (width is for the scrolling block) */

  /**
   * TileEntityXray.searchingClient, used to checking if searching just finished
   */
  private boolean wasSearching;

  /**
   * The number of the first displayed line of blocks. Min is 0, max is num of rows - number on
   * screen (5)
   */
  private int scrollPos;

  private BlockButton[] blockButtons = new BlockButton[0];
  private GuiButton searchButton;

  public GuiXray(InventoryPlayer inventoryPlayer, TEEXray tileEntity) {
    super(196, 240, inventoryPlayer, tileEntity);
    tex = tileEntity;
    progressBar.setLocation(54, 5);
    networkIcon = new java.awt.Point(9, 6);
  }

  @Override
  public void initGui() {
    super.initGui();
    buttonList.add(
        searchButton =
            new GuiButton(1, width / 2 - 30, height / 2 - 90, 80, 20,
                          Funcs.getLoc("machine.xray.search")));
    setButtons();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    // If the list of blocks is short enough to fit on one page, disable the scroll bar
    if (blockButtons.length <= 20) {
      Funcs.drawTexturedModalRect(this, topLeft.x + SCROLL_BAR.x, topLeft.y + SCROLL_BAR.y,
                                  SCROLL_OFF);
    }
    // Otherwise, enable it
    else {
      Funcs.drawTexturedModalRect(this, topLeft.x + SCROLL_BAR.x, topLeft.y + SCROLL_BAR.y + (int) (
                                      (float) (SCROLL_BAR.height - 15) / (float) (
                                          blockButtons.length / 4 - 4) * scrollPos),
                                  SCROLL_ON);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    if (tex.refreshGUI) {
      tex.refreshGUI = false;
      setButtons();
    }

    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_DEPTH_TEST);

    searchButton.enabled =
        tex.inventoryStacks[0]
        != null; // Disable the search button if there are no ores in the machine

    // If it was searching last tick and it's now done, refresh the buttons
    if (wasSearching && tex.getProcessProgress() == 0) {
      setButtons();
    }
    wasSearching =
        tex.getProcessProgress() > 0; // Set the searching status for this tick (used next tick)

    Funcs.bindTexture(GuiMachine.extraIcons);

    for (int i = scrollPos * 4; i < blockButtons.length && i < scrollPos * 4 + 20; i++) {
      blockButtons[i].drawButton();
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (mouseButton == 0) { // Was the left mouse button clicked?
      for (int i = 0; i < blockButtons.length; i++) { // Iterate though each block button
        // Was this button clicked?
        if (Funcs.mouseInZone(mouseX - topLeft.x, mouseY - topLeft.y,
                              blockButtons[i].xPos, blockButtons[i].yPos, blockButtons[i].width,
                              blockButtons[i].height)) {
          // Was there already a selected button? If so, deselect it.
          if (tex.selectedButton >= 0) {
            blockButtons[tex.selectedButton].selected = false;
          }

          // Clear the highlighted blocks from the last selected button
          InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();

          // Was this button already selected? If so, none of the buttons are selected now
          if (tex.selectedButton == i) {
            tex.selectedButton = -1;
          }

          // This button wasn't already selected
          else {
            // This button is now selected
            tex.selectedButton = i;
            blockButtons[i].selected = true;

            // The blocks that are represented by the newly selected button get highlighted
            for (final Point3 block : tex.detectedBlocks)
            // Is this block represented by the newly selected button?
            {
              if (block.y == blockButtons[i].getYValue())
              // If so, add this block to the list of blocks to be highlighted. Convert the x and z coords from relative to absolute
              {
                InfiniteAlloys.proxy.gfxHandler.xrayBlocks
                    .add(new Point3(tex.xCoord + block.x, block.y, tex.zCoord + block.z));
              }
            }
          }
        }
      }

      setButtons();

      // Was the scroll up button clicked?
      if (Funcs.mouseInZone(mouseX, mouseY, topLeft.x + 172, topLeft.y + 40, 14, 8)) {
        scroll(-1); // Scroll up
      }

      // Was the scroll down button clicked?
      else if (Funcs.mouseInZone(mouseX, mouseY, topLeft.x + 172, topLeft.y + 147, 14, 8)) {
        scroll(1); // Scroll down
      }
    }
  }

  @Override
  public void handleMouseInput() {
    super.handleMouseInput();
    int scrollAmt = Mouse.getEventDWheel();
    // Scroll one line up or down based on the movement, if the list is long enough to need scrolling
    if (blockButtons.length > 20) {
      scroll(scrollAmt > 0 ? -1 : scrollAmt < 0 ? 1 : 0);
    }
  }

  private void setButtons() {
    if (tex.inventoryStacks[0] == null || tee.getProcessProgress() > 0) {
      blockButtons = new BlockButton[0];
    } else {
      int[] blockCounts = new int[tee.yCoord];
      List<Integer> levels = new ArrayList<Integer>();

      for (Point3 block : tex.detectedBlocks) { // For each detected block
        // If there hasn't been a block for that y-level yet, at that y to the list
        if (blockCounts[block.y]++ == 0) {
          levels.add(block.y);
        }
      }
      blockButtons = new BlockButton[levels.size()];
      for (int i = 0; i < blockButtons.length; i++) {
        blockButtons[i] =
            new BlockButton(i % 4 * 40 + 9, (i / 4 - scrollPos) * 20 + 52,
                            tex.inventoryStacks[0].getItem(), blockCounts[levels.get(i)],
                            tex.inventoryStacks[0].getItemDamage(), levels.get(i));
      }
      if (tex.selectedButton != -1) {
        blockButtons[tex.selectedButton].selected = true;
      }
    }
  }

  /**
   * Scroll the block list the specified amount of lines. Positive is down, negative is up.
   */
  private void scroll(int lines) {
    if (lines > 0 && scrollPos < blockButtons.length / 4 - 4 || lines < 0 && scrollPos > 0) {
      scrollPos += lines;
    }
    setButtons();
  }

  @Override
  public void actionPerformed(GuiButton button) {
    super.actionPerformed(button);
    if (button.id == 1) {
      tex.selectedButton = -1;
      InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
      tex.shouldSearch = true;
      tex.syncToServer();
    }
  }

  /**
   * A button that represents a type of a block, its y-level, and the quantity of that block in the
   * y-level
   */
  private class BlockButton {

    private final int xPos, yPos;
    private final int width, height;
    private final Item block;
    private final int blockAmount, blockMeta;

    /**
     * The yValue of blocks that this button represents
     */
    private final int yValue;

    private Background background;
    private boolean selected;

    private BlockButton(int xPos, int yPos, Item block, int blockAmount, int blockMeta,
                        int yValue) {
      this.xPos = xPos;
      this.yPos = yPos;
      this.block = block;
      this.blockAmount = blockAmount;
      this.blockMeta = blockMeta;
      this.yValue = yValue;
      width = 33;
      height = 15;

      // Set the background of the button based on its y-value
      for (final Background bg : Background.values()) {
        if (bg.start <= yValue && yValue <= bg.end) {
          background = bg;
          break;
        }
      }
    }

    private void drawButton() {
      if (blockAmount > 0) {
        // Draw the background texture for the button
        Funcs.bindTexture(GuiMachine.extraIcons);
        drawTexturedModalRect(xPos, yPos, background.texture.x, background.texture.y,
                              background.texture.width, background.texture.height);

        // If this button is selected, draw an overlay to indicate that
        if (selected) {
          drawHorizontalLine(xPos - 1, xPos + width + 1, yPos - 1, 0xff000000); // Top
          drawHorizontalLine(xPos - 1, xPos + width + 1, yPos + height + 1, 0xff000000); // Bottom
          drawVerticalLine(xPos - 1, yPos + height + 1, yPos - 1, 0xff000000);// Left
          drawVerticalLine(xPos + width + 1, yPos + height + 1, yPos - 1, 0xff000000);// Right
        }

        // Draw the yValue string
        fontRendererObj.drawStringWithShadow(yValue + "", xPos + 9 - fontRendererObj.getStringWidth(
            yValue + "") / 2, yPos + 5, 0xffffff);

        GL11.glEnable(GL11.GL_LIGHTING);

        itemRender
            .renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(block, 1, blockMeta),
                               xPos + 18, yPos);
        itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine,
                                            new ItemStack(block, blockAmount, blockMeta), xPos + 19,
                                            yPos + 1, blockAmount + "");

        GL11.glDisable(GL11.GL_LIGHTING);
      }
    }

    int getYValue() {
      return yValue;
    }
  }

  private enum Background {

    BEDROCK(0, 5, 84, 24), STONE(6, 50, 118, 24), DIRT(51, 60, 152, 24), GRASS(61, 85, 186,
                                                                               24), SKY(86,
                                                                                        Short.MAX_VALUE,
                                                                                        220, 24);

    /**
     * The y-value of the start of the texture's range (inclusive)
     */
    int start;
    /**
     * The y-value of the end of the texture's range (inclusive)
     */
    int end;
    /**
     * The texture's location and size in the texture sheet (extras.png)
     */
    Rectangle texture;

    Background(int start, int end, int u, int v) {
      this.start = start;
      this.end = end;
      texture = new Rectangle(u, v, 34, 16);
    }
  }
}
