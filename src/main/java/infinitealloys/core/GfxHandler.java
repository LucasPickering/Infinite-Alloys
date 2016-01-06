package infinitealloys.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

import infinitealloys.client.gui.GuiInternetWand;
import infinitealloys.client.gui.GuiOverlay;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;

public final class GfxHandler implements IGuiHandler {

  public int renderID;
  private final GuiOverlay guiOverlay = new GuiOverlay();

  /**
   * A map of blocks identified by an x-ray machine to be highlighted, their respective colors.
   */
  @SideOnly(Side.CLIENT)
  public HashMap<BlockPos, Integer> xrayBlocks = new HashMap<>();

  @Override
  public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    if (id < Consts.MACHINE_COUNT) {
      return EnumMachine.values()[id].getNewContainer(
          player.inventory, (TileEntityMachine) world.getTileEntity(new BlockPos(x, y, z)));
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    if (id < Consts.MACHINE_COUNT) {
      return EnumMachine.values()[id].getNewGui(
          player.inventory, (TileEntityMachine) world.getTileEntity(new BlockPos(x, y, z)));
    } else if (id == Consts.WAND_GUI_ID) {
      return new GuiInternetWand();
    }
    return null;
  }

  /*
  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    if (block == IABlocks.machine) {
      if (metadata < temInstances.length) {
        TileEntityRendererDispatcher.instance.renderTileEntityAt(temInstances[metadata],
                                                                 -0.5D, -0.5D, -0.5D, 0);
      }
    } else if (block == IABlocks.ore) {
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

      // Draw ore pieces on top of stone backgroundIcon
      if (metadata < Consts.METAL_COUNT) {
        int mult = EnumMetal.values()[metadata].color;
        GL11.glColor4f((mult >> 16 & 255) / 255F, (mult >> 8 & 255) / 255F, (mult & 255) / 255F,
                       1F);

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
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
                                  RenderBlocks renderer) {
    boolean rendered = true;
    if (block == IABlocks.ore) { // Used to colorize the ores
      rendered = renderer.renderStandardBlock(block, x, y, z);
      Tessellator tessellator = Tessellator.instance;
      int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
      int color = EnumMetal.values()[world.getBlockMetadata(x, y, z)].color;
      float red = (color >> 16 & 255) / 255F;
      float green = (color >> 8 & 255) / 255F;
      float blue = (color & 255) / 255F;

      if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0)) {
        tessellator.setBrightness(renderer.renderMinY > 0D ? brightness : block
            .getMixedBrightnessForBlock(world, x, y - 1, z));
        tessellator.setColorOpaque_F(red * 0.5F, green * 0.5F, blue * 0.5F);
        renderer.renderFaceYNeg(block, x, y, z, IABlocks.oreForegroundIcon);
        rendered = true;
      }
      if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
        tessellator.setBrightness(renderer.renderMaxY < 1D ? brightness : block
            .getMixedBrightnessForBlock(world, x, y + 1, z));
        tessellator.setColorOpaque_F(red, green, blue);
        renderer.renderFaceYPos(block, x, y, z, IABlocks.oreForegroundIcon);
        rendered = true;
      }
      if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 2)) {
        tessellator.setBrightness(renderer.renderMinZ > 0D ? brightness : block
            .getMixedBrightnessForBlock(world, x + 1, y, z));
        tessellator.setColorOpaque_F(red * 0.6F, green * 0.6F, blue * 0.6F);
        renderer.renderFaceXPos(block, x, y, z, IABlocks.oreForegroundIcon);
        rendered = true;
      }
      if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 3)) {
        tessellator.setBrightness(renderer.renderMaxZ < 1D ? brightness : block
            .getMixedBrightnessForBlock(world, x - 1, y, z));
        tessellator.setColorOpaque_F(red * 0.6F, green * 0.6F, blue * 0.6F);
        renderer.renderFaceXNeg(block, x, y, z, IABlocks.oreForegroundIcon);
        rendered = true;
      }
      if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 4)) {
        tessellator.setBrightness(renderer.renderMinX > 0D ? brightness : block
            .getMixedBrightnessForBlock(world, x, y, z - 1));
        tessellator.setColorOpaque_F(red * 0.8F, green * 0.8F, blue * 0.8F);
        renderer.renderFaceZNeg(block, x, y, z, IABlocks.oreForegroundIcon);
        rendered = true;
      }
      if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 5)) {
        tessellator.setBrightness(renderer.renderMaxX < 1D ? brightness : block
            .getMixedBrightnessForBlock(world, x, y, z + 1));
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

  @SubscribeEvent
  public void onRenderGameOverlay(RenderGameOverlayEvent event) {
    if (!event.isCancelable() && event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
      guiOverlay.drawHealthBar();
    }
  }

  @SubscribeEvent
  public void onInitGui(GuiScreenEvent.InitGuiEvent event) {
    guiOverlay.resizeGui();
  }

  @SideOnly(Side.CLIENT)
  @SuppressWarnings("unchecked")
  @SubscribeEvent
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    for (Point3 block : xrayBlocks.keySet()) {
      renderBlockOutline(block.x, block.y, block.z, xrayBlocks.get(block));
    }
  }
  */

