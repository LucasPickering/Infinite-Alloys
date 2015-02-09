package infinitealloys.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import java.awt.Point;

import infinitealloys.util.Funcs;

public class GuiSummoner extends GuiScreen {

  private final ResourceLocation background = Funcs.getGuiTexture("summoner");
  private final int WIDTH = 178;
  private final int HEIGHT = 160;

  /**
   * Coordinates of the top-left corner of the GUI
   */
  private final Point topLeft = new Point();

  @Override
  public void initGui() {
    topLeft.setLocation((width - WIDTH) / 2, (height - HEIGHT) / 2);

    buttonList.clear();
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTick) {
    mc.renderEngine.bindTexture(background);
    drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, WIDTH, HEIGHT);
    super.drawScreen(mouseX, mouseY, partialTick);

    mc.renderEngine.bindTexture(GuiMachine.extraIcons);
    GL11.glPushMatrix();

    GL11.glPopMatrix();
  }
}
