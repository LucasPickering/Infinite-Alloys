package infinitealloys.handlers;

import java.util.ArrayList;
import infinitealloys.client.GuiAnalyzer;
import infinitealloys.client.GuiComputer;
import infinitealloys.client.GuiMetalForge;
import infinitealloys.client.GuiPrinter;
import infinitealloys.client.GuiXray;
import infinitealloys.inventory.ContainerAnalyzer;
import infinitealloys.inventory.ContainerMachine;
import infinitealloys.inventory.ContainerMetalForge;
import infinitealloys.inventory.ContainerPrinter;
import infinitealloys.inventory.ContainerXray;
import infinitealloys.tile.TileEntityAnalyzer;
import infinitealloys.tile.TileEntityComputer;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.tile.TileEntityMetalForge;
import infinitealloys.tile.TileEntityPrinter;
import infinitealloys.tile.TileEntityXray;
import infinitealloys.util.Point;
import infinitealloys.util.References;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GfxHandler implements IGuiHandler, ISimpleBlockRenderingHandler {

	public int renderID;

	@SideOnly(Side.CLIENT)
	public ArrayList<Point> xrayBlocks = new ArrayList<Point>();

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		switch(id) {
			case 0:
				return new ContainerMachine(player.inventory, (TileEntityMachine)tileEntity);
			case 1:
				return new ContainerMetalForge(player.inventory, (TileEntityMetalForge)tileEntity);
			case 2:
				return new ContainerAnalyzer(player.inventory, (TileEntityAnalyzer)tileEntity);
			case 3:
				return new ContainerPrinter(player.inventory, (TileEntityPrinter)tileEntity);
			case 4:
				return new ContainerXray(player.inventory, (TileEntityXray)tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		switch(id) {
			case 0:
				return new GuiComputer(player.inventory, (TileEntityComputer)tileEntity);
			case 1:
				return new GuiMetalForge(player.inventory, (TileEntityMetalForge)tileEntity);
			case 2:
				return new GuiAnalyzer(player.inventory, (TileEntityAnalyzer)tileEntity);
			case 3:
				return new GuiPrinter(player.inventory, (TileEntityPrinter)tileEntity);
			case 4:
				return new GuiXray(player.inventory, (TileEntityXray)tileEntity);
		}
		return null;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		block.setBlockBoundsForItemRender();
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderBottomFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(0, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderTopFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(1, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderEastFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(2, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderWestFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(3, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderNorthFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(4, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderSouthFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(5, metadata));
		tessellator.draw();

		if(metadata < References.METAL_COUNT) {
			int mult = References.metalColors[metadata];
			GL11.glColor4f((float)(mult >> 16 & 255) / 255F, (float)(mult >> 8 & 255) / 255F, (float)(mult & 255) / 255F, 1F);

			tessellator.startDrawingQuads();
			tessellator.setNormal(0F, -1F, 0F);
			renderer.renderBottomFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(0, References.METAL_COUNT));
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(0F, 1F, 0F);
			renderer.renderTopFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(1, References.METAL_COUNT));
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(0F, 0F, -1F);
			renderer.renderEastFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(2, References.METAL_COUNT));
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(0F, 0F, 1F);
			renderer.renderWestFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(3, References.METAL_COUNT));
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(-1F, 0F, 0F);
			renderer.renderNorthFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(4, References.METAL_COUNT));
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(1F, 0F, 0F);
			renderer.renderSouthFace(block, 0D, 0D, 0D, block.getBlockTextureFromSideAndMetadata(5, References.METAL_COUNT));
			tessellator.draw();
		}
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		Tessellator tess = Tessellator.instance;
		int mult = References.metalColors[world.getBlockMetadata(x, y, z)];
		boolean rendered = renderer.renderStandardBlock(block, x, y, z);
		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
		float var10 = 0.5F;
		float var11 = 1.0F;
		float var12 = 0.8F;
		float var13 = 0.6F;
		float var14 = var11 * (float)(mult >> 16 & 255) / 255F;
		float var15 = var11 * (float)(mult >> 8 & 255) / 255F;
		float var16 = var11 * (float)(mult & 255) / 255F;
		float var17 = var10;
		float var18 = var12;
		float var19 = var13;
		float var20 = var10;
		float var21 = var12;
		float var22 = var13;
		float var23 = var10;
		float var24 = var12;
		float var25 = var13;

		var17 = var10 * (float)(mult >> 16 & 255) / 255F;
		var18 = var12 * (float)(mult >> 16 & 255) / 255F;
		var19 = var13 * (float)(mult >> 16 & 255) / 255F;
		var20 = var10 * (float)(mult >> 8 & 255) / 255F;
		var21 = var12 * (float)(mult >> 8 & 255) / 255F;
		var22 = var13 * (float)(mult >> 8 & 255) / 255F;
		var23 = var10 * (float)(mult & 255) / 255F;
		var24 = var12 * (float)(mult & 255) / 255F;
		var25 = var13 * (float)(mult & 255) / 255F;
		if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0)) {
			tess.setBrightness(renderer.renderMinY > 0D ? brightness : block.getMixedBrightnessForBlock(world, x, y - 1, z));
			tess.setColorOpaque_F(var17, var20, var23);
			renderer.renderBottomFace(block, (double)x, (double)y, (double)z, block.getBlockTextureFromSideAndMetadata(0, References.METAL_COUNT));
			rendered = true;
		}
		if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
			tess.setBrightness(renderer.renderMaxY < 1D ? brightness : block.getMixedBrightnessForBlock(world, x, y + 1, z));
			tess.setColorOpaque_F(var14, var15, var16);
			renderer.renderTopFace(block, (double)x, (double)y, (double)z, block.getBlockTextureFromSideAndMetadata(1, References.METAL_COUNT));
			rendered = true;
		}
		if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 2)) {
			tess.setBrightness(renderer.renderMinZ > 0D ? brightness : block.getMixedBrightnessForBlock(world, x, y, z - 1));
			tess.setColorOpaque_F(var18, var21, var24);
			renderer.renderEastFace(block, (double)x, (double)y, (double)z, block.getBlockTextureFromSideAndMetadata(2, References.METAL_COUNT));
			rendered = true;
		}
		if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 3)) {
			tess.setBrightness(renderer.renderMaxZ < 1D ? brightness : block.getMixedBrightnessForBlock(world, x, y, z + 1));
			tess.setColorOpaque_F(var18, var21, var24);
			renderer.renderWestFace(block, (double)x, (double)y, (double)z, block.getBlockTextureFromSideAndMetadata(3, References.METAL_COUNT));
			rendered = true;
		}
		if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 4)) {
			tess.setBrightness(renderer.renderMinX > 0D ? brightness : block.getMixedBrightnessForBlock(world, x - 1, y, z));
			tess.setColorOpaque_F(var19, var22, var25);
			renderer.renderNorthFace(block, (double)x, (double)y, (double)z, block.getBlockTextureFromSideAndMetadata(4, References.METAL_COUNT));
			rendered = true;
		}
		if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 5)) {
			tess.setBrightness(renderer.renderMaxX < 1D ? brightness : block.getMixedBrightnessForBlock(world, x + 1, y, z));
			tess.setColorOpaque_F(var19, var22, var25);
			renderer.renderSouthFace(block, (double)x, (double)y, (double)z, block.getBlockTextureFromSideAndMetadata(5, References.METAL_COUNT));
			rendered = true;
		}
		return rendered;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void renderOutlines(RenderWorldLastEvent event) {
		if(xrayBlocks.isEmpty())
			return;
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(-RenderManager.renderPosX, 1 - RenderManager.renderPosY, -RenderManager.renderPosZ);
		GL11.glColor3f(255, 0, 0);
		Tessellator tess = Tessellator.instance;
		Tessellator.renderingWorldRenderer = false;

		Point last = new Point(0, 0, 0);
		for(Point block : xrayBlocks) {
			if(last.equals(0, 0, 0))
				GL11.glTranslated(block.x - last.x, block.y - last.y, block.z - last.z);
			else
				GL11.glTranslated(block.x - last.x, -(block.y - last.y), block.z - last.z);
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			renderOutlineBox(tess);
			last.set(block);
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	@SideOnly(Side.CLIENT)
	private void renderOutlineBox(Tessellator tess) {
		tess.startDrawing(GL11.GL_LINES);

		// FRONT
		tess.addVertex(0, 0, 0);
		tess.addVertex(0, 1, 0);

		tess.addVertex(0, 1, 0);
		tess.addVertex(1, 1, 0);

		tess.addVertex(1, 1, 0);
		tess.addVertex(1, 0, 0);

		tess.addVertex(1, 0, 0);
		tess.addVertex(0, 0, 0);

		// BACK
		tess.addVertex(0, 0, -1);
		tess.addVertex(0, 1, -1);
		tess.addVertex(0, 0, -1);
		tess.addVertex(1, 0, -1);
		tess.addVertex(1, 0, -1);
		tess.addVertex(1, 1, -1);
		tess.addVertex(0, 1, -1);
		tess.addVertex(1, 1, -1);

		// betweens.
		tess.addVertex(0, 0, 0);
		tess.addVertex(0, 0, -1);

		tess.addVertex(0, 1, 0);
		tess.addVertex(0, 1, -1);

		tess.addVertex(1, 0, 0);
		tess.addVertex(1, 0, -1);

		tess.addVertex(1, 1, 0);
		tess.addVertex(1, 1, -1);

		tess.draw();
	}
}
