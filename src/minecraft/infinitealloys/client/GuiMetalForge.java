package infinitealloys.client;

import org.lwjgl.opengl.GL11;
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
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for(int i = 0; i < References.metalCount; i++) {
			if(mouseInZone(mouseX, mouseY, topLeft.x + i % 4 * 18 + 39, topLeft.y + i / 4 * 18 + 42, 18, 18))
				drawTextBox(InfiniteAlloys.getStringLocalization("metal." + References.metalNames[i] + ".name"), 0xffffff, mouseX, mouseY);
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		for(int i = 0; i < References.metalCount; i++)
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(InfiniteAlloys.ingot, 1, i), i % 4 * 18 + 40, i / 4 * 18 + 43);
		for(int i = 0; i < References.metalCount; i++)
			fontRenderer.drawStringWithShadow(new Byte(temf.recipeAmts[i]).toString(), i % 4 * 18 + 51, i / 4 * 18 + 52, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		bindTexture("metalforge");
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
		bindTexture("extras");
		drawTexturedModalRect(topLeft.x + 113, topLeft.y + 53, PROGRESS_ARROW.x, PROGRESS_ARROW.y, temf.getProcessProgressScaled(PROGRESS_ARROW.width) + 1, PROGRESS_ARROW.height);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		for(int i = 0; i < References.metalCount; i++) {
			if(mouseInZone(mouseX, mouseY, topLeft.x + i % 4 * 18 + 39, topLeft.y + i / 4 * 18 + 42, 18, 18)) {
				temf.processProgress = 0;
				if(mouseButton == 0)
					temf.recipeAmts[i] = (byte)Math.min(temf.recipeAmts[i] + 1, References.alloyRadix - 1);
				else if(mouseButton == 1)
					temf.recipeAmts[i] = (byte)Math.max(temf.recipeAmts[i] - 1, 0);
				break;
			}
		}
	}
}
