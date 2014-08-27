package infinitealloys.client.render.entity;

import infinitealloys.client.model.entity.ModelBossBlaze;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumBoss;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBossBlaze extends RenderLiving {

	private final ResourceLocation textureLocation = new ResourceLocation(Consts.TEXTURE_PREFIX + "textures/entity/" + EnumBoss.BLAZE.getName() + ".png");

	public RenderBossBlaze() {
		super(new ModelBossBlaze(), 2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return textureLocation;
	}
}
