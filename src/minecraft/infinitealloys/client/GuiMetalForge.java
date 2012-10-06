package infinitealloys.client;

import infinitealloys.ContainerMetalForge;
import infinitealloys.TileEntityMetalForge;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;

public class GuiMetalForge extends GuiContainer {

	private TileEntityMetalForge temf;
	private GuiButton idMinus;
	private GuiButton idPlus;

	public GuiMetalForge(InventoryPlayer inventoryPlayer, TileEntityMetalForge tileEntity) {
		super(new ContainerMetalForge(inventoryPlayer, tileEntity));
		xSize = 176;
		ySize = 216;
		temf = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		controlList.clear();
		controlList.add(idMinus = new GuiButton(0, width / 2 + 57, height / 2 - 102, 20, 20, "+"));
		controlList.add(idPlus = new GuiButton(1, width / 2 + 18, height / 2 - 102, 20, 20, "-"));
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		fontRenderer.drawString("Inventory", 42, ySize - 94, 4210752);
		fontRenderer.drawString(new Integer(temf.networkID).toString(), 133, ySize - 204, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int k = mc.renderEngine.getTexture("/infinitealloys/gfx/guimetalforge.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(k);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		int l;
		if(temf.heatLeft > 0) {
			l = temf.getBurnTimeRemainingScaled(12);
			drawTexturedModalRect(left + 16, top + 30 - l, 176, 12 - l, 14, l + 2);
		}
		l = temf.getCookProgressScaled(24);
		drawTexturedModalRect(left + 104, top + 34, 176, 14, l + 1, 16);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if(guibutton.enabled) {
			if(guibutton.id == 0)
				temf.networkID = Math.min(++temf.networkID, Byte.MAX_VALUE);
			if(guibutton.id == 1)
				temf.networkID = Math.max(--temf.networkID, 0);
		}
	}
}
