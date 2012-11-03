package infinitealloys.client;

import infinitealloys.References;
import infinitealloys.TileEntityComputer;
import org.lwjgl.opengl.GL11;
import com.overminddl1.minecraft.libs.NMT.NMTModelRenderer;
import net.minecraft.src.ModelBase;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

public class RendererComputer extends TileEntitySpecialRenderer {

	private ModelBase model = new ModelBase() {};
	private NMTModelRenderer modelRenderer;

	public RendererComputer() {
		modelRenderer = new NMTModelRenderer(model);
		modelRenderer.addModelOBJ(getClass().getResource(References.OBJ_PATH + "computer.obj").toString());
	}

	public void render(TileEntityComputer tec, double x, double y, double z, float partialTick) {
		bindTextureByName(References.TEXTURE_PATH + "computer.png");
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
		GL11.glRotatef(tec.orientation * 90, 0F, 1F, 0F);
		modelRenderer.render(0.5F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
		render((TileEntityComputer)te, x, y, z, partialTick);
	}
}
