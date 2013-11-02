package infinitealloys.client.gui;

import infinitealloys.tile.TileEntityMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiMachineTab extends GuiScreen {

	private RenderItem itemRenderer;
	public TileEntityMachine tem;
	public int xPos, yPos;
	private boolean leftSide;
	private boolean activated;

	public GuiMachineTab(Minecraft mc, RenderItem itemRenderer, int xPos, int yPos, TileEntityMachine tem, boolean leftSide, boolean activated) {
		this.mc = mc;
		this.itemRenderer = itemRenderer;
		this.xPos = xPos;
		this.yPos = yPos;
		this.tem = tem;
		this.leftSide = leftSide;
		this.activated = activated;
		width = 20;
		height = 20;
	}

	/** Draws this button to the screen. */
	public void drawButton() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiMachine.bindTexture(GuiUpgradable.extras);
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
		itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(tem.getBlockType().blockID, 1, tem.getBlockMetadata()), xPos + 5, yPos + 4);
	}

	public boolean mousePressed(int mouseX, int mouseY) {
		return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
	}
}