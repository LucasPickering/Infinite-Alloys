package infinitealloys.client.gui;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.tile.TEEXray;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point3;

public class GuiXray extends GuiElectric {

  /**
   * Thew amount of columns of {@link BlockButton BlockButtons} that fit on the scroll menu at once
   */
  private final int LIST_WIDTH = 4;

  /**
   * The amount of rows of {@link BlockButton BlockButtons} that fit on the scroll menu at once
   */
  private final int LIST_HEIGHT = 5;

  private final TEEXray tex;

  /**
   * TileEntityXray.searchingClient, used to checking if searching just finished
   */
  private boolean wasProcessing;

  /**
   * The number of the first displayed line of blocks, starting from 0.
   */
  private int scrollPos;

  private BlockButton[] blockButtons = new BlockButton[0];
  private GuiButton searchButton;

  public GuiXray(InventoryPlayer inventoryPlayer, TEEXray tileEntity) {
    super(196, 240, inventoryPlayer, tileEntity);
    tex = tileEntity;
    progressBar.setLocation(54, 5);
    networkIcon = new Point(9, 6);
  }

  @Override
  public void initGui() {
    super.initGui();
    buttonList.add(searchButton = new GuiButton(1, width / 2 - 40, height / 2 - 90, 80, 20,
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
    // Disable the search button if there are no ores in the machine
    searchButton.enabled = tex.inventoryStacks[0] != null;

    // If it was searching last tick and now it's done, refresh the buttons
    if (wasProcessing && tex.getProcessProgress() == 0) {
      setButtons();
    }
    // Set the searching status for this tick (used next tick)
    wasProcessing = tex.getProcessProgress() > 0;

    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    if (wasProcessing) {
      drawCenteredString(fontRendererObj, Funcs.getLoc("machine.xray.searching"), xSize / 2, 56,
                         0xffffff);
    } else if (tex.getRevealBlocks()) {
      if (blockButtons.length == 0) {
        drawCenteredString(fontRendererObj, Funcs.getLoc("machine.xray.noBlocks"), xSize / 2, 56,
                           0xffffff);
      } else {
        Funcs.bindTexture(GuiMachine.extraIcons);
        for (int i = scrollPos * LIST_WIDTH;
             i < blockButtons.length && i < (scrollPos + LIST_HEIGHT) * LIST_WIDTH; i++) {
          blockButtons[i].drawButton();
        }
      }
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
            for (Point3 block : tex.getDetectedBlocks()) {
              // Is this block represented by the newly selected button?
              if (block.y == blockButtons[i].yValue) {
                // If so, add this block to the list of blocks to be highlighted.
                // Convert the x and z coords from relative to absolute.
                InfiniteAlloys.proxy.gfxHandler.xrayBlocks.put(
                    new Point3(tex.xCoord + block.x, block.y, tex.zCoord + block.z),
                    MachineHelper.getDetectableColor(blockButtons[i].block,
                                                     blockButtons[i].blockMeta));
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

  /**
   * Set the value of each {@link BlockButton} based on the current search results.
   */
  private void setButtons() {
    if (tex.inventoryStacks[0] == null || tee.getProcessProgress() > 0) {
      blockButtons = new BlockButton[0];
    } else {
      int[] blockCounts = new int[tee.yCoord];
      ArrayList<Integer> levels = new ArrayList<>();

      // For each detected block
      for (Point3 block : tex.getDetectedBlocks()) {
        // If there hasn't been a block for that y-level yet, at that y to the list
        if (blockCounts[block.y]++ == 0) {
          levels.add(block.y);
        }
      }
      blockButtons = new BlockButton[levels.size()];
      for (int i = 0; i < blockButtons.length; i++) {
        blockButtons[i] = new BlockButton(
            i % LIST_WIDTH * 40 + 9, (i / LIST_WIDTH - scrollPos) * 20 + 52,
            Block.getBlockFromItem(tex.inventoryStacks[0].getItem()), blockCounts[levels.get(i)],
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
    if (lines > 0 && scrollPos < (blockButtons.length - 1) / LIST_WIDTH
        || lines < 0 && scrollPos > 0) {
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
      tex.startProcess();
    }
  }

  /**
   * A button that represents a type of a block, its y-level, and the quantity of that block in the
   * y-level
   */
  private class BlockButton {

    private final int xPos, yPos;
    private final int width, height;
    private final Block block;
    private final int blockAmount, blockMeta;

    /**
     * The yValue of blocks that this button represents
     */
    private final int yValue;

    private Background background;
    private boolean selected;

    private BlockButton(int xPos, int yPos, Block block, int blockAmount, int blockMeta,
                        int yValue) {
      this.xPos = xPos;
      this.yPos = yPos;
      this.block = block;
      this.blockAmount = blockAmount;
      this.blockMeta = blockMeta;
      this.yValue = yValue;
      width = 33;
      height = 15;

      // Set the backgroundIcon of the button based on its y-value
      for (Background bg : Background.values()) {
        if (bg.start <= yValue && yValue <= bg.end) {
          background = bg;
          break;
        }
      }
    }

    private void drawButton() {
      if (blockAmount > 0) {
        // Draw the backgroundIcon texture for the button
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

        itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine,
                                     new ItemStack(block, 1, blockMeta), xPos + 18, yPos);
        itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine,
                                            new ItemStack(block, blockAmount, blockMeta),
                                            xPos + 19, yPos + 1, String.valueOf(blockAmount));

        GL11.glDisable(GL11.GL_LIGHTING);
      }
    }
  }

  private enum Background {
    BEDROCK(0, 5, 84, 24), STONE(6, 50, 118, 24), DIRT(51, 60, 152, 24),
    GRASS(61, 85, 186, 24), SKY(86, Short.MAX_VALUE, 220, 24);

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
