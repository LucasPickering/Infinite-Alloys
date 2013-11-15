package infinitealloys.client.gui;

import org.lwjgl.opengl.GL11;
import infinitealloys.tile.TEMGenerator;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiGenerator extends GuiMachine {

	public TEMGenerator teg;

	public java.awt.Point fire = new java.awt.Point(30, 75);

	public GuiGenerator(InventoryPlayer inventoryPlayer, TEMGenerator tileEntity) {
		super(176, 176,inventoryPlayer, tileEntity);
		teg = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(extras);

		// Draw the flame overlay
		if(tem.ticksToProcess > 0)
			drawTexturedModalRect(progressBar.x, progressBar.y, PROGRESS_BAR.x, PROGRESS_BAR.y, tem.getProcessProgressScaled(PROGRESS_BAR.width),
					PROGRESS_BAR.height);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
