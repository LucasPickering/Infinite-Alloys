package infinitealloys.client;

import infinitealloys.ContainerXray;
import infinitealloys.References;
import infinitealloys.TileEntityXray;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.InventoryPlayer;

public class GuiXray extends GuiMachine {

	private TileEntityXray tex;

	public GuiXray(InventoryPlayer inventoryPlayer, TileEntityXray tileEntity) {
		super(tileEntity, new ContainerXray(inventoryPlayer, tileEntity));
		tex = tileEntity;
		ySize = 148;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(References.TEXTURE_PATH + "gui/printer.png"));
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}
}
