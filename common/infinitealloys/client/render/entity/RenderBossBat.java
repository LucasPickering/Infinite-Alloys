package infinitealloys.client.render.entity;

import infinitealloys.client.model.entity.ModelBossBat;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumBoss;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBossBat extends RenderLiving {

	private final ResourceLocation textureLocation = new ResourceLocation(Consts.TEXTURE_PREFIX + "textures/entity/" + EnumBoss.BAT.getName() + ".png");

	public RenderBossBat() {
		super(new ModelBossBat(), 2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return textureLocation;
	}
}
