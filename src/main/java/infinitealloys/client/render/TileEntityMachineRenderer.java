package infinitealloys.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import infinitealloys.util.Consts;

public class TileEntityMachineRenderer extends TileEntitySpecialRenderer {

  private final String name;
  private final ModelBase model;

  public TileEntityMachineRenderer(String name, ModelBase model) {
    this.name = name;
    this.model = model;
  }

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
    ResourceLocation textures =
        (new ResourceLocation(Consts.TEXTURE_PREFIX + "textures/blocks/" + name + ".png"));
    Minecraft.getMinecraft().renderEngine.bindTexture(textures);
    GL11.glPushMatrix();
    GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
    model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
    GL11.glPopMatrix();
    GL11.glPopMatrix();
  }
}
