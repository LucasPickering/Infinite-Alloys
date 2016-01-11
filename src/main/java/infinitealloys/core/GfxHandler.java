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
   * A map of blocks and their respective colors, to have outlines drawn around them. These blocks are
   * located and specified by {@link infinitealloys.tile.TEEXray}.
   */
  @SideOnly(Side.CLIENT)
  public Map<BlockPos, Integer> xrayBlocks = new HashMap<>();

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
    // Render an outline for each block in xrayBlocks
    xrayBlocks.entrySet().forEach(entry -> renderBlockOutline(entry.getKey(), entry.getValue()));
  }

  /**
   * Draw an outline around the block at the specific coordinates. To be specific, the coordinates of
   * a block are the ones given in the debug menu while standing ON TOP OF that block. Convenience
   * method for {@link #renderBoxOutline}.
   *
   * @param pos   the position of the block to be outlined
   * @param color a hexcode for the color of the outline
   */
  @SideOnly(Side.CLIENT)
  private void renderBlockOutline(BlockPos pos, int color) {
    renderBoxOutline(pos, pos, color);
  }

  /**
   * Draw an outline around the specified set of block(s).  Passing the same argument twice will draw
   * an outline around just that box.
   *
   * @param pos1  the first corner of the box
   * @param pos2  the second corner of the box
   * @param color a hexcode for the color to be drawn
   */
  @SideOnly(Side.CLIENT)
  private void renderBoxOutline(BlockPos pos1, BlockPos pos2, int color) {
    final WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();

    GL11.glPushMatrix();
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glLineWidth(5);

    final double renderX = TileEntityRendererDispatcher.staticPlayerX;
    final double renderY = TileEntityRendererDispatcher.staticPlayerY;
    final double renderZ = TileEntityRendererDispatcher.staticPlayerZ;
    GL11.glTranslated(-renderX, -renderY, -renderZ);

    final int x1 = Math.min(pos1.getX(), pos2.getX());
    final int y1 = Math.min(pos1.getY(), pos2.getY());
    final int z1 = Math.min(pos1.getZ(), pos2.getZ());

    // Add one to these to get the upper corner of the block
    final int x2 = Math.max(pos1.getX(), pos2.getX()) + 1;
    final int y2 = Math.max(pos1.getY(), pos2.getY()) + 1;
    final int z2 = Math.max(pos1.getZ(), pos2.getZ()) + 1;

    renderer.startDrawing(GL11.GL_LINES);
    renderer.setColorOpaque_I(color);

    // Front
    renderer.addVertex(x1, y1, z1);
    renderer.addVertex(x1, y2, z1);

    renderer.addVertex(x1, y2, z1);
    renderer.addVertex(x2, y2, z1);

    renderer.addVertex(x2, y2, z1);
    renderer.addVertex(x2, y1, z1);

    renderer.addVertex(x2, y1, z1);
    renderer.addVertex(x1, y1, z1);

    // Back
    renderer.addVertex(x1, y1, z2);
    renderer.addVertex(x1, y2, z2);

    renderer.addVertex(x1, y1, z2);
    renderer.addVertex(x2, y1, z2);

    renderer.addVertex(x2, y1, z2);
    renderer.addVertex(x2, y2, z2);

    renderer.addVertex(x1, y2, z2);
    renderer.addVertex(x2, y2, z2);

    // Betweens
    renderer.addVertex(x1, y1, z1);
    renderer.addVertex(x1, y1, z2);

    renderer.addVertex(x1, y2, z1);
    renderer.addVertex(x1, y2, z2);

    renderer.addVertex(x2, y1, z1);
    renderer.addVertex(x2, y1, z2);

    renderer.addVertex(x2, y2, z1);
    renderer.addVertex(x2, y2, z2);

    Tessellator.getInstance().draw();

    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glPopMatrix();
  }
}
