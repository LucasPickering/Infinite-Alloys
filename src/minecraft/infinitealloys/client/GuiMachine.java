package infinitealloys.client;

import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.TileEntityMachine;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.src.Container;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public abstract class GuiMachine extends GuiContainer {

	public static Rectangle ENERGY_METER = new Rectangle(0, 0, 10, 32);
	public static Rectangle COMP_MACH_BG = new Rectangle(10, 0, 20, 20);
	public static Rectangle PROGRESS_ARROW = new Rectangle(30, 0, 24, 17);

	protected Point topLeft = new Point();
	protected Point energyTopLeft = new Point();
	protected TileEntityMachine tem;

	public GuiMachine(int xSize, int ySize, TileEntityMachine tileEntity, Container container) {
		super(container);
		this.xSize = xSize;
		this.ySize = ySize;
		tem = tileEntity;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		topLeft.setLocation((width - xSize) / 2, (height - ySize) / 2);
		energyTopLeft.setLocation(topLeft.x + 10, topLeft.y + 10);
		super.drawScreen(mouseX, mouseY, partialTick);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture("extras");
		drawTexturedModalRect(energyTopLeft.x, energyTopLeft.y, ENERGY_METER.x, ENERGY_METER.y, ENERGY_METER.width, tem.getJoulesScaled(ENERGY_METER.height));
		Slot slot = inventorySlots.getSlot(tem.upgradeSlotIndex);
		if(func_74188_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY)) {
			ArrayList<String> texts = new ArrayList<String>();
			ArrayList<Integer> colors = new ArrayList<Integer>();
			texts.add(InfiniteAlloys.getStringLocalization("upgrade.name"));
			colors.add(0xffffff);
			for(int i = 0; i < References.upgradeCount; i++) {
				int damage = (int)Math.pow(2, i);
				if(tem.isPrereqUpgrade(new ItemStack(InfiniteAlloys.upgrade, 1, damage)) && tem.hasUpgrade(damage >> 1) || !tem.hasUpgrade(damage))
					continue;
				texts.add(InfiniteAlloys.getStringLocalization("upgrade." + References.upgradeNames[i] + ".name"));
				colors.add(0xaaaaaa);
			}
			drawTextBox(texts, colors, mouseX, mouseY);
		}
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	protected void drawTextBox(String text, int color, int mouseX, int mouseY) {
		ArrayList<String> texts = new ArrayList<String>();
		ArrayList<Integer> colors = new ArrayList<Integer>();
		texts.add(text);
		colors.add(color);
		drawTextBox(texts, colors, mouseX, mouseY);
	}

	protected void drawTextBox(List<String> text, List<Integer> colors, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
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
			for(int i = 0; i < text.size(); i++) {
				fontRenderer.drawStringWithShadow(text.get(i), mouseX, mouseY, colors.get(i));
				if(i == 0)
					mouseY += 2;
				mouseY += 10;
			}
			zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
		}
		GL11.glPopMatrix();
	}

	protected void bindTexture(String texture) {
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(References.TEXTURE_PATH + "gui/" + texture + ".png"));
	}
}
