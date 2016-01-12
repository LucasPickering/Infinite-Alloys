package infinitealloys.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import infinitealloys.client.EnumHelp;
import infinitealloys.network.MessageOpenGui;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;

public abstract class GuiMachine extends GuiContainer {

  // The position for each item in the texture sheet "extras.png"
  static final Rectangle TAB_LEFT_OFF = new Rectangle(0, 0, 24, 24);
  static final Rectangle TAB_LEFT_ON = new Rectangle(24, 0, 28, 24);
  static final Rectangle TAB_RIGHT_OFF = new Rectangle(52, 0, 29, 24);
  static final Rectangle TAB_RIGHT_ON = new Rectangle(81, 0, 28, 24);
  static final Rectangle PROGRESS_BAR = new Rectangle(109, 0, 108, 18);
  static final Rectangle SCROLL_ON = new Rectangle(217, 0, 12, 15);
  static final Rectangle SCROLL_OFF = new Rectangle(229, 0, 12, 15);
  static final Rectangle SCROLL_BAR = new Rectangle(172, 51, 12, 96);
  static final Rectangle NETWORK_ICON = new Rectangle(0, 40, 16, 16);
  static final Rectangle HEALTH_BAR_BG = new Rectangle(16, 40, 182, 5);
  static final Rectangle HEALTH_BAR_FG = new Rectangle(16, 45, 182, 5);

  /**
   * The texture resource for the texture item
   */
  static final ResourceLocation extraIcons = Funcs.getGuiTexture("extras");
  /**
   * The backgroundIcon texture
   */
  protected ResourceLocation background;

  /**
   * Coordinates of the top-left corner of the GUI
   */
  protected Point topLeft = new Point();

  protected TileEntityMachine tem;
  protected GuiMachineTab computerTab;
  protected final List<GuiMachineTab> machineTabs = new LinkedList<>();
  /**
   * Coordinates of the network icon, which shows network statuses when hovered over
   */
  protected Point networkIcon;
  /**
   * When help is enabled, slots get a colored outline and a mouse-over description
   */
  protected boolean helpEnabled;
  private Map<String, ColoredText[]> helpText = new HashMap<>();

