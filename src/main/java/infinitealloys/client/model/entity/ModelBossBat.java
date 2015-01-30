package infinitealloys.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

public class ModelBossBat extends ModelBase {

  private final ModelRenderer batHead;
  /**
   * The body box of the bat model.
   */
  private final ModelRenderer batBody;
  /**
   * The inner right wing box of the bat model.
   */
  private final ModelRenderer batRightWing;
  /**
   * The inner left wing box of the bat model.
   */
  private final ModelRenderer batLeftWing;
  /**
   * The outer right wing box of the bat model.
   */
  private final ModelRenderer batOuterRightWing;
  /**
   * The outer left wing box of the bat model.
   */
  private final ModelRenderer batOuterLeftWing;

  public ModelBossBat() {
    textureWidth = 64;
    textureHeight = 64;
    batHead = new ModelRenderer(this, 0, 0);
    batHead.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
    ModelRenderer modelrenderer = new ModelRenderer(this, 24, 0);
    modelrenderer.addBox(-4.0F, -6.0F, -2.0F, 3, 4, 1);
    batHead.addChild(modelrenderer);
    ModelRenderer modelrenderer1 = new ModelRenderer(this, 24, 0);
    modelrenderer1.mirror = true;
    modelrenderer1.addBox(1.0F, -6.0F, -2.0F, 3, 4, 1);
    batHead.addChild(modelrenderer1);
    batBody = new ModelRenderer(this, 0, 16);
    batBody.addBox(-3.0F, 4.0F, -3.0F, 6, 12, 6);
    batBody.setTextureOffset(0, 34).addBox(-5.0F, 16.0F, 0.0F, 10, 6, 1);
    batRightWing = new ModelRenderer(this, 42, 0);
    batRightWing.addBox(-12.0F, 1.0F, 1.5F, 10, 16, 1);
    batOuterRightWing = new ModelRenderer(this, 24, 16);
    batOuterRightWing.setRotationPoint(-12.0F, 1.0F, 1.5F);
    batOuterRightWing.addBox(-8.0F, 1.0F, 0.0F, 8, 12, 1);
    batLeftWing = new ModelRenderer(this, 42, 0);
    batLeftWing.mirror = true;
    batLeftWing.addBox(2.0F, 1.0F, 1.5F, 10, 16, 1);
    batOuterLeftWing = new ModelRenderer(this, 24, 16);
    batOuterLeftWing.mirror = true;
    batOuterLeftWing.setRotationPoint(12.0F, 1.0F, 1.5F);
    batOuterLeftWing.addBox(0.0F, 1.0F, 0.0F, 8, 12, 1);
    batBody.addChild(batRightWing);
    batBody.addChild(batLeftWing);
    batRightWing.addChild(batOuterRightWing);
    batLeftWing.addChild(batOuterLeftWing);
  }

  @Override
  public void render(Entity entity, float par2, float par3, float par4, float par5, float par6,
                     float par7) {
    GL11.glPushMatrix();
    GL11.glScalef(4F, 4F, 4F);
    GL11.glTranslatef(0F, -0.35F, -0.2F);

    batHead.rotateAngleX = par6 / (180F / (float) Math.PI);
    batHead.rotateAngleY = par5 / (180F / (float) Math.PI);
    batHead.rotateAngleZ = 0.0F;
    batHead.setRotationPoint(0.0F, 0.0F, 0.0F);
    batRightWing.setRotationPoint(0.0F, 0.0F, 0.0F);
    batLeftWing.setRotationPoint(0.0F, 0.0F, 0.0F);
    batBody.rotateAngleX = (float) Math.PI / 4F + MathHelper.cos(par4 * 0.1F) * 0.15F;
    batBody.rotateAngleY = 0.0F;
    batRightWing.rotateAngleY = MathHelper.cos(par4 * 1.3F) * (float) Math.PI * 0.25F;
    batLeftWing.rotateAngleY = -batRightWing.rotateAngleY;
    batOuterRightWing.rotateAngleY = batRightWing.rotateAngleY * 0.5F;
    batOuterLeftWing.rotateAngleY = -batRightWing.rotateAngleY * 0.5F;

    batHead.render(par7);
    batBody.render(par7);

    GL11.glPopMatrix();
  }
}