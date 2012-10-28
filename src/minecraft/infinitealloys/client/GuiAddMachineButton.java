package infinitealloys.client;

import infinitealloys.IAValues;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;

public class GuiAddMachineButton extends GuiScreen {
	private int xPos, yPos;

	public GuiAddMachineButton(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
		width = 16;
		height = 16;
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.renderEngine.getTexture(IAValues.TEXTURE_PATH + "guicomputer.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(xPos, yPos, 176, 0, width, height);
	}

	public boolean mousePressed(int mouseX, int mouseY) {
		return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
	}
}
