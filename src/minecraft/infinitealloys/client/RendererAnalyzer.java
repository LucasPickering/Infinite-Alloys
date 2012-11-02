package infinitealloys.client;

import java.io.File;
import java.net.MalformedURLException;
import infinitealloys.References;
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
		staticModelRenderer.addModelOBJ(getClass().getResource("obj/analyzer.obj").toString());
		animModelRenderer = new NMTModelRenderer(model);
		double pi = Math.PI;
		// Adds spheres at 45, 135, 225, and 315 degrees
		animModelRenderer.addModel(new NMTModelSphere(animModelRenderer, (float)(Math.cos(pi / 4)) / 1.5F, (float)(Math.sin(pi / 4)) / 1.5F, 0F, 0.25F, 20, 20, 1, 1));
		animModelRenderer.addModel(new NMTModelSphere(animModelRenderer, (float)(Math.cos(3 * pi / 4)) / 1.5F, (float)(Math.sin(3 * pi / 4)) / 1.5F, 0F, 0.25F, 20, 20, 1, 1));
		animModelRenderer.addModel(new NMTModelSphere(animModelRenderer, (float)(Math.cos(5 * pi / 4)) / 1.5F, (float)(Math.sin(5 * pi / 4)) / 1.5F, 0F, 0.25F, 20, 20, 1, 1));
		animModelRenderer.addModel(new NMTModelSphere(animModelRenderer, (float)(Math.cos(7 * pi / 4)) / 1.5F, (float)(Math.sin(7 * pi / 4)) / 1.5F, 0F, 0.25F, 20, 20, 1, 1));
	}

	public void render(TileEntityAnalyzer tea, double x, double y, double z, float partialTick) {
		bindTextureByName(References.TEXTURE_PATH + "replace.png");
		GL11.glPushMatrix();
		GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
		GL11.glRotatef(90, 1F, 0F, 0F);
		staticModelRenderer.render(0.5F);
		GL11.glPushMatrix();
		GL11.glTranslatef(0F, 0F, (float)(tea.ticksSinceStart <= 90 ? tea.ticksSinceStart : 90) / -270F);
		GL11.glTranslatef(0F, 0F, (float)(tea.ticksSinceStart > 90 ? MathHelper.sin(tea.ticksSinceStart / 5F) / 20F : 0));
		GL11.glRotatef((tea.ticksSinceStart > 90 ? tea.ticksSinceStart : 0) * 10F, 0F, 0F, 1F);
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
