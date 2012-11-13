package infinitealloys.client;

import infinitealloys.References;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderItem;

public class GuiMachineButton extends GuiScreen {

	private RenderItem itemRenderer;
	public int xPos, yPos, blockX, blockY, blockZ;

	public GuiMachineButton(RenderItem itemRenderer, int xPos, int yPos, int blockX, int blockY, int blockZ) {
		this.itemRenderer = itemRenderer;
		this.xPos = xPos;
		this.yPos = yPos;
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		width = 20;
		height = 20;
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft mc) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(References.TEXTURE_PATH + "gui/extras.png"));
		drawTexturedModalRect(xPos, yPos, GuiMachine.COMP_MACH_BG.x, GuiMachine.COMP_MACH_BG.y, GuiMachine.COMP_MACH_BG.width, GuiMachine.COMP_MACH_BG.height);
		itemRenderer.func_82406_b(fontRenderer, mc.renderEngine, new ItemStack(mc.theWorld.getBlockId(blockX, blockY, blockZ), 1, mc.theWorld.getBlockMetadata(blockX, blockY, blockZ)), xPos + 2, yPos + 2);
	}

	public boolean mousePressed(int mouseX, int mouseY) {
		return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
	}
}