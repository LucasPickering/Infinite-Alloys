package infinitealloys.client;

import java.util.ArrayList;
import infinitealloys.ContainerAnalyzer;
import infinitealloys.References;
import infinitealloys.TileEntityAnalyzer;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.InventoryPlayer;

public class GuiAnalyzer extends GuiMachine {

	private TileEntityAnalyzer tea;

	public GuiAnalyzer(InventoryPlayer inventoryPlayer, TileEntityAnalyzer tileEntity) {
		super(tileEntity, new ContainerAnalyzer(inventoryPlayer, tileEntity));
		tea = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for(int i = 0; i < tea.alloyReport.length(); i += 24) {
			int lastSpace = 0;
			for(int j = i; j < i + 24; j++)
				if(tea.alloyReport.charAt(j) == ' ')
					lastSpace = i;
			System.out.println(tea.alloyReport.substring(i, lastSpace).trim());
			fontRenderer.drawString(tea.alloyReport.substring(i, lastSpace).trim(), 14, 0, 4210752);
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int k = mc.renderEngine.getTexture(References.TEXTURE_PATH + "gui/analyzer.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(k);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		drawTexturedModalRect(left + 34, top + 57, 0, 166, tea.getAnalysisProgressScaled(106) + 1, 18);
	}
}
