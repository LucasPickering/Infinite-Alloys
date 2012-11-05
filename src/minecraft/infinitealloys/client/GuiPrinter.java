package infinitealloys.client;

import java.util.ArrayList;
import infinitealloys.ContainerAnalyzer;
import infinitealloys.ContainerPrinter;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.TileEntityAnalyzer;
import infinitealloys.TileEntityPrinter;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.InventoryPlayer;

public class GuiPrinter extends GuiMachine {

	private TileEntityPrinter tep;

	public GuiPrinter(InventoryPlayer inventoryPlayer, TileEntityPrinter tileEntity) {
		super(tileEntity, new ContainerPrinter(inventoryPlayer, tileEntity));
		tep = tileEntity;
		ySize = 148;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(References.TEXTURE_PATH + "gui/printer.png"));
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		drawTexturedModalRect(left + 33, top + 25, 0, 148, tep.getPrintProgressScaled(110), 16);
	}
}
