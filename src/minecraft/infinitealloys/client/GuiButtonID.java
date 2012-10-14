package infinitealloys.client;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiButtonID extends GuiButton {

	public GuiButtonID(int id, int x, int y, String text) {
		super(id, x, y, 20, 20, text);
	}

	public boolean isMouseOver(int mouseX,int mouseY) {
		return mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
	}
}
