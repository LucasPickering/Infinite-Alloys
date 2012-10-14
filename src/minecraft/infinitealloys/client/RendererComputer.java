package infinitealloys.client;

import infinitealloys.TileEntityComputer;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

public class RendererComputer extends TileEntitySpecialRenderer {

	private ModelBase model = new ModelBase() {
	};
	private final ModelRenderer[] monitorStand = new ModelRenderer[5];
	private final ModelRenderer monitorScreen;
	private final ModelRenderer keyboard;
	private final ModelRenderer mouse;

	public RendererComputer() {
		model.textureWidth = 64;
		model.textureHeight = 32;

		monitorStand[0] = new ModelRenderer(model, 16, 10);
		monitorStand[0].addBox(0F, 0F, 0F, 2, 1, 6).setRotationPoint(-1F, 23F, 1F);
		monitorStand[1] = new ModelRenderer(model, 8, 13);
		monitorStand[1].addBox(0F, 0F, 0F, 2, 1, 2).setRotationPoint(-3F, 23F, 3F);
		monitorStand[2] = new ModelRenderer(model, 8, 10);
		monitorStand[2].addBox(0F, 0F, 0F, 2, 1, 2).setRotationPoint(1F, 23F, 3F);
		monitorStand[3] = new ModelRenderer(model, 0, 10);
		monitorStand[3].addBox(0F, 0F, 0F, 2, 5, 2).setRotationPoint(-1F, 18F, 3F);
		monitorStand[4] = new ModelRenderer(model, 26, 0);
		monitorStand[4].addBox(0F, 0F, 0F, 2, 2, 1).setRotationPoint(-1F, 18F, 2F);

		monitorScreen = new ModelRenderer(model, 0, 0);
		monitorScreen.addBox(0F, 0F, 0F, 12, 9, 1).setRotationPoint(-6F, 12F, 1F);

		keyboard = new ModelRenderer(model, 0, 17);
		keyboard.addBox(0F, 0F, 0F, 11, 1, 5).setRotationPoint(-7F, 23F, -7F);
		keyboard.rotateAngleX = (float)(Math.PI) / 12;

		mouse = new ModelRenderer(model, 26, 3);
		mouse.addBox(0F, 0F, 0F, 2, 1, 3).setRotationPoint(5F, 23F, -6F);
	}

	public void render(TileEntityComputer tec, double x, double y, double z, float partialTick) {
		bindTextureByName("/infinitealloys/gfx/computer.png");
		GL11.glPushMatrix();
		GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glRotatef((2 - tec.orientation) * -90, 0, 1, 0);
		for(ModelRenderer shape : monitorStand)
			shape.render(0.0625F);
		monitorScreen.render(0.0625F);
		keyboard.render(0.0625F);
		mouse.render(0.0625F);
		GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
		render((TileEntityComputer)te, x, y, z, partialTick);
	}
}
