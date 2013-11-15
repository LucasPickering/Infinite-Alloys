package infinitealloys.client.gui;

import infinitealloys.tile.TileEntityElectric;
import java.awt.Rectangle;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public abstract class GuiElectric extends GuiMachine {

	// The position for each item in the texture sheet extras.png
	static final Rectangle PROGRESS_BAR = new Rectangle(119, 0, 108, 18);
	static final Rectangle SCROLL_ON = new Rectangle(227, 0, 12, 15);
	static final Rectangle SCROLL_OFF = new Rectangle(239, 0, 12, 15);
	static final Rectangle UP_ARROW = new Rectangle(10, 24, 16, 16);
	static final Rectangle DOWN_ARROW = new Rectangle(26, 24, 16, 16);
	static final Rectangle CHECK = new Rectangle(42, 24, 16, 16);
	static final Rectangle BLOCK_BG_OFF = new Rectangle(58, 24, 36, 18);
	static final Rectangle BLOCK_BG_ON = new Rectangle(94, 24, 36, 18);

	/** Coordinates of the progress bar texture, changes by machine but still otherwise */
	protected java.awt.Point progressBar = new java.awt.Point();

	protected TileEntityElectric tem;

	public GuiElectric(int xSize, int ySize, InventoryPlayer inventoryPlayer, TileEntityElectric tileEntity) {
		super(xSize, ySize, inventoryPlayer, tileEntity);
		tem = tileEntity;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		super.drawScreen(mouseX, mouseY, partialTick);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// Draw the progress info if the mouse is over the progress bar
		if(mouseInZone(mouseX, mouseY, topLeft.x + progressBar.x, topLeft.y + progressBar.y, PROGRESS_BAR.width, PROGRESS_BAR.height))
			drawTextBox(mouseX, mouseY, new ColoredLine(tem.getProcessProgressScaled(100) + "%", 0xffffff));

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(extras);

		// Draw the progress bar overlay
		if(tem.ticksToProcess > 0)
			drawTexturedModalRect(progressBar.x, progressBar.y, PROGRESS_BAR.x, PROGRESS_BAR.y, tem.getProcessProgressScaled(PROGRESS_BAR.width), PROGRESS_BAR.height);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
