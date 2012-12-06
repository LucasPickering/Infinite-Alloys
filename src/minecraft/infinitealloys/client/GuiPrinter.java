package infinitealloys.client;

import org.lwjgl.opengl.GL11;
import infinitealloys.inventory.ContainerPrinter;
import infinitealloys.tile.TileEntityPrinter;
import net.minecraft.src.InventoryPlayer;

public class GuiPrinter extends GuiMachine {

	private TileEntityPrinter tep;

	public GuiPrinter(InventoryPlayer inventoryPlayer, TileEntityPrinter tileEntity) {
		super(176, 148, tileEntity, new ContainerPrinter(inventoryPlayer, tileEntity), "printer");
		tep = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		bindTexture("extras");
		drawTexturedModalRect(31, 14, PROGRESS_BAR.x, PROGRESS_BAR.y, tep.getProcessProgressScaled(PROGRESS_BAR.width), PROGRESS_BAR.height);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
