package infinitealloys.client;

import infinitealloys.ContainerPrinter;
import infinitealloys.References;
import infinitealloys.TileEntityPrinter;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.InventoryPlayer;

public class GuiPrinter extends GuiMachine {

	private TileEntityPrinter tep;

	public GuiPrinter(InventoryPlayer inventoryPlayer, TileEntityPrinter tileEntity) {
		super(176, 148, tileEntity, new ContainerPrinter(inventoryPlayer, tileEntity));
		tep = tileEntity;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		bindTexture("printer");
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
		drawTexturedModalRect(topLeft.x + 33, topLeft.y + 25, 0, 148, tep.getPrintProgressScaled(110), 16);
	}
}