  /**
   * Draw a red outline around the block at the specific coordinates. To be specific, the
   * coordinates of a block are the ones given in the debug menu while standing ON TOP OF that
   * block. Convenience method for {@link #renderOutlineBox}.
   *
   * @param blockX the x-coordinate of the block to be outlined
   * @param blockY the y-coordinate of the block to be outlined
   * @param blockZ the z-coordinate of the block to be outlined
   * @param color  a hexcode for the color of the outline
   */
  @SideOnly(Side.CLIENT)
  private void renderBlockOutline(int blockX, int blockY, int blockZ, int color) {
    renderOutlineBox(blockX, blockY - 1, blockZ, blockX + 1, blockY, blockZ + 1, color);
  }

  /**
   * Draw a red outline
   *
   * @param minX  the x value for the first corner
   * @param minY  the y value for the first corner
   * @param minZ  the z value for the first corner
   * @param maxX  the x value for the second corner
   * @param maxY  the y value for the second corner
   * @param maxZ  the z value for the second corner
   * @param color a hexcode for the color to be drawn
   */
  @SideOnly(Side.CLIENT)
  private void renderOutlineBox(double minX, double minY, double minZ,
                                double maxX, double maxY, double maxZ, int color) {
    /*
    final RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
    final Tessellator tess = Tessellator.getInstance();

    GL11.glPushMatrix();
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glTranslated(-renderManager.render, -RenderManager.renderPosY,
                      -RenderManager.renderPosZ);

    tess.startDrawing(GL11.GL_LINES);
    tess.setColorOpaque_I(color);

    // Front
    tess.addVertex(minX, minY, minZ);
    tess.addVertex(minX, maxY, minZ);

    tess.addVertex(minX, maxY, minZ);
    tess.addVertex(maxX, maxY, minZ);

    tess.addVertex(maxX, maxY, minZ);
    tess.addVertex(maxX, minY, minZ);

    tess.addVertex(maxX, minY, minZ);
    tess.addVertex(minX, minY, minZ);

    // Back
    tess.addVertex(minX, minY, maxZ);
    tess.addVertex(minX, maxY, maxZ);

    tess.addVertex(minX, minY, maxZ);
    tess.addVertex(maxX, minY, maxZ);

    tess.addVertex(maxX, minY, maxZ);
    tess.addVertex(maxX, maxY, maxZ);

    tess.addVertex(minX, maxY, maxZ);
    tess.addVertex(maxX, maxY, maxZ);

    // Betweens
    tess.addVertex(minX, minY, minZ);
    tess.addVertex(minX, minY, maxZ);

    tess.addVertex(minX, maxY, minZ);
    tess.addVertex(minX, maxY, maxZ);

    tess.addVertex(maxX, minY, minZ);
    tess.addVertex(maxX, minY, maxZ);

    tess.addVertex(maxX, maxY, minZ);
    tess.addVertex(maxX, maxY, maxZ);

    tess.draw();

    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glPopMatrix();
    */
  }
}
