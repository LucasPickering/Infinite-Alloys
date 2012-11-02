package infinitealloys.client;

import infinitealloys.CommonProxy;
import infinitealloys.References;
import infinitealloys.TileEntityAnalyzer;
import infinitealloys.TileEntityComputer;
import infinitealloys.TileEntityMetalForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy implements ISimpleBlockRenderingHandler {

	public static int renderId;
	private TileEntityComputer tec = new TileEntityComputer(0);
	private TileEntityMetalForge temf = new TileEntityMetalForge(0);
	private TileEntityAnalyzer tea = new TileEntityAnalyzer(0);

	@Override
	public void initTileEntities() {
		super.initTileEntities();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityComputer.class, new RendererComputer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMetalForge.class, new RendererMetalForge());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAnalyzer.class, new RendererAnalyzer());
	}

	@Override
	public void initRendering() {
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "tex.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/computer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/metalforge.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/analyzer.png");
		renderId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(renderId, this);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		GL11.glRotatef(-90F, 0F, 1F, 0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		TileEntity te = null;
		switch(metadata) {
			case 0:
				te = tec;
				break;
			case 1:
				te = temf;
				break;
			case 2:
				te = tea;
				break;
		}
		TileEntityRenderer.instance.renderTileEntityAt(te, 0, 0, 0, 0);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderId;
	}
}
