package infinitealloys.client.gui;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import java.awt.*;

import infinitealloys.tile.TEEEnergyStorage;
import infinitealloys.util.Funcs;

public final class GuiEnergyStorage extends GuiElectric {

  public TEEEnergyStorage tees;

  public GuiEnergyStorage(InventoryPlayer inventoryPlayer, TEEEnergyStorage tileEntity) {
    super(214, 176, inventoryPlayer, tileEntity);
    tees = tileEntity;
    progressBar.setLocation(70, 57);
    networkIcon = new Point(31, 4);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    GL11.glDisable(GL11.GL_LIGHTING);
    mc.renderEngine.bindTexture(extraIcons);

    // Draw the energy amount
    drawString(fontRendererObj, String.format("%s/%sRK", Funcs.abbreviateNum(tees.getCurrentRK()),
                                              Funcs.abbreviateNum(tees.getMaxRK())),
               topLeft.x + 70, topLeft.y + 26, 0xffffff);

  }

  @Override
  protected ColoredText[] getNetworkStatuses() {
    String status;
    int color = 0x00ff00;

    if (tees.isHostingNetwork()) {
      color = 0x0060ff;
      int clients = tees.getNetworkSize();

      // A string that says this TE is hosting a network and how many clients are connected
      status = Funcs.formatLoc("%s %d %s", "%machine.network.hosting", clients,
                               clients == 1 ? "%machine.network.client" : "%machine.network.clients");
    } else {
      status = Funcs.formatLoc("%s %s", "%machine.network.hostedby",
                               Funcs.getBlockPosString(tees.energyHost));
    }

    return new ColoredText[]{
        new ColoredText(Funcs.formatLoc("%s: %s", "%machine.network.energy", status), color)
    };
  }
}
