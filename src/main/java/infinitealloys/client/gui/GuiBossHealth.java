package infinitealloys.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

import infinitealloys.entity.EntityIABoss;
import infinitealloys.util.Funcs;

public class GuiBossHealth extends GuiScreen {

  public GuiBossHealth() {
    Minecraft.getMinecraft().displayGuiScreen(this);
  }

  @SuppressWarnings("unchecked")
  public void drawHealthBar() {
    List<EntityIABoss> nearbyBosses =
        mc.theWorld.getEntitiesWithinAABB(EntityIABoss.class, AxisAlignedBB.getBoundingBox(
            mc.thePlayer.posX - 15, mc.thePlayer.posY - 15, mc.thePlayer.posZ - 15,
            mc.thePlayer.posX + 15, mc.thePlayer.posY + 15, mc.thePlayer.posZ + 15));
    if (!nearbyBosses.isEmpty()) {
      EntityIABoss boss = nearbyBosses.get(0);

      drawCenteredString(fontRendererObj,
                         Funcs.getLoc("entity." + boss.getBossType().name + ".name"),
                         width / 2, 2, 0xffffff);

      Funcs.bindTexture(GuiMachine.extraIcons);
      int barX = (width - GuiMachine.HEALTH_BAR_BG.width) / 2;
      final int barY = 12;
      Funcs.drawTexturedModalRect(this, barX, barY, GuiMachine.HEALTH_BAR_BG);
      System.out.println(boss.getMaxHealth());
      int foregroundWidth = (int) (boss.getHealth() / boss.getMaxHealth()
                                   * GuiMachine.HEALTH_BAR_FG.width);
      drawTexturedModalRect(barX, barY, GuiMachine.HEALTH_BAR_FG.x, GuiMachine.HEALTH_BAR_FG.y,
                            foregroundWidth, GuiMachine.HEALTH_BAR_FG.height);
    }
  }
}
