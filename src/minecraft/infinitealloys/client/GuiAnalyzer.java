package infinitealloys.client;

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
		drawTexturedModalRect(topLeft.x + 38, topLeft.y + 57, 0, 166, tea.getProcessProgressScaled(106) + 1, 18);
		if(tea.inventoryStacks[1] != null) {
			int currentAlloy = tea.inventoryStacks[1].getTagCompound().getInteger("alloy");
			int nearestValidAlloy = Integer.MAX_VALUE;
			for(int i = 0; i < References.validAlloyCount; i++)
				if(getAlloyDiff(currentAlloy, nearestValidAlloy) > getAlloyDiff(currentAlloy, InfiniteAlloys.instance.worldData.validAlloys[i]))
					nearestValidAlloy = InfiniteAlloys.instance.worldData.validAlloys[i];
			for(int i = 0; i < References.metalCount; i++) {
				int currentValue = InfiniteAlloys.intAtPosRadix(10, References.metalCount, currentAlloy, i);
				int nearestValue = InfiniteAlloys.intAtPosRadix(10, References.metalCount, nearestValidAlloy, i);
				drawTexturedModalRect(topLeft.x + (References.validAlloyCount - i) * 18 + 26, topLeft.y + 26, nearestValue > currentValue ? 184 : nearestValue < currentValue ? 200 : 216, 0, 16, 16);
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
