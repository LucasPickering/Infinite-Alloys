package infinitealloys.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.BlockPos;

import org.lwjgl.opengl.GL11;

import java.awt.*;

import infinitealloys.util.EnumMachine;
import infinitealloys.util.Funcs;

final class GuiMachineTab extends GuiScreen {

  private static int WIDTH = 24;
  private static int HEIGHT = 20;

  private final RenderItem itemRenderer;
  int xPos, yPos;
  private final EnumMachine machineType;
  private final BlockPos machinePos;
  private final boolean leftSide;
  private final boolean activated;
  private final GuiTextBox textBox;

  GuiMachineTab(Minecraft mc, RenderItem itemRenderer, int xPos, int yPos, EnumMachine machineType,
                BlockPos machinePos, boolean leftSide, boolean activated) {
    this.mc = mc;
    this.itemRenderer = itemRenderer;
    this.xPos = xPos;
    this.yPos = yPos;
    this.machineType = machineType;
    this.machinePos = machinePos;
    this.leftSide = leftSide;
    this.activated = activated;
    width = WIDTH;
    height = HEIGHT;
    textBox = new GuiTextBox(0, 0, Funcs.getLoc("tile." + machineType.getName() + ".name"),
                             Funcs.getBlockPosString(machinePos));
  }

  /**
   * Draws this tab to the screen.
   *
   * @param mouseX the x-position of the mouse, in the same coord system as this tab's position
   * @param mouseY the y-position of the mouse, in the same coord system as this tab's position
   */
  void draw(int mouseX, int mouseY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    mc.renderEngine.bindTexture(GuiMachine.extraIcons);

    Rectangle rect;
    if (leftSide) {
      rect = activated ? GuiElectric.TAB_LEFT_ON : GuiElectric.TAB_LEFT_OFF;
    } else {
      rect = activated ? GuiElectric.TAB_RIGHT_ON : GuiElectric.TAB_RIGHT_OFF;
    }

    drawTexturedModalRect(xPos, yPos, rect.x, rect.y, rect.width, rect.height);
    itemRenderer.renderItemIntoGUI(machineType.getItemStack(), xPos + 5, yPos + 4);

    if (mouseOver(mouseX, mouseY)) {
      textBox.setPosition(mouseX, mouseY);
      textBox.draw();
    }
  }

  boolean mouseOver(int mouseX, int mouseY) {
    return Funcs.pointInZone(mouseX, mouseY, xPos, yPos, width, height);
  }

  EnumMachine getMachineType() {
    return machineType;
  }

  BlockPos getMachinePos() {
    return machinePos;
  }

  boolean isActivated() {
    return activated;
  }
}