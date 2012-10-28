package infinitealloys.client;

import infinitealloys.IAValues;
import infinitealloys.handlers.PacketHandler;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;

public class GuiAddMachine extends GuiScreen {

	private int xPos, yPos;
	private GuiComputer compGui;
	private GuiTextField xInput, yInput, zInput;
	private GuiButton enter;

	public GuiAddMachine(Minecraft mc, GuiComputer compGui, int xPos, int yPos) {
		this.mc = mc;
		this.compGui = compGui;
		this.xPos = xPos;
		this.yPos = yPos;
		width = 148;
		height = 32;
		xInput = new GuiTextField(mc.fontRenderer, xPos + 8, yPos + 8, 24, 16);
		yInput = new GuiTextField(mc.fontRenderer, xPos + 40, yPos + 8, 24, 16);
		zInput = new GuiTextField(mc.fontRenderer, xPos + 72, yPos + 8, 24, 16);
		enter = new GuiButton(0, xPos + 104, yPos + 6, 36, 20, "Enter");
	}

	public void drawGui(int mouseX, int mouseY) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture(IAValues.TEXTURE_PATH + "guicomputer.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(xPos, yPos, 0, 150, width, height);
		xInput.drawTextBox();
		yInput.drawTextBox();
		zInput.drawTextBox();
		enter.drawButton(mc, mouseX, mouseY);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		xInput.mouseClicked(mouseX, mouseY, mouseButton);
		yInput.mouseClicked(mouseX, mouseY, mouseButton);
		zInput.mouseClicked(mouseX, mouseY, mouseButton);
		if(enter.mousePressed(mc, mouseX, mouseY)) {
			mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
			try {
				int x = new Integer(xInput.getText());
				int y = new Integer(yInput.getText());
				int z = new Integer(zInput.getText());
				compGui.tec.addMachine(mc.thePlayer, x, y, z);
				PacketDispatcher.sendPacketToServer(PacketHandler.getComputerPacketAddMachine(compGui.tec.xCoord, compGui.tec.yCoord, compGui.tec.zCoord, x, y, z));
				compGui.drawAddGui = false;
				compGui.addGui = null;
			}
			catch(NumberFormatException e) {
				mc.thePlayer.addChatMessage("Error: Coords must be numbers");
			}
		}
	}

	@Override
	public void keyTyped(char key, int eventKey) {
		if(eventKey == 1) {
			compGui.drawAddGui = false;
			compGui.addGui = null;
			return;
		}
		xInput.textboxKeyTyped(key, eventKey);
		yInput.textboxKeyTyped(key, eventKey);
		zInput.textboxKeyTyped(key, eventKey);
	}
}
