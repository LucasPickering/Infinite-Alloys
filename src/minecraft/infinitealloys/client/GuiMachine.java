package infinitealloys.client;

import infinitealloys.IAValues;
import infinitealloys.InfiniteAlloys;
import infinitealloys.TileEntityMachine;
import infinitealloys.handlers.PacketHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.src.Container;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class GuiMachine extends GuiContainer {

	protected TileEntityMachine tem;
	protected GuiButtonID idButton;

	public GuiMachine(TileEntityMachine tileEntity, Container container) {
		super(container);
		tem = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		controlList.clear();
		controlList.add(idButton = new GuiButtonID(0, width / 2 + 61, height / 2 - 102, new Byte(tem.networkID).toString()));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		GL11.glDisable(GL11.GL_LIGHTING);
		Slot slot = inventorySlots.getSlot(tem.upgradeSlotIndex);
		if(idButton.isMouseOver(mouseX, mouseY))
			drawTextBox("Network ID", 0xffffff, mouseX, mouseY);
		if(func_74188_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY)) {
			ArrayList<String> texts = new ArrayList<String>();
			ArrayList<Integer> colors = new ArrayList<Integer>();
			texts.add("Upgrades");
			colors.add(0xffffff);
			for(int i = 0; i < IAValues.upgradeCount; i++) {
				int damage = (int)Math.pow(2, i);
				if(tem.isPrereqUpgrade(new ItemStack(InfiniteAlloys.upgrade, 1, damage)) && (tem.upgrades << 1 | damage) == tem.upgrades || (tem.upgrades | damage) != tem.upgrades) continue;
				texts.add(IAValues.upgradeNames[i]);
				colors.add(0xaaaaaa);
			}
			drawTextBox(texts, colors, mouseX, mouseY);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(idButton.mousePressed(mc, mouseX, mouseY)) {
			if(mouseButton == 0)
				tem.networkID = (byte)Math.min(tem.networkID + 1, Byte.MAX_VALUE);
			else if(mouseButton == 1) {
				tem.networkID = (byte)Math.max(tem.networkID - 1, 0);
				mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
			}
			PacketDispatcher.sendPacketToServer(PacketHandler.getPacketToServer(tem));
			idButton.displayString = new Byte(tem.networkID).toString();
		}
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
}
