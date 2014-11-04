package infinitealloys.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEnergyStorage extends ModelBase {

	ModelRenderer base;
	ModelRenderer frontWall;
	ModelRenderer backWall;
	ModelRenderer leftWall;
	ModelRenderer rightWall;
	ModelRenderer tower;
	ModelRenderer dish;

	public ModelEnergyStorage() {
		textureWidth = 128;
		textureHeight = 64;

		base = new ModelRenderer(this, 0, 0);
		base.addBox(0F, 0F, 0F, 16, 1, 16);
		base.setRotationPoint(-8F, 23F, -8F);
		base.setTextureSize(128, 64);
		base.mirror = true;

		frontWall = new ModelRenderer(this, 65, 0);
		frontWall.addBox(0F, 0F, 0F, 16, 3, 1);
		frontWall.setRotationPoint(-8F, 20F, -8F);
		frontWall.setTextureSize(128, 64);
		frontWall.mirror = true;

		backWall = new ModelRenderer(this, 65, 5);
		backWall.addBox(0F, 0F, 0F, 16, 3, 1);
		backWall.setRotationPoint(-8F, 20F, 7F);
		backWall.setTextureSize(128, 64);
		backWall.mirror = true;

		leftWall = new ModelRenderer(this, 0, 36);
		leftWall.addBox(0F, 0F, 0F, 1, 3, 14);
		leftWall.setRotationPoint(-8F, 20F, -7F);
		leftWall.setTextureSize(128, 64);
		leftWall.mirror = true;

		rightWall = new ModelRenderer(this, 0, 18);
		rightWall.addBox(0F, 0F, 0F, 1, 3, 14);
		rightWall.setRotationPoint(7F, 20F, -7F);
		rightWall.setTextureSize(128, 64);
		rightWall.mirror = true;

		tower = new ModelRenderer(this, 31, 18);
		tower.addBox(0F, 0F, 0F, 2, 12, 2);
		tower.setRotationPoint(-1F, 11F, -1F);
		tower.setTextureSize(128, 64);
		tower.mirror = true;

		dish = new ModelRenderer(this, 40, 18);
		dish.addBox(-4F, -2F, -2F, 8, 4, 1);
		dish.setRotationPoint(0F, 11F, 0F);
		dish.setTextureSize(128, 64);
		dish.mirror = true;
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		base.render(f5);
		frontWall.render(f5);
		backWall.render(f5);
		leftWall.render(f5);
		rightWall.render(f5);
		tower.render(f5);
		dish.render(f5);
	}
}
