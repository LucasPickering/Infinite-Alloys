package infinitealloys.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Funcs;

public final class GuiMachineTab extends GuiScreen {

  private final RenderItem itemRenderer;
  public TileEntityMachine tem;
  public int xPos, yPos;
  private final boolean leftSide;
  private final boolean activated;

  public GuiMachineTab(Minecraft mc, RenderItem itemRenderer, int xPos, int yPos,
                       TileEntityMachine tem, boolean leftSide, boolean activated) {
    this.mc = mc;
    this.itemRenderer = itemRenderer;
    this.xPos = xPos;
    this.yPos = yPos;
    this.tem = tem;
    this.leftSide = leftSide;
    this.activated = activated;
    width = 20;
    height = 20;
  }

  /**
   * Draws this button to the screen.
   */
  public void drawButton() {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    mc.renderEngine.bindTexture(GuiMachine.extraIcons);
    if (leftSide) {
      if (activated) {
        drawTexturedModalRect(xPos, yPos, GuiElectric.TAB_LEFT_ON.x, GuiElectric.TAB_LEFT_ON.y,
                              GuiElectric.TAB_LEFT_ON.width, GuiElectric.TAB_LEFT_ON.height);
      } else {
        drawTexturedModalRect(xPos, yPos, GuiElectric.TAB_LEFT_OFF.x, GuiElectric.TAB_LEFT_OFF.y,
                              GuiElectric.TAB_LEFT_OFF.width, GuiElectric.TAB_LEFT_OFF.height);
      }
    } else {
      if (activated) {
        drawTexturedModalRect(xPos, yPos, GuiElectric.TAB_RIGHT_ON.x, GuiElectric.TAB_RIGHT_ON.y,
                              GuiElectric.TAB_RIGHT_ON.width, GuiElectric.TAB_RIGHT_ON.height);
      } else {
        drawTexturedModalRect(xPos, yPos, GuiElectric.TAB_RIGHT_OFF.x, GuiElectric.TAB_RIGHT_OFF.y,
                              GuiElectric.TAB_RIGHT_OFF.width, GuiElectric.TAB_RIGHT_OFF.height);
      }
    }
    itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine,
                                   new ItemStack(tem.getBlockType(), 1, tem.getBlockMetadata()),
                                   xPos + 5, yPos + 4);
  }

  public boolean mousePressed(int mouseX, int mouseY) {
    return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
  }
}