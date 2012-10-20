package infinitealloys.client;

import net.minecraft.src.GuiButton;

public class GuiButtonID extends GuiButton {

	public GuiButtonID(int id, int x, int y, String text) {
		super(id, x, y, 20, 20, text);
	}

	public boolean isMouseOver(int mouseX,int mouseY) {
		return mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
	}
}
