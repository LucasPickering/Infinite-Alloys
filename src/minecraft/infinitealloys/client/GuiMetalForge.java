package infinitealloys.client;

import infinitealloys.ContainerMetalForge;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.TileEntityMetalForge;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Slot;

public class GuiMetalForge extends GuiMachine {

	private TileEntityMetalForge temf;

	public GuiMetalForge(InventoryPlayer inventoryPlayer, TileEntityMetalForge tileEntity) {
		super(176, 216, tileEntity, new ContainerMetalForge(inventoryPlayer, tileEntity));
		temf = tileEntity;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		for(int i = 0; i < References.metalCount; i++) {
			Slot slot = inventorySlots.getSlot(i + 1);
			if(func_74188_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY) && 0 <= i && i <= 7)
				drawTextBox(InfiniteAlloys.getStringLocalization("metal." + References.metalNames[i] + ".name"), 0xffffff, mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		for(int y = 0; y < 2; y++)
			for(int x = 0; x < 4; x++)
				fontRenderer.drawStringWithShadow(new Byte(temf.recipeAmts[y * 4 + x]).toString(), x * 18 + 45, y * 18 + 35, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		bindTexture("metalforge");
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
		bindTexture("extras");
		drawTexturedModalRect(topLeft.x + 112, topLeft.y + 34, PROGRESS_ARROW.x, PROGRESS_ARROW.y, temf.getSmeltProgressScaled(PROGRESS_ARROW.width) + 1, PROGRESS_ARROW.height);
	}
}
