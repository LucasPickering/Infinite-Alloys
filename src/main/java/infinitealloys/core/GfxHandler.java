package infinitealloys.core;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

import infinitealloys.client.gui.GuiInternetWand;
import infinitealloys.client.gui.GuiOverlay;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;

public final class GfxHandler implements IGuiHandler {

  private final GuiOverlay guiOverlay = new GuiOverlay();

  /**
   * A map of block identified by an x-ray machine to be highlighted, their respective colors.
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
  @SubscribeEvent
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    for (Map.Entry<BlockPos, Integer> entry : xrayBlocks.entrySet()) {
      final BlockPos pos = entry.getKey();
      renderBlockOutline(pos.getX(), pos.getY(), pos.getZ(), entry.getValue());
    }
  }

  /**
   * Draw a red outline around the block at the specific coordinates. To be specific, the coordinates
   * of a block are the ones given in the debug menu while standing ON TOP OF that block. Convenience
   * method for {@link #renderOutlineBox}.
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
    final WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();

    GL11.glPushMatrix();
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glLineWidth(5);

    final double renderX = TileEntityRendererDispatcher.staticPlayerX;
    final double renderY = TileEntityRendererDispatcher.staticPlayerY;
    final double renderZ = TileEntityRendererDispatcher.staticPlayerZ;
    GL11.glTranslated(-renderX, -renderY, -renderZ);

    renderer.startDrawing(GL11.GL_LINES);
    renderer.setColorOpaque_I(color);

    // Front
    renderer.addVertex(minX, minY, minZ);
    renderer.addVertex(minX, maxY, minZ);

    renderer.addVertex(minX, maxY, minZ);
    renderer.addVertex(maxX, maxY, minZ);

    renderer.addVertex(maxX, maxY, minZ);
    renderer.addVertex(maxX, minY, minZ);

    renderer.addVertex(maxX, minY, minZ);
    renderer.addVertex(minX, minY, minZ);

    // Back
    renderer.addVertex(minX, minY, maxZ);
    renderer.addVertex(minX, maxY, maxZ);

    renderer.addVertex(minX, minY, maxZ);
    renderer.addVertex(maxX, minY, maxZ);

    renderer.addVertex(maxX, minY, maxZ);
    renderer.addVertex(maxX, maxY, maxZ);

    renderer.addVertex(minX, maxY, maxZ);
    renderer.addVertex(maxX, maxY, maxZ);

    // Betweens
    renderer.addVertex(minX, minY, minZ);
    renderer.addVertex(minX, minY, maxZ);

    renderer.addVertex(minX, maxY, minZ);
    renderer.addVertex(minX, maxY, maxZ);

    renderer.addVertex(maxX, minY, minZ);
    renderer.addVertex(maxX, minY, maxZ);

    renderer.addVertex(maxX, maxY, minZ);
    renderer.addVertex(maxX, maxY, maxZ);

    Tessellator.getInstance().draw();

    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glPopMatrix();
  }
}
