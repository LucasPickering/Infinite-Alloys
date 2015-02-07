package infinitealloys.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import infinitealloys.block.IABlocks;
import infinitealloys.client.EnumHelp;
import infinitealloys.item.IAItems;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.network.MessageWand;
import infinitealloys.tile.IHost;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point3;

public class GuiInternetWand extends GuiScreen {

  private final ResourceLocation background = Funcs.getGuiTexture("wand");
  private final int WIDTH = 178;
  private final int HEIGHT = 160;
  /**
   * The amount of machines that can appear on the GUI at once
   */
  private final int MAX_ROWS = 5;
  private final Rectangle SCROLL_BAR = new Rectangle(156, 42, 12, 102);

  /**
   * Coordinates of the top-left corner of the GUI
   */
  protected Point topLeft = new Point();

  /**
   * The button that toggles the help screen.
   */
  private GuiButton helpButton;

  /**
   * If the GUI was opened by clicking on a machine, this button adds the machine that was clicked
   * to the wand
   */
  private GuiButton addToWand;

  /**
   * If the GUI was opened by clicking on a machine, this button adds the selected machine to the
   * machine that was clicked
   */
  private GuiButton addSelected;

  /**
   * The list of buttons that apply to each machine
   */
  private final ArrayList<MachineButton> machineButtons = new ArrayList<>();

  /**
   * When help is enabled, slots get a colored outline and a mouse-over description
   */
  private boolean helpEnabled;

  /**
   * Binary integer to represent the buttons that are selected. Right-most digit is the top of the
   * list. There is one bit for each button. 0 is not-selected. 1 is selected.
   */
  private int selectedButtons;

  /**
   * The number of the first displayed line of blocks. Min is 0, max is num of rows minus {@link
   * #MAX_ROWS}
   */
  private int scrollPos;

  private HashMap<String, ColoredText[]> helpText = new HashMap<>();

  public GuiInternetWand() {
    // Make an array with the help title and the lines of help text
    for (EnumHelp help : EnumHelp.getNetworkWandBoxes()) {
      List<ColoredText> lines = new ArrayList<>();
      lines.add(new ColoredText(Funcs.getLoc("machineHelp." + help.name + ".title"), 0xffffff));
      for (String s : Funcs.getLoc("machineHelp." + help.name + ".info").split("/n")) {
        lines.add(new ColoredText(s, 0xaaaaaa));
      }
      helpText.put(help.name, lines.toArray(new ColoredText[lines.size()]));
    }
  }

