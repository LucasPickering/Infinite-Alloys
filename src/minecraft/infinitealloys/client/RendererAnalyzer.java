package infinitealloys.client;

import infinitealloys.TileEntityAnalyzer;
import org.lwjgl.opengl.GL11;
import com.overminddl1.minecraft.libs.NMT.NMTModelRenderer;
import com.overminddl1.minecraft.libs.NMT.NMTModelSphere;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModelBase;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

public class RendererAnalyzer extends TileEntitySpecialRenderer {

	private ModelBase model = new ModelBase() {};
	private NMTModelRenderer staticModelRenderer;
	private NMTModelRenderer animModelRenderer;

	public RendererAnalyzer() {
		staticModelRenderer = new NMTModelRenderer(model);
		staticModelRenderer.addModelOBJ("file:///E:/Files/github/Infinite-Alloys/src/common/infinitealloys/gfx/analyzer.obj");
		animModelRenderer = new NMTModelRenderer(model);
		double pi = Math.PI;
		// Adds spheres at 0, 120, and 240 degrees
		animModelRenderer.addModel(new NMTModelSphere(animModelRenderer, (float)(Math.cos(0)) / 1.5F, (float)(Math.sin(0)) / 1.5F, 0F, 0.25F, 20, 20, 1, 1));
		animModelRenderer.addModel(new NMTModelSphere(animModelRenderer, (float)(Math.cos(2 * pi / 3)) / 1.5F, (float)(Math.sin(2 * pi / 3)) / 1.5F, 0F, 0.25F, 20, 20, 1, 1));
		animModelRenderer.addModel(new NMTModelSphere(animModelRenderer, (float)(Math.cos(4 * pi / 3)) / 1.5F, (float)(Math.sin(4 * pi / 3)) / 1.5F, 0F, 0.25F, 20, 20, 1, 1));
	}

	public void render(TileEntityAnalyzer tea, double x, double y, double z, float partialTick) {
		bindTextureByName("/infinitealloys/gfx/replace.png");
		GL11.glPushMatrix();
		GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
		GL11.glRotatef(90, 1F, 0F, 0F);
		GL11.glRotatef((1 - tea.orientation) * -90, 0F, 0F, 1F);
		staticModelRenderer.render(0.5F);
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.075F, 0F, 0F);
		GL11.glTranslatef(0F, 0F, (float)(tea.ticksSinceStart <= 60 ? tea.ticksSinceStart : 60) / -180F);
		GL11.glTranslatef(0F, 0F, (float)(tea.ticksSinceStart > 60 ? MathHelper.sin(tea.ticksSinceStart / 5F) / 20F : 0));
		GL11.glRotatef((tea.ticksSinceStart > 60 ? tea.ticksSinceStart : 0) * 10F, 0F, 0F, 1F);
		animModelRenderer.render(0.5F);
		GL11.glPopMatrix();
		GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
		render((TileEntityAnalyzer)te, x, y, z, partialTick);
	}
}
