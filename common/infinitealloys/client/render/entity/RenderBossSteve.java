package infinitealloys.client.render.entity;

import infinitealloys.client.model.entity.ModelBossSteve;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumBoss;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBossSteve extends RenderBiped {

	public RenderBossSteve() {
		super(new ModelBossSteve(), 2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return new ResourceLocation(Consts.TEXTURE_PREFIX + "textures/entity/" + EnumBoss.STEVE.getName()+ ".png");
	}
}