  @Override
  public void initGui() {
    topLeft.setLocation((width - WIDTH) / 2, (height - HEIGHT) / 2);

    buttonList.clear();
    buttonList.add(helpButton = new GuiButton(0, width - 20, 0, 20, 20, "?"));
    buttonList.add(addToWand = new GuiButton(1, topLeft.x + 6, topLeft.y + 6, 82, 20,
                                             Funcs.getLoc("wand.addToWand")));
    buttonList.add(addSelected = new GuiButton(2, topLeft.x + 90, topLeft.y + 6, 82, 20,
                                               Funcs.getLoc("wand.addSelected")));

    // Reset button states
    addToWand.enabled = false;
    addSelected.enabled = false;

    ItemStack heldItem = mc.thePlayer.getHeldItem();
    if (heldItem.getItem() == IAItems.internetWand && heldItem.hasTagCompound()) {
      NBTTagCompound tagCompound = heldItem.getTagCompound();

      // Create each button for the machines
      machineButtons.clear(); // Get rid of all the old buttons
      // For each button in the array
      for (int i = 0; i < Consts.WAND_SIZE; i++) {
        // If there is a machine that corresponds to this button
        if (tagCompound.hasKey("Coords" + i)) {
          int[] client = tagCompound.getIntArray("Coords" + i); // Variables for this machine's data
          // If the block is no longer valid
          if (!MachineHelper.isClient(DimensionManager.getWorld(client[0]).getTileEntity(
              client[1], client[2], client[3]))) {
            Funcs.sendPacketToServer(new MessageWand((byte) i)); // Remove it
            ((ItemInternetWand) heldItem.getItem()).removeMachine(heldItem, i);
            i--; // Decrement i so that it repeats this number for the new button
          } else {
            // Create a button
            machineButtons.add(new MachineButton(i, topLeft.x + 7,
                                                 topLeft.y + 42 + (i - scrollPos) * 21,
                                                 client[0], client[1], client[2], client[3]));
          }
        }
      }

      if (tagCompound.hasKey("CoordsCurrent")) {
        int[] a = tagCompound.getIntArray("CoordsCurrent");
        addToWand.enabled = ((ItemInternetWand) heldItem.getItem())
            .isMachineValid(DimensionManager.getWorld(a[0]), heldItem, a[1], a[2], a[3]);

        TileEntity te = DimensionManager.getWorld(a[0]).getTileEntity(a[1], a[2], a[3]);

        addSelected.enabled = selectedButtons != 0;
        // Go over each machine button
        for (MachineButton button : machineButtons) {
          if (button != null
              && (selectedButtons & 1 << button.buttonID) != 0) // If this button is selected
          // If the selected machine is not valid for the block that was clicked
          {
            if (!(te instanceof IHost)
                || button.dimensionID != te.getWorldObj().provider.dimensionId || !((IHost) te)
                .isClientValid(new Point3(button.machineX, button.machineY, button.machineZ))) {
              addSelected.enabled = false; // Set the button to false
            }
          }
        }
      }

      if (0 <= machineButtons.size() - MAX_ROWS && machineButtons.size() - MAX_ROWS < scrollPos) {
        scrollPos--;
      }
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTick) {
    Funcs.bindTexture(background);
    drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, WIDTH, HEIGHT);
    super.drawScreen(mouseX, mouseY, partialTick);

    Funcs.bindTexture(GuiMachine.extraIcons);
    GL11.glPushMatrix();
    GL11.glColor4f(1, 1, 1, 1);
    // If the list of machines is short enough to fit on one page, disable the scroll bar
    if (machineButtons.size() <= MAX_ROWS) {
      Funcs.drawTexturedModalRect(this, topLeft.x + SCROLL_BAR.x, topLeft.y + SCROLL_BAR.y,
                                  GuiMachine.SCROLL_OFF);
    }
    // Otherwise, enable it
    else {
      Funcs.drawTexturedModalRect(this, topLeft.x + SCROLL_BAR.x,
                                  topLeft.y + SCROLL_BAR.y + (int)
                                      ((float) (SCROLL_BAR.height - GuiMachine.SCROLL_ON.height)
                                       / (float) (machineButtons.size() - 5) * scrollPos),
                                  GuiMachine.SCROLL_ON);
    }
    GL11.glPopMatrix();

    for (MachineButton button : machineButtons) {
      if (button != null) {
        button.drawButton();
      }
    }

    GL11.glPushMatrix();
    GL11.glTranslatef(topLeft.x, topLeft.y, 0);

    mc.fontRenderer.drawStringWithShadow("D", 30, 30, 0xffffff);
    mc.fontRenderer.drawStringWithShadow("X", 62, 30, 0xffffff);
    mc.fontRenderer.drawStringWithShadow("Y", 86, 30, 0xffffff);
    mc.fontRenderer.drawStringWithShadow("Z", 110, 30, 0xffffff);

    // Draw the help dialogue and shade the help zone if help is enabled and the mouse is over a help zone
    if (helpEnabled) {
      EnumHelp
          hoveredZone =
          null; // The help zone that the mouse is over to render to dialogue later, null if mouse is not over a zone\
      for (EnumHelp help : EnumHelp.getNetworkWandBoxes()) {
        // Draw zone outline, add alpha to make the rectangles opaque
        drawRect(help.x, help.y, help.x + help.w, help.y + 1,
                 0xff000000 + help.color); // Top of outline box
        drawRect(help.x, help.y + help.h, help.x + help.w, help.y + help.h - 1,
                 0xff000000 + help.color); // Bottom of outline box
        drawRect(help.x, help.y, help.x + 1, help.y + help.h - 1,
                 0xff000000 + help.color); // Left side of outline box
        drawRect(help.x + help.w - 1, help.y, help.x + help.w, help.y + help.h,
                 0xff000000 + help.color); // Right side of outline box

        // Set hoveredZone to this zone if it hasn't been set already and the mouse is over this zone
        if (hoveredZone == null && Funcs
            .mouseInZone(mouseX, mouseY, topLeft.x + help.x, topLeft.y + help.y, help.w, help.h)) {
          hoveredZone = help;
        }
      }

      if (hoveredZone != null) {
        // Fill in the zone with an smaller 4th hex pair for less alpha
        drawRect(hoveredZone.x, hoveredZone.y, hoveredZone.x + hoveredZone.w,
                 hoveredZone.y + hoveredZone.h, 0x60000000 + hoveredZone.color);
        new GuiTextBox(fontRendererObj, mouseX - topLeft.x, mouseY - topLeft.y,
                       helpText.get(hoveredZone.name)).draw(); // Draw text box with help info
      }
    }
    GL11.glPopMatrix();
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (mouseButton == 0) { // If it was a left-click and there are stored machines
      for (int i = scrollPos; i < machineButtons.size() && i < scrollPos + MAX_ROWS; i++) {
        MachineButton button = machineButtons.get(i);
        if (button != null && Funcs
            .mouseInZone(mouseX, mouseY, button.xPos, button.yPos, button.width,
                         button.height)) { // If this button was clicked
          if (!isCtrlKeyDown()
              && !isShiftKeyDown()) // If the CTRL or Shift key wasn't held, set all buttons to 0
          {
            selectedButtons = 0;
          }
          selectedButtons ^= 1 << i; // Invert its selected state

          if (isShiftKeyDown()) {
            int firstOne = -1; // The index of the position of the rightmost 1 in the binary integer
            int lastOne = -1; // The index of the position of the leftmost 1 in the binary integer
            for (int j = 0; j < Consts.WAND_SIZE; j++) { // Go over each bit in the integer
              if ((selectedButtons & 1 << j) != 0) { // If the bit is 1
                if (firstOne == -1) // If the rightmost 1 has not been found already
                {
                  firstOne = j; // This is the rightmost 1
                }
                lastOne = j; // This is the leftmost 1 (so far)
              }
            }
            if (firstOne != -1) // If there is a 1 in the integer
            // Loop from the first 1 to the last 1
            {
              for (int j = firstOne; j <= lastOne; j++) {
                selectedButtons |= 1 << j; // Set each bit to 1
              }
            }
          }
          break;
        }
      }

      // Was the scroll up button clicked?
      if (Funcs.mouseInZone(mouseX, mouseY, topLeft.x + 155, topLeft.y + 40, 14, 8)) {
        scroll(true); // Scroll up
      }

      // Was the scroll down button clicked?
      else if (Funcs.mouseInZone(mouseX, mouseY, topLeft.x + 155, topLeft.y + 147, 14, 8)) {
        scroll(false); // Scroll down
      }

      initGui();
    }
  }

