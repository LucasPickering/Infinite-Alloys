package infinitealloys.client;

import java.awt.Rectangle;
import org.lwjgl.opengl.GL11;
import infinitealloys.ContainerAnalyzer;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.TileEntityAnalyzer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;

public class GuiAnalyzer extends GuiMachine {

	private TileEntityAnalyzer tea;

	public GuiAnalyzer(InventoryPlayer inventoryPlayer, TileEntityAnalyzer tileEntity) {
		super(216, 166, tileEntity, new ContainerAnalyzer(inventoryPlayer, tileEntity));
		tea = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		bindTexture("extras");
		drawTexturedModalRect(54, 57, PROGRESS_BAR.x, PROGRESS_BAR.y, tea.getProcessProgressScaled(PROGRESS_BAR.width), PROGRESS_BAR.height);
		for(int i = 0; i < References.metalCount; i++)
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(InfiniteAlloys.ingot, 1, i), i * 18 + 27, 8);
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
				drawTexturedModalRect((References.validAlloyCount - i) * 18 + 45, 26, symbol.x, symbol.y, symbol.width, symbol.height);
			}
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		bindTexture("analyzer");
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
	}

	private int getAlloyDiff(int alloy1, int alloy2) {
		int diff = 0;
		for(int i = 0; i < References.metalCount; i++)
			diff += Math.pow(Math.abs(InfiniteAlloys.intAtPos(10, References.metalCount, alloy1, i) - InfiniteAlloys.intAtPos(10, References.metalCount, alloy2, i)), References.alloyRadix);
		return diff;
	}
}
