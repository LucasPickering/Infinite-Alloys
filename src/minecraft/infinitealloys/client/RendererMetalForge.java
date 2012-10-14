package infinitealloys.client;

import infinitealloys.TileEntityMetalForge;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

public class RendererMetalForge extends TileEntitySpecialRenderer {

	private ModelBase model = new ModelBase() {
	};
	private final ModelRenderer[] outerBox = new ModelRenderer[5];
	private final ModelRenderer[] openingTrim = new ModelRenderer[12];

	public RendererMetalForge() {
		model.textureWidth = 128;
		model.textureHeight = 64;

		outerBox[0] = new ModelRenderer(model, 0, 46); // Bottom
		outerBox[0].addBox(0F, 0F, 0F, 16, 2, 16).setRotationPoint(-8F, 22F, -8F);
		outerBox[1] = new ModelRenderer(model, 0, 0); // Left
		outerBox[1].addBox(0F, 0F, 0F, 1, 12, 16).setRotationPoint(-8F, 10F, -8F);
		outerBox[2] = new ModelRenderer(model, 0, 28); // Top
		outerBox[2].addBox(0F, 0F, 0F, 16, 2, 16).setRotationPoint(-8F, 8F, -8F);
		outerBox[3] = new ModelRenderer(model, 66, 0); // Back
		outerBox[3].addBox(0F, 0F, 0F, 14, 12, 1).setRotationPoint(-7F, 10F, 7F);
		outerBox[4] = new ModelRenderer(model, 32, 0); // Right
		outerBox[4].addBox(0F, 0F, 0F, 1, 12, 16).setRotationPoint(7F, 10F, -8F);

		openingTrim[0] = new ModelRenderer(model, 64, 28); // Top strip
		openingTrim[0].addBox(0F, 0F, 0F, 14, 1, 1).setRotationPoint(-7F, 10F, -8F);
		openingTrim[1] = new ModelRenderer(model, 64, 32); // Left strip
		openingTrim[1].addBox(0F, 0F, 0F, 1, 10, 1).setRotationPoint(-7F, 11F, -8F);
		openingTrim[2] = new ModelRenderer(model, 64, 30); // Bottom strip
		openingTrim[2].addBox(0F, 0F, 0F, 14, 1, 1).setRotationPoint(-7F, 21F, -8F);
		openingTrim[3] = new ModelRenderer(model, 66, 32); // Right strip
		openingTrim[3].addBox(0F, 0F, 0F, 1, 10, 1).setRotationPoint(6F, 11F, -8F);
		openingTrim[4] = new ModelRenderer(model, 66, 13); // Bottom right 1
		openingTrim[4].addBox(0F, 0F, 0F, 1, 2, 1).setRotationPoint(5F, 19F, -8F);
		openingTrim[5] = new ModelRenderer(model, 70, 13); // Bottom right 2
		openingTrim[5].addBox(0F, 0F, 0F, 1, 1, 1).setRotationPoint(4F, 20F, -8F);
		openingTrim[6] = new ModelRenderer(model, 66, 16); // Bottom left 1
		openingTrim[6].addBox(0F, 0F, 0F, 1, 2, 1).setRotationPoint(-6F, 19F, -8F);
		openingTrim[7] = new ModelRenderer(model, 70, 15); // Bottom left 2
		openingTrim[7].addBox(0F, 0F, 0F, 1, 1, 1).setRotationPoint(-5F, 20F, -8F);
		openingTrim[8] = new ModelRenderer(model, 66, 19); // Top left 1
		openingTrim[8].addBox(0F, 0F, 0F, 1, 2, 1).setRotationPoint(-6F, 11F, -8F);
		openingTrim[9] = new ModelRenderer(model, 70, 17); // Top left 2
		openingTrim[9].addBox(0F, 0F, 0F, 1, 1, 1).setRotationPoint(-5F, 11F, -8F);
		openingTrim[10] = new ModelRenderer(model, 66, 22); // Top right 1
		openingTrim[10].addBox(0F, 0F, 0F, 1, 2, 1).setRotationPoint(5F, 11F, -8F);
		openingTrim[11] = new ModelRenderer(model, 70, 19); // Top right 2
		openingTrim[11].addBox(0F, 0F, 0F, 1, 1, 1).setRotationPoint(4F, 11F, -8F);
	}

	public void render(TileEntityMetalForge temf, double x, double y, double z, float partialTick) {
		bindTextureByName("/infinitealloys/gfx/metalforge.png");
		GL11.glPushMatrix();
		GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glRotatef((2 - temf.orientation) * -90, 0.0F, 1.0F, 0.0F);
		for(ModelRenderer shape : outerBox)
			shape.render(0.0625F);
		for(ModelRenderer shape : openingTrim)
			shape.render(0.0625F);
		GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
		render((TileEntityMetalForge)te, x, y, z, partialTick);
	}
}
