package infinitealloys.client;

import infinitealloys.References;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiAlloyBook extends GuiScreen {

	private int xSize = 256;
	private int ySize = 192;
	private ItemStack bookItem;

	public GuiAlloyBook(ItemStack book) {
		bookItem = book;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(References.TEXTURE_PATH + "gui/alloybook.png"));
		drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
	}
}