  public GuiMachine(int xSize, int ySize, InventoryPlayer inventoryPlayer,
                    TileEntityMachine tileEntity) {
    super(tileEntity.getMachineType().getNewContainer(inventoryPlayer, tileEntity));
    this.xSize = xSize;
    this.ySize = ySize;
    tem = tileEntity;
    background = Funcs.getGuiTexture(tem.getMachineType().name);
    // Make an array with the help title and the lines of help text
    for (EnumHelp help : tem.getMachineType().getHelpBoxes()) {
      List<ColoredText> lines = new LinkedList<>();
      lines.add(new ColoredText(Funcs.getLoc("machineHelp." + help.name + ".title"), 0xffffff));
      for (String s : Funcs.getLoc("machineHelp." + help.name + ".info").split("/n")) {
        lines.add(new ColoredText(s, 0xaaaaaa));
      }
      helpText.put(help.name, lines.toArray(new ColoredText[lines.size()]));
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void initGui() {
    super.initGui();
    topLeft.setLocation((width - xSize) / 2, (height - ySize) / 2);
    buttonList
        .add(new GuiButton(0, width - 20, 0, 20, 20, "?")); // The button to enable/disable help
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTick) {
    super.drawScreen(mouseX, mouseY, partialTick);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_DEPTH_TEST);

    // Draw the upgrade list if the mouse is over the upgrade slot and help is disabled
    Slot slot = inventorySlots.getSlot(tem.upgradeSlotIndex);
    if (!helpEnabled && Funcs.pointInZone(mouseX, mouseY, slot.xDisplayPosition + topLeft.x,
                                          slot.yDisplayPosition + topLeft.y, 16, 16)) {
      List<ColoredText> lines = new LinkedList<>();
      lines.add(new ColoredText(Funcs.getLoc("general.upgrades"), 0xffffff));

      for (EnumUpgrade upgradeType : EnumUpgrade.values()) {
        int tier = tem.getUpgradeTier(upgradeType);
        if (tier > 0) {
          lines.add(new ColoredText(upgradeType.getItemStackForTier(tier).getDisplayName(),
                                    0xaaaaaa));
        }
      }

      new GuiTextBox(mouseX, mouseY, lines.toArray(new ColoredText[lines.size()])).draw();
    }

    // Draw the network info if the mouse is over the network icon and help is disabled
    if (!helpEnabled && networkIcon != null && Funcs
        .pointInZone(mouseX, mouseY, topLeft.x + networkIcon.x, topLeft.y + networkIcon.y,
                     NETWORK_ICON.width, NETWORK_ICON.height))
    // Draw a text box with a line for each network show its status and information
    {
      new GuiTextBox(mouseX, mouseY, getNetworkStatuses()).draw();
    }

    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_LIGHTING);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
    mc.renderEngine.bindTexture(background);
    drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    mc.renderEngine.bindTexture(extraIcons);
    GL11.glPushMatrix();

    // Draw the network icon if this GUI has one
    if (networkIcon != null) {
      Funcs.drawTexturedModalRect(this, networkIcon.x, networkIcon.y,
                                  NETWORK_ICON); // Draw the network icon
    }

    // Draw the tabs of other machines on the network if this machine is connected to a computer
    machineTabs.clear();
    if (tem.computerHost != null) {
      TEMComputer tec = (TEMComputer) mc.theWorld.getTileEntity(tem.computerHost);
      computerTab = new GuiMachineTab(mc, itemRender, -24, 6, tec.getMachineType(), tec.getPos(),
                                      true, tem.getPos().equals(tem.computerHost));
      computerTab.draw(mouseX - topLeft.x, mouseY - topLeft.y);

      BlockPos[] clients = tec.getClients();
      // For each client
      for (int i = 0; i < clients.length; i++) {
        GuiMachineTab tab =
            new GuiMachineTab(mc, itemRender, i / 5 * 197 - 24, i % 5 * 25 + 36,
                              ((TileEntityElectric) mc.theWorld.getTileEntity(clients[i]))
                                  .getMachineType(),
                              clients[i], i / 5 == 0, clients[i].equals(tem.getPos()));
        machineTabs.add(tab);

        tab.draw(mouseX - topLeft.x, mouseY - topLeft.y); // Draw the tab
      }
    }

    // Draw the help dialogue and shade the help zone if help is enabled and
    // the mouse is over a help zone
    if (helpEnabled) {
      // The help zone that the mouse is over to render to dialogue later,
      // null if mouse is not over a zone
      EnumHelp hoveredZone = null;
      for (EnumHelp help : tem.getMachineType().getHelpBoxes()) {
        final int color = 0xff000000 + help.color; // Add alpha to make it opaque

        // Draw the outline box
        drawHorizontalLine(help.x, help.x + help.w - 1, help.y, color); // Top
        drawHorizontalLine(help.x, help.x + help.w - 1, help.y + help.h - 1, color); // Bottom
        drawVerticalLine(help.x, help.y, help.y + help.h - 1, color); // Left
        drawVerticalLine(help.x + help.w - 1, help.y, help.y + help.h - 1, color); // Right

        // Set hoveredZone to this zone if it hasn't been set already and the mouse is over this zone
        if (hoveredZone == null && Funcs.pointInZone(
            mouseX, mouseY, topLeft.x + help.x, topLeft.y + help.y, help.w, help.h)) {
          hoveredZone = help;
        }
      }

      if (hoveredZone != null) {
        // Fill in the zone with an smaller 4th hex pair for less alpha
        drawRect(hoveredZone.x, hoveredZone.y, hoveredZone.x + hoveredZone.w,
                 hoveredZone.y + hoveredZone.h, 0x60000000 + hoveredZone.color);

        // Draw text box with help info
        new GuiTextBox(mouseX - topLeft.x, mouseY - topLeft.y, helpText.get(hoveredZone.name)).draw();
      }
    }
    GL11.glPopMatrix();

    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_LIGHTING);
  }

  @Override
  public void actionPerformed(GuiButton button) {
    if (button.id == 0) {
      helpEnabled = !helpEnabled;
    }
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    World world = Minecraft.getMinecraft().theWorld;
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;

    // If the computer tab was clicked...
    if (computerTab != null && computerTab.mouseOver(mouseX - topLeft.x, mouseY - topLeft.y)) {
      // If the computer tab isn't activated (it's for another GUI)
      if (!computerTab.isActivated()) {
        // Open the GUI corresponding to the computer tab
        computerTab.getMachineType().getBlock().openGui(world, player, computerTab.getMachinePos());
        Funcs.sendPacketToServer(new MessageOpenGui(computerTab.getMachinePos())); // TODO: Necessary?
      }
      return; // No need to check if anything else was clicked
    }

    // For each machine tab...
    for (GuiMachineTab tab : machineTabs) {
      // If this tab was clicked...
      if (tab.mouseOver(mouseX - topLeft.x, mouseY - topLeft.y)) {
        // If this tab isn't activated (it's for another GUI)
        if (!tab.isActivated()) {
          // Open the GUI corresponding to this tab
          tab.getMachineType().getBlock().openGui(world, player, tab.getMachinePos());
          Funcs.sendPacketToServer(new MessageOpenGui(tab.getMachinePos())); // TODO: Necessary?
        }
        return; // No need to check if anything else was clicked
      }
    }
  }

  protected abstract ColoredText[] getNetworkStatuses();
}