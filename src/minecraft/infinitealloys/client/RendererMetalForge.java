package infinitealloys.client;

import infinitealloys.References;
import infinitealloys.TileEntityMetalForge;
import net.minecraft.src.ModelBase;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import com.overminddl1.minecraft.libs.NMT.NMTModelBox;
import com.overminddl1.minecraft.libs.NMT.NMTModelRenderer;

public class RendererMetalForge extends TileEntitySpecialRenderer {

	private ModelBase model = new ModelBase() {};
	private NMTModelRenderer staticModelRenderer;
	private NMTModelRenderer animModelRenderer;

	public RendererMetalForge() {
		staticModelRenderer = new NMTModelRenderer(model);
		staticModelRenderer.addModelOBJ(getClass().getResource(References.OBJ_PATH + "metalforge.obj").toString());
		animModelRenderer = new NMTModelRenderer(model);
		animModelRenderer.addModel(new NMTModelBox(animModelRenderer, -1.4375F, -6F, -4.4375F, 1, 12, 11, 0F, 0.125F, false));
	}

	public void render(TileEntityMetalForge temf, double x, double y, double z, float partialTick) {
		bindTextureByName(References.TEXTURE_PATH + "tex.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
		GL11.glRotatef((temf.orientation - 1) * -90, 0F, 0F, 1F);
		staticModelRenderer.render(0.5F);
		/*GL11.glPushMatrix();
		GL11.glRotatef(temf.doorAngle, 0F, 0F, 1F);
		GL11.glTranslatef(-0.12F, (float)temf.doorAngle / 120F, 0F);
		animModelRenderer.render(0.5F);
		GL11.glPopMatrix();*/
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
		render((TileEntityMetalForge)te, x, y, z, partialTick);
	}
}
