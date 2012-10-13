package infinitealloys.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import infinitealloys.ContainerMetalForge;
import infinitealloys.IAValues;
import infinitealloys.TileEntityMetalForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Slot;

public class GuiMetalForge extends GuiContainer {

	private TileEntityMetalForge temf;
	private GuiButton idMinus;
	private GuiButton idPlus;

	public GuiMetalForge(InventoryPlayer inventoryPlayer, TileEntityMetalForge tileEntity) {
		super(new ContainerMetalForge(inventoryPlayer, tileEntity));
		xSize = 176;
		ySize = 216;
		temf = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		controlList.clear();
		controlList.add(idMinus = new GuiButton(0, width / 2 + 57, height / 2 - 102, 20, 20, "+"));
		controlList.add(idPlus = new GuiButton(1, width / 2 + 18, height / 2 - 102, 20, 20, "-"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		for(int i = 0; i < IAValues.metalCount; i++) {
			Slot slot = inventorySlots.getSlot(i + 1);
			if(func_74188_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY) && 0 <= i && i <= 8)
				drawTextBox(IAValues.metalNames[i], mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		fontRenderer.drawString("Inventory", 42, ySize - 94, 4210752);
		fontRenderer.drawString(new Integer(temf.networkID).toString(), 133, ySize - 204, 4210752);
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 3; x++)
				fontRenderer.drawStringWithShadow(new Byte(temf.recipeAmts[y * 3 + x]).toString(), x * 18 + 55, y * 18 + 25, 0xffffff);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int k = mc.renderEngine.getTexture("/infinitealloys/gfx/guimetalforge.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(k);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		int l;
		if(temf.heatLeft > 0) {
			l = temf.getBurnTimeRemainingScaled(12);
			drawTexturedModalRect(left + 16, top + 30 - l, 176, 12 - l, 14, l + 2);
		}
		l = temf.getCookProgressScaled(24);
		drawTexturedModalRect(left + 104, top + 34, 176, 14, l + 1, 16);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if(guibutton.enabled) {
			if(guibutton.id == 0)
				temf.networkID = (byte)Math.min(temf.networkID + 1, Byte.MAX_VALUE);
			if(guibutton.id == 1)
				temf.networkID = (byte)Math.max(temf.networkID - 1, 0);
		}
	}

	private void drawTextBox(String text, int mouseX, int mouseY) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(text);
		drawTextBox(list, mouseX, mouseY);
	}

	private void drawTextBox(List<String> text, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		if(!text.isEmpty()) {
			int var5 = 0;
			Iterator var6 = text.iterator();
			while(var6.hasNext()) {
				String var7 = (String)var6.next();
				int var8 = this.fontRenderer.getStringWidth(var7);
				if(var8 > var5)
					var5 = var8;
			}
			mouseX += 12;
			mouseY -= 12;
			int var9 = 8;
			if(text.size() > 1)
				var9 += 2 + (text.size() - 1) * 10;
			zLevel = 300.0F;
			itemRenderer.zLevel = 300.0F;
			int var10 = -267386864;
			drawGradientRect(mouseX - 3, mouseY - 4, mouseX + var5 + 3, mouseY - 3, var10, var10);
			drawGradientRect(mouseX - 3, mouseY + var9 + 3, mouseX + var5 + 3, mouseY + var9 + 4, var10, var10);
			drawGradientRect(mouseX - 3, mouseY - 3, mouseX + var5 + 3, mouseY + var9 + 3, var10, var10);
			drawGradientRect(mouseX - 4, mouseY - 3, mouseX - 3, mouseY + var9 + 3, var10, var10);
			drawGradientRect(mouseX + var5 + 3, mouseY - 3, mouseX + var5 + 4, mouseY + var9 + 3, var10, var10);
			int var11 = 1347420415;
			int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
			drawGradientRect(mouseX - 3, mouseY - 3 + 1, mouseX - 3 + 1, mouseY + var9 + 3 - 1, var11, var12);
			drawGradientRect(mouseX + var5 + 2, mouseY - 3 + 1, mouseX + var5 + 3, mouseY + var9 + 3 - 1, var11, var12);
			drawGradientRect(mouseX - 3, mouseY - 3, mouseX + var5 + 3, mouseY - 3 + 1, var11, var11);
			drawGradientRect(mouseX - 3, mouseY + var9 + 2, mouseX + var5 + 3, mouseY + var9 + 3, var12, var12);
			for(int var13 = 0; var13 < text.size(); ++var13) {
				String var14 = (String)text.get(var13);
				var14 = "\u00a77" + var14;
				fontRenderer.drawStringWithShadow(var14, mouseX, mouseY, -1);
				if(var13 == 0)
					mouseY += 2;
				mouseY += 10;
			}
			zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
		}
		GL11.glPopMatrix();
	}
}
