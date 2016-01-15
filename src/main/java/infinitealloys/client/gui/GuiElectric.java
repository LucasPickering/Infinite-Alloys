package infinitealloys.client.gui;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;

import infinitealloys.tile.TileEntityElectric;
import infinitealloys.util.Funcs;

public abstract class GuiElectric extends GuiMachine {

  protected TileEntityElectric tee;

  /**
   * Coordinates of the progress bar texture, changes by machine but stationary otherwise.
   */
  protected Point progressBar = new Point();
  private final GuiTextBox energyTextBox;
  private final DecimalFormat rkFormat = new DecimalFormat("0.0%");

  public GuiElectric(int xSize, int ySize, InventoryPlayer inventoryPlayer,
                     TileEntityElectric tileEntity) {
    super(xSize, ySize, inventoryPlayer, tileEntity);
    tee = tileEntity;
    energyTextBox = new GuiTextBox(0, 0, new ColoredText(null, 0xffffff),
                                   new ColoredText(null, 0xffffff));
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTick) {
    super.drawScreen(mouseX, mouseY, partialTick);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_DEPTH_TEST);

    // Draw the progress and energy info if the mouse is over the progress bar and help is disabled
    if (!helpEnabled && tee.ticksToProcess > 0
        && Funcs.pointInZone(mouseX, mouseY,
                             topLeft.x + progressBar.x, topLeft.y + progressBar.y,
                             PROGRESS_BAR.width, PROGRESS_BAR.height)) {
      energyTextBox.setPosition(mouseX, mouseY); // Move the box to the mouse cursor

      final int rkChange = tee.shouldProcess() ? tee.getRKChange() : 0;

      // The current process progress displayed as a percent
      energyTextBox.setText(0, rkFormat.format(tee.getProcessProgressScaled(1f)));

      // If the rk change is positive, add '+', then display the rate of change of RK
      energyTextBox.setText(1, (rkChange > 0 ? "+" : "") + rkChange + " RK/t");
      energyTextBox.setColor(1, rkChange < 0 ? 0xff0000 : rkChange > 0 ? 0x00ff00 : 0xffffff);

      energyTextBox.draw();
    }

    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_LIGHTING);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    GL11.glPushMatrix();
    GL11.glTranslatef(topLeft.x, topLeft.y, 0);
    mc.renderEngine.bindTexture(extraIcons);

    // Draw the progress bar overlay
    if (tee.ticksToProcess > 0) {
      drawTexturedModalRect(progressBar.x, progressBar.y, PROGRESS_BAR.x, PROGRESS_BAR.y,
                            (int) tee.getProcessProgressScaled(PROGRESS_BAR.width),
                            PROGRESS_BAR.height);
    }

    GL11.glPopMatrix();
  }

  @Override
  protected ColoredText[] getNetworkStatuses() {
    int color;
    String status;

    if (tee.energyHost == null) {
      color = 0xff0000;
      status = Funcs.getLoc("machine.network.noconnection");
    } else {
      color = 0x00ff00;
      status = Funcs.formatLoc("%s %s", "%machine.network.hostedby",
                               Funcs.getBlockPosString(tee.energyHost));
    }

    return new ColoredText[]{
        new ColoredText(Funcs.formatLoc("%s: %s", "%machine.network.energy", status), color)
    };
  }
}
