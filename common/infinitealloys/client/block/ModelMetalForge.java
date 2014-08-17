package infinitealloys.client.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMetalForge extends ModelBase {

	ModelRenderer bottom;
	ModelRenderer back;
	ModelRenderer left;
	ModelRenderer right;
	ModelRenderer top;
	ModelRenderer borderBottom;
	ModelRenderer borderTop;
	ModelRenderer borderLeft;
	ModelRenderer borderRight;

	public ModelMetalForge() {
		textureWidth = 128;
		textureHeight = 64;

		bottom = new ModelRenderer(this, 0, 0);
		bottom.addBox(0F, 0F, 0F, 16, 1, 16);
		bottom.setRotationPoint(-8F, 23F, -8F);
		bottom.setTextureSize(128, 64);
		bottom.mirror = true;

		back = new ModelRenderer(this, 64, 17);
		back.addBox(0F, 0F, 0F, 16, 14, 1);
		back.setRotationPoint(-8F, 9F, 7F);
		back.setTextureSize(128, 64);
		back.mirror = true;

		left = new ModelRenderer(this, 0, 17);
		left.addBox(0F, 0F, 0F, 1, 14, 15);
		left.setRotationPoint(-8F, 9F, -8F);
		left.setTextureSize(128, 64);
		left.mirror = true;

		right = new ModelRenderer(this, 32, 17);
		right.addBox(0F, 0F, 0F, 1, 14, 15);
		right.setRotationPoint(7F, 9F, -8F);
		right.setTextureSize(128, 64);
		right.mirror = true;

		top = new ModelRenderer(this, 64, 0);
		top.addBox(0F, 0F, 0F, 16, 1, 16);
		top.setRotationPoint(-8F, 8F, -8F);
		top.setTextureSize(128, 64);
		top.mirror = true;

		borderBottom = new ModelRenderer(this, 12, 49);
		borderBottom.addBox(0F, 0F, 0F, 14, 2, 1);
		borderBottom.setRotationPoint(-7F, 21F, -8F);
		borderBottom.setTextureSize(128, 64);
		borderBottom.mirror = true;

		borderTop = new ModelRenderer(this, 12, 46);
		borderTop.addBox(0F, 0F, 0F, 14, 2, 1);
		borderTop.setRotationPoint(-7F, 9F, -8F);
		borderTop.setTextureSize(128, 64);
		borderTop.mirror = true;

		borderLeft = new ModelRenderer(this, 0, 46);
		borderLeft.addBox(0F, 0F, 0F, 2, 10, 1);
		borderLeft.setRotationPoint(-7F, 11F, -8F);
		borderLeft.setTextureSize(128, 64);
		borderLeft.mirror = true;

		borderRight = new ModelRenderer(this, 6, 46);
		borderRight.addBox(0F, 0F, 0F, 2, 10, 1);
		borderRight.setRotationPoint(5F, 11F, -8F);
		borderRight.setTextureSize(128, 64);
		borderRight.mirror = true;
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		bottom.render(f5);
		back.render(f5);
		left.render(f5);
		right.render(f5);
		top.render(f5);
		borderBottom.render(f5);
		borderTop.render(f5);
		borderLeft.render(f5);
		borderRight.render(f5);
	}
}
