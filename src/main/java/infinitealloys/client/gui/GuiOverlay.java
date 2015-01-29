package infinitealloys.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.AxisAlignedBB;

import infinitealloys.entity.EntityIABoss;
import infinitealloys.util.Funcs;

public class GuiOverlay extends GuiScreen {

  public GuiOverlay() {
    Minecraft.getMinecraft().displayGuiScreen(this);
  }

  @SuppressWarnings("unchecked")
  public void drawHealthBar() {
    final int boxSize = 20;
    EntityIABoss nearestBoss = (EntityIABoss) mc.theWorld.findNearestEntityWithinAABB(
        EntityIABoss.class, AxisAlignedBB.getBoundingBox(
            mc.thePlayer.posX - boxSize, mc.thePlayer.posY - boxSize, mc.thePlayer.posZ - boxSize,
            mc.thePlayer.posX + boxSize, mc.thePlayer.posY + boxSize, mc.thePlayer.posZ + boxSize),
        mc.thePlayer);

    if (nearestBoss != null) {
      drawCenteredString(fontRendererObj, nearestBoss.getCommandSenderName(),
                         width / 2, 2, 0xffffff);

      Funcs.bindTexture(GuiMachine.extraIcons);
      int barX = (width - GuiMachine.HEALTH_BAR_BG.width) / 2;
      final int barY = 12;
      Funcs.drawTexturedModalRect(this, barX, barY, GuiMachine.HEALTH_BAR_BG);
      int foregroundWidth = (int) (nearestBoss.getHealth() / nearestBoss.getMaxHealth()
                                   * GuiMachine.HEALTH_BAR_FG.width);
      drawTexturedModalRect(barX, barY, GuiMachine.HEALTH_BAR_FG.x, GuiMachine.HEALTH_BAR_FG.y,
                            foregroundWidth, GuiMachine.HEALTH_BAR_FG.height);
    }
  }
}
