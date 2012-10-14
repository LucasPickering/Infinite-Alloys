package infinitealloys.client;

import infinitealloys.ContainerMachine;
import infinitealloys.TileEntityComputer;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.InventoryPlayer;

public class GuiComputer extends GuiMachine {

	public GuiComputer(InventoryPlayer inventoryPlayer, TileEntityComputer tileEntity) {
		super(tileEntity, new ContainerMachine(inventoryPlayer, tileEntity));
		xSize = 176;
		ySize = 216;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		int k = mc.renderEngine.getTexture("/infinitealloys/gfx/guicomputer.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(k);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}
}
