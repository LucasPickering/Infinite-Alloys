package infinitealloys.client;

import infinitealloys.TileEntityComputer;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.StatCollector;

public class GuiComputer extends GuiScreen {

	private TileEntityComputer tec;
	private GuiButton[] minusButtons;
	private GuiButton[] plusButtons;
	private int xSize = 150;
	private int ySize = 161;

	public GuiComputer(TileEntityComputer tileentity) {
		tec = tileentity;
		minusButtons = new GuiButton[tileentity.maxIdCount];
		plusButtons = new GuiButton[tileentity.maxIdCount];
	}

	@Override
	public void initGui() {
		controlList.clear();
		for(int i = 0; i < tec.maxIdCount; i++) {
			controlList.add(minusButtons[i] = new GuiButton(i * 2, width / 2 - 50 + (i / 5 * 58), height / 2 - 68 + (i % 5 * 28), 12, 20, "-"));
			controlList.add(plusButtons[i] = new GuiButton(i * 2 + 1, width / 2 - 20 + (i / 5 * 58), height / 2 - 68 + (i % 5 * 28), 12, 20, "+"));
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		int k = mc.renderEngine.getTexture("/infinitealloys/gfx/guicomputer.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(k);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		super.drawScreen(i, j, f);
		if(tec.init)
			for(int l = 0; l < tec.selectedIDs.length; l++)
				fontRenderer.drawString(new Integer(tec.selectedIDs[l]).toString(),left+ 44 + (l / 5 * 58), top+19 + (l % 5 * 28), 4210752);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void actionPerformed(GuiButton guibutton) {
		if(guibutton.enabled) {
			if(guibutton.id % 2 == 0)
				tec.selectedIDs[guibutton.id / 2] = Math.max(--tec.selectedIDs[guibutton.id / 2], 0);
			else if(guibutton.id % 2 == 1)
				tec.selectedIDs[guibutton.id / 2] = Math.min(++tec.selectedIDs[guibutton.id / 2], 100000);
			tec.updateNetwork();
		}
	}
}
