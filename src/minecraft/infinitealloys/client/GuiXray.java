package infinitealloys.client;

import infinitealloys.ContainerXray;
import infinitealloys.TileEntityXray;
import net.minecraft.src.InventoryPlayer;

public class GuiXray extends GuiMachine {

	private TileEntityXray tex;

	public GuiXray(InventoryPlayer inventoryPlayer, TileEntityXray tileEntity) {
		super(176, 238, tileEntity, new ContainerXray(inventoryPlayer, tileEntity));
		tex = tileEntity;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		bindTexture("xray");
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
	}
}
