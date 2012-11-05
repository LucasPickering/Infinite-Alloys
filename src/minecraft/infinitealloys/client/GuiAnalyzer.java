package infinitealloys.client;

import java.util.ArrayList;
import infinitealloys.ContainerAnalyzer;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.TileEntityAnalyzer;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.InventoryPlayer;

public class GuiAnalyzer extends GuiMachine {

	private TileEntityAnalyzer tea;

	public GuiAnalyzer(InventoryPlayer inventoryPlayer, TileEntityAnalyzer tileEntity) {
		super(tileEntity, new ContainerAnalyzer(inventoryPlayer, tileEntity));
		tea = tileEntity;
		xSize = 184;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(References.TEXTURE_PATH + "gui/analyzer.png"));
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		drawTexturedModalRect(left + 38, top + 57, 0, 166, tea.getAnalysisProgressScaled(106) + 1, 18);
		if(tea.inventoryStacks[1] != null) {
			int currentAlloy = tea.inventoryStacks[1].getTagCompound().getInteger("alloy");
			int nearestValidAlloy = 2147483647;
			for(int i = 0; i < References.validAlloyCount; i++)
				if(getAlloyDiff(currentAlloy, nearestValidAlloy) > getAlloyDiff(currentAlloy, InfiniteAlloys.instance.worldData.validAlloys[i]))
					nearestValidAlloy = InfiniteAlloys.instance.worldData.validAlloys[i];
			for(int i = 0; i < References.metalCount; i++) {
				int currentValue = InfiniteAlloys.intAtPosRadix(10, References.metalCount, currentAlloy, i);
				int nearestValue = InfiniteAlloys.intAtPosRadix(10, References.metalCount, nearestValidAlloy, i);
				drawTexturedModalRect(left + (References.validAlloyCount - i) * 18 + 26, top + 26, nearestValue > currentValue ? 184 : nearestValue < currentValue ? 200 : 216, 0, 16, 16);
			}
		}
	}

	private int getAlloyDiff(int alloy1, int alloy2) {
		int diff = 0;
		for(int i = 0; i < References.metalCount; i++)
			diff += Math.abs(InfiniteAlloys.intAtPosRadix(10, References.metalCount, alloy1, i) - InfiniteAlloys.intAtPosRadix(10, References.metalCount, alloy2, i));
		return diff;
	}
}