  @Override
  public void actionPerformed(GuiButton button) {
    ItemStack heldItem = mc.thePlayer.getHeldItem();
    if (button == helpButton) {
      helpEnabled = !helpEnabled;
    } else if (button == addToWand) {
      int[] a = heldItem.getTagCompound().getIntArray("CoordsCurrent");
      Funcs.sendPacketToServer(new MessageWand(new Point3(a[1], a[2], a[3])));
      ((ItemInternetWand) heldItem.getItem()).addMachine(mc.theWorld, heldItem, a[1], a[2], a[3]);
    } else if (button == addSelected) {
      int[] host = heldItem.getTagCompound().getIntArray("CoordsCurrent");

      // If this is a host
      if (mc.theWorld.getTileEntity(host[1], host[2], host[3]) instanceof IHost) {
        // Go over each button
        for (MachineButton machineButton : machineButtons) {
          // If this button is selected
          if (machineButton != null && (selectedButtons & 1 << machineButton.buttonID) != 0) {
            // Add the selected machine to the host
            int[] client =
                heldItem.getTagCompound().getIntArray("Coords" + (machineButton.buttonID));
            // They're in the same dimension
            if (host[0] == client[0]) {
              ((IHost) mc.theWorld.getTileEntity(host[1], host[2], host[3]))
                  .addClientWithChecks(mc.thePlayer, new Point3(client[1], client[2], client[3]),
                                       true);
            }
          }
        }
      }
    } else {
      Funcs.sendPacketToServer(new MessageWand((byte) (button.id - 3)));
      ((ItemInternetWand) heldItem.getItem()).removeMachine(heldItem, (byte) button.id - 3);
    }
  }

  @Override
  public void handleMouseInput() {
    super.handleMouseInput();
    int scrollAmt = Mouse.getEventDWheel();
    if (scrollAmt != 0) {
      scroll(scrollAmt > 0); // Scroll one line up or down based on wheel movement
    }
    initGui();
  }

