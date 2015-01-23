package infinitealloys.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelPasture extends ModelBase {

  ModelRenderer post1;
  ModelRenderer post2;
  ModelRenderer post3;
  ModelRenderer post4;
  ModelRenderer crossbar1;
  ModelRenderer crossbar6;
  ModelRenderer crossbar5;
  ModelRenderer crossbar2;
  ModelRenderer crossbar3;
  ModelRenderer crossbar4;
  ModelRenderer crossbar7;
  ModelRenderer crossbar8;
  ModelRenderer pole1;
  ModelRenderer pole2;
  ModelRenderer pole3;
  ModelRenderer pole4;
  ModelRenderer antenna;

  public ModelPasture() {
    post1 = new ModelRenderer(this, 0, 0);
    post1.addBox(0F, 0F, 0F, 3, 12, 3);
    post1.setRotationPoint(-8F, 12F, -8F);
    post1.setTextureSize(64, 32);
    post1.mirror = true;

    post2 = new ModelRenderer(this, 0, 0);
    post2.addBox(0F, 0F, 0F, 3, 12, 3);
    post2.setRotationPoint(5F, 12F, 5F);
    post2.setTextureSize(64, 32);
    post2.mirror = true;

    post3 = new ModelRenderer(this, 0, 0);
    post3.addBox(0F, 0F, 0F, 3, 12, 3);
    post3.setRotationPoint(5F, 12F, -8F);
    post3.setTextureSize(64, 32);
    post3.mirror = true;

    post4 = new ModelRenderer(this, 0, 0);
    post4.addBox(0F, 0F, 0F, 3, 12, 3);
    post4.setRotationPoint(-8F, 12F, 5F);
    post4.setTextureSize(64, 32);
    post4.mirror = true;

    crossbar1 = new ModelRenderer(this, 34, 0);
    crossbar1.addBox(0F, 0F, 0F, 10, 2, 1);
    crossbar1.setRotationPoint(-5F, 14F, -7F);
    crossbar1.setTextureSize(64, 32);
    crossbar1.mirror = true;

    crossbar6 = new ModelRenderer(this, 34, 0);
    crossbar6.addBox(0F, 0F, 0F, 10, 2, 1);
    crossbar6.setRotationPoint(-5F, 19F, 6F);
    crossbar6.setTextureSize(64, 32);
    crossbar6.mirror = true;

    crossbar5 = new ModelRenderer(this, 34, 0);
    crossbar5.addBox(0F, 0F, 0F, 10, 2, 1);
    crossbar5.setRotationPoint(-5F, 14F, 6F);
    crossbar5.setTextureSize(64, 32);
    crossbar5.mirror = true;

    crossbar2 = new ModelRenderer(this, 34, 0);
    crossbar2.addBox(0F, 0F, 0F, 10, 2, 1);
    crossbar2.setRotationPoint(-5F, 19F, -7F);
    crossbar2.setTextureSize(64, 32);
    crossbar2.mirror = true;

    crossbar3 = new ModelRenderer(this, 12, 0);
    crossbar3.addBox(0F, 0F, 0F, 1, 2, 10);
    crossbar3.setRotationPoint(6F, 14F, -5F);
    crossbar3.setTextureSize(64, 32);
    crossbar3.mirror = true;

    crossbar4 = new ModelRenderer(this, 12, 0);
    crossbar4.addBox(0F, 0F, 0F, 1, 2, 10);
    crossbar4.setRotationPoint(6F, 19F, -5F);
    crossbar4.setTextureSize(64, 32);
    crossbar4.mirror = true;

    crossbar7 = new ModelRenderer(this, 12, 0);
    crossbar7.addBox(0F, 0F, 0F, 1, 2, 10);
    crossbar7.setRotationPoint(-7F, 14F, -5F);
    crossbar7.setTextureSize(64, 32);
    crossbar7.mirror = true;

    crossbar8 = new ModelRenderer(this, 12, 0);
    crossbar8.addBox(0F, 0F, 0F, 1, 2, 10);
    crossbar8.setRotationPoint(-7F, 19F, -5F);
    crossbar8.setTextureSize(64, 32);
    crossbar8.mirror = true;

    pole1 = new ModelRenderer(this, 0, 15);
    pole1.addBox(0F, 0F, 0F, 1, 14, 1);
    pole1.setRotationPoint(0F, 10F, 0F);
    pole1.setTextureSize(64, 32);
    pole1.mirror = true;
    setRotation(pole1, 0.122173F, 0F, -0.122173F);

    pole2 = new ModelRenderer(this, 0, 15);
    pole2.addBox(0F, 0F, 0F, 1, 14, 1);
    pole2.setRotationPoint(-1F, 10F, 0F);
    pole2.setTextureSize(64, 32);
    pole2.mirror = true;
    setRotation(pole2, 0.122173F, 0F, 0.122173F);

    pole3 = new ModelRenderer(this, 0, 15);
    pole3.addBox(0F, 0F, 0F, 1, 14, 1);
    pole3.setRotationPoint(0F, 10F, -1F);
    pole3.setTextureSize(64, 32);
    pole3.mirror = true;
    setRotation(pole3, -0.122173F, 0F, -0.122173F);

    pole4 = new ModelRenderer(this, 0, 15);
    pole4.addBox(0F, 0F, 0F, 1, 14, 1);
    pole4.setRotationPoint(-1F, 10F, -1F);
    pole4.setTextureSize(64, 32);
    pole4.mirror = true;
    setRotation(pole4, -0.122173F, 0F, 0.122173F);

    antenna = new ModelRenderer(this, 4, 15);
    antenna.addBox(0F, 0F, 0F, 1, 3, 1);
    antenna.setRotationPoint(-0.5F, 7F, -0.5F);
    antenna.setTextureSize(64, 32);
    antenna.mirror = true;
    setRotation(antenna, 0F, 0F, 0F);
  }

  @Override
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    post1.render(f5);
    post2.render(f5);
    post3.render(f5);
    post4.render(f5);
    crossbar1.render(f5);
    crossbar6.render(f5);
    crossbar5.render(f5);
    crossbar2.render(f5);
    crossbar3.render(f5);
    crossbar4.render(f5);
    crossbar7.render(f5);
    crossbar8.render(f5);
    pole1.render(f5);
    pole2.render(f5);
    pole3.render(f5);
    pole4.render(f5);
    antenna.render(f5);
  }

  private void setRotation(ModelRenderer model, float x, float y, float z) {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
}
