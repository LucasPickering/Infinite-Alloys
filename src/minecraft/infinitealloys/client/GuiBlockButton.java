package infinitealloys.client;

import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

public class GuiBlockButton extends GuiScreen {

	private RenderItem itemRenderer;
	private int xPos, yPos;
	private int blockID, blockAmount, blockMeta;
	private int yValue;
	public boolean activated;

	public GuiBlockButton(Minecraft mc, RenderItem itemRenderer, int xPos, int yPos, int blockID, int blockAmount, int blockMeta, int yValue) {
		this.mc = mc;
		this.itemRenderer = itemRenderer;
		this.xPos = xPos;
		this.yPos = yPos;
		this.blockID = blockID;
		this.blockAmount = blockAmount;
		this.blockMeta = blockMeta;
		this.yValue = yValue;
		width = 36;
		height = 18;
	}

	public void drawButton() {
		if(blockAmount > 0) {
			GuiMachine.bindTexture("extras");
			Rectangle bg;
			if(activated)
				bg = GuiMachine.BLOCK_BG_ON;
			else
				bg = GuiMachine.BLOCK_BG_OFF;
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			drawTexturedModalRect(xPos, yPos, bg.x, bg.y, bg.width, bg.height);
			String display = Integer.toString(yValue);
			mc.fontRenderer.drawStringWithShadow(display, 17 - (mc.fontRenderer.getStringWidth(display) / 2), 32, 0xffffff);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(blockID, 1, blockMeta), xPos + 19, yPos + 1);
            itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(blockID, blockAmount, blockMeta), xPos + 19, yPos + 1);
		}
	}

	public boolean mousePressed(int mouseX, int mouseY) {
		return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
	}
}