  /**
   * Scroll the machine list one line, with direction given by the parameter
   */
  private void scroll(boolean up) {
    if (up && scrollPos > 0) {
      scrollPos--;
    }

    if (!up && scrollPos + MAX_ROWS <= machineButtons.size() - 1) {
      scrollPos++;
    }
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  /**
   * A button that represents a machine with its texture and coordinates
   */
  private class MachineButton {

    final int width = 118;
    final int height = 17;

    boolean visible;

    int buttonID;
    int xPos;
    int yPos;

    int machineID;
    int dimensionID;
    int machineX;
    int machineY;
    int machineZ;

    GuiButton removeButton;

    MachineButton(int buttonID, int xPos, int yPos, int dimensionID, int machineX, int machineY,
                  int machineZ) {
      super();
      this.buttonID = buttonID;
      this.xPos = xPos;
      this.yPos = yPos;
      this.dimensionID = dimensionID;
      machineID =
          DimensionManager.getWorld(dimensionID).getBlockMetadata(machineX, machineY, machineZ);
      this.machineX = machineX;
      this.machineY = machineY;
      this.machineZ = machineZ;

      visible = scrollPos <= buttonID && buttonID < scrollPos + MAX_ROWS;

      removeButton = new GuiButton(buttonID + 3, xPos + 122, yPos - 1, 20, 20, "X");
      removeButton.visible = visible;
      buttonList.add(removeButton);
    }

    void drawButton() {
      Funcs.bindTexture(GuiMachine.extraIcons);

      // If the button isn't currently in the scroll window, don't draw it
      if (!visible) {
        return;
      }

      if ((1 << buttonID & selectedButtons)
          != 0) { // If this button is selected, draw a box around it
        int yPosBox = yPos;
        int heightBox = height;

        // If there is a button before this one and it is selected, extend this box to connect to that one
        if (buttonID != 0 && (1 << buttonID - 1 & selectedButtons) != 0) {
          yPosBox -= 3;
          heightBox += 3;
        }

        // Only draw the top line if this box isn't connecting to the one above it
        else {
          drawHorizontalLine(xPos - 1, xPos + width + 1, yPosBox - 1, 0xff26a0da); // Top
        }

        // If this is the last button or if the button below this isn't selected, draw the bottom line
        if (buttonID == machineButtons.size() - 1 || (1 << buttonID + 1 & selectedButtons) == 0) {
          drawHorizontalLine(xPos - 1, xPos + width + 1, yPosBox + heightBox + 1,
                             0xff26a0da); // Bottom
        }

        drawVerticalLine(xPos - 1, yPosBox + heightBox + 1, yPosBox - 1, 0xff26a0da);// Left
        drawVerticalLine(xPos + width + 1, yPosBox + heightBox + 1, yPosBox - 1,
                         0xff26a0da);// Right
        drawRect(xPos, yPosBox, xPos + width + 1, yPosBox + heightBox + 1, 0xaacbe8f6); // Shading
      }

      GL11.glColor3f(1F, 1F, 1F); // Reset the color

      // Draw the string for the coordinates
      mc.fontRenderer.drawStringWithShadow(dimensionID + "", xPos + 26 - mc.fontRenderer
                                                                             .getStringWidth(
                                                                                 dimensionID + "")
                                                                         / 2, yPos + 5, 0xffffff);
      mc.fontRenderer.drawStringWithShadow(machineX + "", xPos + 58 - mc.fontRenderer
                                                                          .getStringWidth(
                                                                              machineX + "") / 2,
                                           yPos + 5, 0xffffff);
      mc.fontRenderer.drawStringWithShadow(machineY + "", xPos + 82 - mc.fontRenderer
                                                                          .getStringWidth(
                                                                              machineY + "") / 2,
                                           yPos + 5, 0xffffff);
      mc.fontRenderer.drawStringWithShadow(machineZ + "", xPos + 106 - mc.fontRenderer
                                                                           .getStringWidth(
                                                                               machineZ + "") / 2,
                                           yPos + 5, 0xffffff);

      itemRender.renderItemIntoGUI(fontRendererObj, mc.getTextureManager(),
                                   new ItemStack(IABlocks.machine, 1, machineID), xPos, yPos + 1);
    }
  }
}