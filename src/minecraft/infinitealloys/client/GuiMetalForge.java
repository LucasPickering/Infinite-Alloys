package infinitealloys.client;

import infinitealloys.ContainerMetalForge;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.TileEntityMetalForge;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
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
		for(int i = 0; i < References.metalCount; i++)
			fontRenderer.drawStringWithShadow(new Byte(temf.recipeAmts[i]).toString(), i % 4 * 18 + 45, i / 4 * 18 + 47, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		bindTexture("metalforge");
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
		bindTexture("extras");
		drawTexturedModalRect(topLeft.x + 113, topLeft.y + 53, PROGRESS_ARROW.x, PROGRESS_ARROW.y, temf.getProcessProgressScaled(PROGRESS_ARROW.width) + 1, PROGRESS_ARROW.height);
		for(int i = 0; i < References.metalCount; i++)
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(InfiniteAlloys.ingot, 1, i), topLeft.x + i % 4 * 18 + 34, topLeft.y + i / 4 * 18 + 38);
	}
}
