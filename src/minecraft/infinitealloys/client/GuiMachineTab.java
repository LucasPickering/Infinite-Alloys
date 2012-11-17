package infinitealloys.client;

import infinitealloys.Point;
import infinitealloys.References;
import infinitealloys.TileEntityMachine;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderItem;

public class GuiMachineTab extends GuiScreen {

	private RenderItem itemRenderer;
	public int xPos, yPos;
	public TileEntityMachine tem;
	private boolean leftSide;
	private boolean activated;

	public GuiMachineTab(RenderItem itemRenderer, int xPos, int yPos, TileEntityMachine tem, boolean leftSide, boolean activated) {
		this.itemRenderer = itemRenderer;
		this.xPos = xPos;
		this.yPos = yPos;
		this.tem = tem;
		this.leftSide = leftSide;
		this.activated = activated;
		width = 20;
		height = 20;
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft mc) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(References.TEXTURE_PATH + "gui/extras.png"));
		if(leftSide) {
			if(activated)
				drawTexturedModalRect(xPos, yPos, GuiMachine.TAB_LEFT_ON.x, GuiMachine.TAB_LEFT_ON.y, GuiMachine.TAB_LEFT_ON.width, GuiMachine.TAB_LEFT_ON.height);
			else
				drawTexturedModalRect(xPos, yPos, GuiMachine.TAB_LEFT_OFF.x, GuiMachine.TAB_LEFT_OFF.y, GuiMachine.TAB_LEFT_OFF.width, GuiMachine.TAB_LEFT_OFF.height);
		}
		else {
			if(activated)
				drawTexturedModalRect(xPos, yPos, GuiMachine.TAB_RIGHT_ON.x, GuiMachine.TAB_RIGHT_ON.y, GuiMachine.TAB_RIGHT_ON.width, GuiMachine.TAB_RIGHT_ON.height);
			else
				drawTexturedModalRect(xPos, yPos, GuiMachine.TAB_RIGHT_OFF.x, GuiMachine.TAB_RIGHT_OFF.y, GuiMachine.TAB_RIGHT_OFF.width, GuiMachine.TAB_RIGHT_OFF.height);
		}
		itemRenderer.func_82406_b(fontRenderer, mc.renderEngine, new ItemStack(mc.theWorld.getBlockId(tem.xCoord, tem.yCoord, tem.zCoord), 1, mc.theWorld.getBlockMetadata(tem.xCoord, tem.yCoord, tem.zCoord)), xPos + 5, yPos + 4);
	}

	public boolean mousePressed(int mouseX, int mouseY) {
		return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
	}
}