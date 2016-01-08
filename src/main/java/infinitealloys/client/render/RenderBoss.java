package infinitealloys.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumBoss;

public final class RenderBoss extends RenderLiving {

  private final ResourceLocation textureLocation;

  public RenderBoss(EnumBoss bossType) {
    super(Minecraft.getMinecraft().getRenderManager(), bossType.model, 2F);
    textureLocation =
        new ResourceLocation(Consts.TEXTURE_PREFIX + "textures/entity/" + bossType.name + ".png");
  }

  @Override
  protected ResourceLocation getEntityTexture(Entity entity) {
    return textureLocation;
  }
}
