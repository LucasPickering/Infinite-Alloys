package infinitealloys.core;

import infinitealloys.block.BlockMachine;
import infinitealloys.block.IABlocks;
import infinitealloys.client.gui.GuiInternetWand;
import infinitealloys.client.gui.GuiSummoner;
import infinitealloys.tile.TEIASummoner;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.Point;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GfxHandler implements IGuiHandler, ISimpleBlockRenderingHandler {

	public int renderID;
	private final TileEntityMachine[] temInstances = new TileEntityMachine[Consts.MACHINE_COUNT];

	public GfxHandler() {
		try {
			for(int i = 0; i < temInstances.length; i++) {
				temInstances[i] = (TileEntityMachine)EnumMachine.values()[i].temClass.newInstance();
				temInstances[i].orientation = 3;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/** The list of blocks identified by an x-ray machine to be highlighted */
	@SideOnly(Side.CLIENT)
	public ArrayList<Point> xrayBlocks = new ArrayList<Point>();

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if(id < Consts.MACHINE_COUNT)
			return EnumMachine.values()[id].getContainer(player.inventory, (TileEntityMachine)world.getTileEntity(x, y, z));
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if(id < Consts.MACHINE_COUNT)
			return EnumMachine.values()[id].getGui(player.inventory, (TileEntityMachine)world.getTileEntity(x, y, z));
		else if(id == Consts.WAND_GUI_ID)
			return new GuiInternetWand();
		else if(id == Consts.SUMMONER_GUI_ID)
			return new GuiSummoner((TEIASummoner)world.getTileEntity(x, y, z));
		return null;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		if(block instanceof BlockMachine && metadata < temInstances.length) {
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			TileEntityRendererDispatcher.instance.renderTileEntityAt(temInstances[metadata], 0, 0, 0, 0);
		}
		else {
			Tessellator tessellator = Tessellator.instance;
			block.setBlockBoundsForItemRender();
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

			tessellator.startDrawingQuads();
			tessellator.setNormal(0F, -1F, 0F);
			renderer.renderFaceYNeg(block, 0D, 0D, 0D, block.getIcon(0, 0));
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(0F, 1F, 0F);
			renderer.renderFaceYPos(block, 0D, 0D, 0D, block.getIcon(1, 0));
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(1F, 0F, 0F);
			renderer.renderFaceXPos(block, 0D, 0D, 0D, block.getIcon(2, 0));
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(-1F, 0F, 0F);
			renderer.renderFaceXNeg(block, 0D, 0D, 0D, block.getIcon(3, 0));
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(0F, 0F, -1F);
			renderer.renderFaceZNeg(block, 0D, 0D, 0D, block.getIcon(4, 0));
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setNormal(0F, 0F, 1F);
			renderer.renderFaceZPos(block, 0D, 0D, 0D, block.getIcon(5, 0));
			tessellator.draw();

			// Draw ore pieces on top of stone background
			if(metadata < Consts.METAL_COUNT) {
				final int mult = Consts.METAL_COLORS[metadata];
				GL11.glColor4f((mult >> 16 & 255) / 255F, (mult >> 8 & 255) / 255F, (mult & 255) / 255F, 1F);

				tessellator.startDrawingQuads();
				tessellator.setNormal(0F, -1F, 0F);
				renderer.renderFaceYNeg(block, 0D, 0D, 0D, IABlocks.oreForegroundIcon);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.setNormal(0F, 1F, 0F);
				renderer.renderFaceYPos(block, 0D, 0D, 0D, IABlocks.oreForegroundIcon);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.setNormal(1F, 0F, 0F);
				renderer.renderFaceXPos(block, 0D, 0D, 0D, IABlocks.oreForegroundIcon);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.setNormal(-1F, 0F, 0F);
				renderer.renderFaceXNeg(block, 0D, 0D, 0D, IABlocks.oreForegroundIcon);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.setNormal(0F, 0F, -1F);
				renderer.renderFaceZNeg(block, 0D, 0D, 0D, IABlocks.oreForegroundIcon);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.setNormal(0F, 0F, 1F);
				renderer.renderFaceZPos(block, 0D, 0D, 0D, IABlocks.oreForegroundIcon);
				tessellator.draw();
			}
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if(block instanceof BlockMachine)
			return true;
		Tessellator tessellator = Tessellator.instance;
		boolean rendered = renderer.renderStandardBlock(block, x, y, z);
		final int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
		if(block == IABlocks.ore) { // Used to colorize the ores
			final int color = Consts.METAL_COLORS[world.getBlockMetadata(x, y, z)];
			float red = (color >> 16 & 255) / 255F;
			float green = (color >> 8 & 255) / 255F;
			float blue = (color & 255) / 255F;

			if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0)) {
				tessellator.setBrightness(renderer.renderMinY > 0D ? brightness : block.getMixedBrightnessForBlock(world, x, y - 1, z));
				tessellator.setColorOpaque_F(red * 0.5F, green * 0.5F, blue * 0.5F);
				renderer.renderFaceYNeg(block, x, y, z, IABlocks.oreForegroundIcon);
				rendered = true;
			}
			if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
				tessellator.setBrightness(renderer.renderMaxY < 1D ? brightness : block.getMixedBrightnessForBlock(world, x, y + 1, z));
				tessellator.setColorOpaque_F(red, green, blue);
				renderer.renderFaceYPos(block, x, y, z, IABlocks.oreForegroundIcon);
				rendered = true;
			}
			if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 2)) {
				tessellator.setBrightness(renderer.renderMinZ > 0D ? brightness : block.getMixedBrightnessForBlock(world, x + 1, y, z));
				tessellator.setColorOpaque_F(red * 0.6F, green * 0.6F, blue * 0.6F);
				renderer.renderFaceXPos(block, x, y, z, IABlocks.oreForegroundIcon);
				rendered = true;
			}
			if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 3)) {
				tessellator.setBrightness(renderer.renderMaxZ < 1D ? brightness : block.getMixedBrightnessForBlock(world, x - 1, y, z));
				tessellator.setColorOpaque_F(red * 0.6F, green * 0.6F, blue * 0.6F);
				renderer.renderFaceXNeg(block, x, y, z, IABlocks.oreForegroundIcon);
				rendered = true;
			}
			if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 4)) {
				tessellator.setBrightness(renderer.renderMinX > 0D ? brightness : block.getMixedBrightnessForBlock(world, x, y, z - 1));
				tessellator.setColorOpaque_F(red * 0.8F, green * 0.8F, blue * 0.8F);
				renderer.renderFaceZNeg(block, x, y, z, IABlocks.oreForegroundIcon);
				rendered = true;
			}
			if(renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 5)) {
				tessellator.setBrightness(renderer.renderMaxX < 1D ? brightness : block.getMixedBrightnessForBlock(world, x, y, z + 1));
				tessellator.setColorOpaque_F(red * 0.8F, green * 0.8F, blue * 0.8F);
				renderer.renderFaceZPos(block, x, y, z, IABlocks.oreForegroundIcon);
				rendered = true;
			}
		}
		return rendered;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelID) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderOutlines(RenderWorldLastEvent event) {
		if(xrayBlocks.isEmpty())
			return;
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, 1 - RenderManager.renderPosZ);
		Tessellator tess = Tessellator.instance;
		Tessellator.renderingWorldRenderer = false;

		Point last = new Point(0, 0, 0);
		for(Point block : xrayBlocks) {
			GL11.glTranslatef(block.x - last.x, block.y - last.y, block.z - last.z);
			renderOutlineBox(tess);
			last.set(block);
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	@SideOnly(Side.CLIENT)
	private static void renderOutlineBox(Tessellator tess) {
		tess.startDrawing(GL11.GL_LINES);

		// Front
		tess.addVertex(0, 0, 0);
		tess.addVertex(0, 1, 0);

		tess.addVertex(0, 1, 0);
		tess.addVertex(1, 1, 0);

		tess.addVertex(1, 1, 0);
		tess.addVertex(1, 0, 0);

		tess.addVertex(1, 0, 0);
		tess.addVertex(0, 0, 0);

		// Back
		tess.addVertex(0, 0, -1);
		tess.addVertex(0, 1, -1);

		tess.addVertex(0, 0, -1);
		tess.addVertex(1, 0, -1);

		tess.addVertex(1, 0, -1);
		tess.addVertex(1, 1, -1);

		tess.addVertex(0, 1, -1);
		tess.addVertex(1, 1, -1);

		// Betweens
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
