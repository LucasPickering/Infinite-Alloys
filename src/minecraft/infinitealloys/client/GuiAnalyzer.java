package infinitealloys.client;

import java.awt.Rectangle;
import infinitealloys.ContainerAnalyzer;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.TileEntityAnalyzer;
import net.minecraft.src.InventoryPlayer;

public class GuiAnalyzer extends GuiMachine {

	private TileEntityAnalyzer tea;

	public GuiAnalyzer(InventoryPlayer inventoryPlayer, TileEntityAnalyzer tileEntity) {
		super(184, 166, tileEntity, new ContainerAnalyzer(inventoryPlayer, tileEntity));
		tea = tileEntity;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		bindTexture("analyzer");
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
		bindTexture("extras");
		drawTexturedModalRect(topLeft.x + 38, topLeft.y + 57, PROGRESS_BAR.x, PROGRESS_BAR.y, tea.getProcessProgressScaled(PROGRESS_BAR.width), PROGRESS_BAR.height);
		if(tea.inventoryStacks[1] != null) {
			int currentAlloy = tea.inventoryStacks[1].getTagCompound().getInteger("alloy");
			int nearestValidAlloy = Integer.MAX_VALUE;
			for(int i = 0; i < References.validAlloyCount; i++)
				if(getAlloyDiff(currentAlloy, nearestValidAlloy) > getAlloyDiff(currentAlloy, InfiniteAlloys.instance.worldData.getValidAlloys()[i]))
					nearestValidAlloy = InfiniteAlloys.instance.worldData.getValidAlloys()[i];
			for(int i = 0; i < References.metalCount; i++) {
				int currentValue = InfiniteAlloys.intAtPos(10, References.metalCount, currentAlloy, i);
				int nearestValue = InfiniteAlloys.intAtPos(10, References.metalCount, nearestValidAlloy, i);
				Rectangle symbol = nearestValue > currentValue ? UP_ARROW : nearestValue < currentValue ? DOWN_ARROW : CHECK;
				drawTexturedModalRect(topLeft.x + (References.validAlloyCount - i) * 18 + 26, topLeft.y + 26, symbol.x, symbol.y, symbol.width, symbol.height);
			}
		}
	}

	private int getAlloyDiff(int alloy1, int alloy2) {
		int diff = 0;
		for(int i = 0; i < References.metalCount; i++)
			diff += Math.abs(InfiniteAlloys.intAtPos(10, References.metalCount, alloy1, i) - InfiniteAlloys.intAtPos(10, References.metalCount, alloy2, i));
		return diff;
	}
}
