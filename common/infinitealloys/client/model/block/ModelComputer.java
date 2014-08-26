package infinitealloys.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelComputer extends ModelBase {

	ModelRenderer mouse;
	ModelRenderer keyboard;
	ModelRenderer monitorBase1;
	ModelRenderer monitorBase2;
	ModelRenderer monitorBack1;
	ModelRenderer monitorBack2;
	ModelRenderer screen;

	public ModelComputer() {
		mouse = new ModelRenderer(this, 0, 14);
		mouse.addBox(0F, 0F, 0F, 2, 1, 3);
		mouse.setRotationPoint(5F, 23F, -5F);
		mouse.setTextureSize(64, 32);
		mouse.mirror = true;

		keyboard = new ModelRenderer(this, 0, 9);
		keyboard.addBox(0F, 0F, 0F, 10, 1, 4);
		keyboard.setRotationPoint(-7F, 23F, -5F);
		keyboard.setTextureSize(64, 32);
		keyboard.mirror = true;
		setRotation(keyboard, 0.2617994F, 0F, 0F);

		monitorBase1 = new ModelRenderer(this, 22, 0);
		monitorBase1.addBox(0F, 0F, 0F, 2, 1, 6);
		monitorBase1.setRotationPoint(-1F, 23F, 1F);
		monitorBase1.setTextureSize(64, 32);
		monitorBase1.mirror = true;

		monitorBase2 = new ModelRenderer(this, 38, 0);
		monitorBase2.addBox(0F, 0F, 0F, 6, 1, 2);
		monitorBase2.setRotationPoint(-3F, 23F, 3F);
		monitorBase2.setTextureSize(64, 32);
		monitorBase2.mirror = true;

		monitorBack1 = new ModelRenderer(this, 38, 3);
		monitorBack1.addBox(0F, 0F, 0F, 2, 4, 2);
		monitorBack1.setRotationPoint(-1F, 19F, 3F);
		monitorBack1.setTextureSize(64, 32);
		monitorBack1.mirror = true;

		monitorBack2 = new ModelRenderer(this, 50, 0);
		monitorBack2.addBox(0F, 0F, 0F, 2, 2, 1);
		monitorBack2.setRotationPoint(-1F, 19F, 2F);
		monitorBack2.setTextureSize(64, 32);
		monitorBack2.mirror = true;

		screen = new ModelRenderer(this, 0, 0);
		screen.addBox(0F, 0F, 0F, 10, 8, 1);
		screen.setRotationPoint(-5F, 14F, 1F);
		screen.setTextureSize(64, 32);
		screen.mirror = true;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		mouse.render(f5);
		keyboard.render(f5);
		monitorBase1.render(f5);
		monitorBase2.render(f5);
		monitorBack1.render(f5);
		monitorBack2.render(f5);
		screen.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
