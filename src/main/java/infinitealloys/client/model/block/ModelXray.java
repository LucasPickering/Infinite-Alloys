package infinitealloys.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelXray extends ModelBase {

  ModelRenderer post1;
  ModelRenderer post2;
  ModelRenderer post3;
  ModelRenderer post4;
  ModelRenderer top;
  ModelRenderer telescope1;
  ModelRenderer telescope2;
  ModelRenderer telescope3;

  public ModelXray() {
    textureWidth = 64;
    textureHeight = 32;

    post1 = new ModelRenderer(this, 0, 0);
    post1.addBox(0F, 0F, 0F, 1, 7, 1);
    post1.setRotationPoint(-8F, 17F, -8F);
    post1.setTextureSize(64, 32);
    post1.mirror = true;

    post2 = new ModelRenderer(this, 0, 0);
    post2.addBox(0F, 0F, 0F, 1, 7, 1);
    post2.setRotationPoint(7F, 17F, -8F);
    post2.setTextureSize(64, 32);
    post2.mirror = true;

    post3 = new ModelRenderer(this, 0, 0);
    post3.addBox(0F, 0F, 0F, 1, 7, 1);
    post3.setRotationPoint(7F, 17F, 7F);
    post3.setTextureSize(64, 32);
    post3.mirror = true;

    post4 = new ModelRenderer(this, 0, 0);
    post4.addBox(0F, 0F, 0F, 1, 7, 1);
    post4.setRotationPoint(-8F, 17F, 7F);
    post4.setTextureSize(64, 32);
    post4.mirror = true;

    top = new ModelRenderer(this, 0, 0);
    top.addBox(0F, 0F, 0F, 16, 1, 16);
    top.setRotationPoint(-8F, 16F, -8F);
    top.setTextureSize(64, 32);
    top.mirror = true;

    telescope1 = new ModelRenderer(this, 0, 18);
    telescope1.addBox(0F, 0F, 0F, 2, 2, 2);
    telescope1.setRotationPoint(-1F, 17F, -1F);
    telescope1.setTextureSize(64, 32);
    telescope1.mirror = true;

    telescope2 = new ModelRenderer(this, 13, 18);
    telescope2.addBox(0F, 0F, 0F, 4, 2, 4);
    telescope2.setRotationPoint(-2F, 19F, -2F);
    telescope2.setTextureSize(64, 32);
    telescope2.mirror = true;

    telescope3 = new ModelRenderer(this, 30, 18);
    telescope3.addBox(0F, 0F, 0F, 6, 3, 6);
    telescope3.setRotationPoint(-3F, 21F, -3F);
    telescope3.setTextureSize(64, 32);
    telescope3.mirror = true;
  }

  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    post1.render(f5);
    post2.render(f5);
    post3.render(f5);
    post4.render(f5);
    top.render(f5);
    telescope1.render(f5);
    telescope2.render(f5);
    telescope3.render(f5);
  }
}
