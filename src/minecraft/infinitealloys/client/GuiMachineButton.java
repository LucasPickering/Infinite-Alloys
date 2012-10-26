package infinitealloys.client;

import infinitealloys.IAValues;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;

public class GuiMachineButton extends GuiScreen {

	public int xPos, yPos, blockX, blockY, blockZ;

	public GuiMachineButton(int xPos, int yPos, int blockX, int blockY, int blockZ) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		width = 16;
		height = 16;
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.renderEngine.getTexture(IAValues.TEXTURE_PATH + "guicomputer.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(xPos, yPos, 0, 176 + minecraft.theWorld.getBlockMetadata(blockX, blockY, blockZ) * 16, width / 2, height);
		drawTexturedModalRect(xPos + width / 2, yPos, 200 - width / 2, 46 + minecraft.theWorld.getBlockMetadata(blockX, blockY, blockZ) * 20, width / 2, height);
	}

	public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
		return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
	}
}