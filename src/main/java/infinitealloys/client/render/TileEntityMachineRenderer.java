package infinitealloys.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.Funcs;

public class TileEntityMachineRenderer extends TileEntitySpecialRenderer {

  private final EnumMachine machine;

  public TileEntityMachineRenderer(EnumMachine machine) {
    this.machine = machine;
  }

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
    ResourceLocation textures =
        new ResourceLocation(Consts.TEXTURE_PREFIX + "textures/blocks/" + machine.name + ".png");
    Minecraft.getMinecraft().renderEngine.bindTexture(textures);
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
    GL11.glRotatef(180F, 1F, 0F, 0F);
    GL11.glRotatef(Funcs.facingToYaw(((TileEntityMachine) te).orientation), 0F, 1F, 0F);
    machine.model.render(null, 0F, 0F, -0.1F, 0F, 0F, 0.0625F);
    GL11.glPopMatrix();
  }
}
