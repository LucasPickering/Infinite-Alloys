package infinitealloys.client;

import infinitealloys.ContainerMetalForge;
import infinitealloys.IAValues;
import infinitealloys.TileEntityMetalForge;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Slot;

public class GuiMetalForge extends GuiMachine {

	private TileEntityMetalForge temf;

	public GuiMetalForge(InventoryPlayer inventoryPlayer, TileEntityMetalForge tileEntity) {
		super(tileEntity, new ContainerMetalForge(inventoryPlayer, tileEntity));
		xSize = 176;
		ySize = 216;
		temf = tileEntity;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		for(int i = 0; i < IAValues.metalCount; i++) {
			Slot slot = inventorySlots.getSlot(i + 1);
			if(func_74188_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY) && 0 <= i && i <= 7)
				drawTextBox(IAValues.metalNames[i], 0xffffff, mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		fontRenderer.drawString("Inventory", 42, ySize - 94, 4210752);
		for(int y = 0; y < 2; y++)
			for(int x = 0; x < 4; x++)
				fontRenderer.drawStringWithShadow(new Byte(temf.recipeAmts[y * 4 + x]).toString(), x * 18 + 45, y * 18 + 35, 0xffffff);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
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
			drawTexturedModalRect(left + 8, top + 30 - l, 176, 12 - l, 14, l + 2);
		}
		l = temf.getCookProgressScaled(24);
		drawTexturedModalRect(left + 112, top + 34, 176, 14, l + 1, 16);
	}
}
