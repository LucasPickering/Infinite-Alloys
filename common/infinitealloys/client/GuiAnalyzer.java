package infinitealloys.client;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.WorldData;
import infinitealloys.inventory.ContainerAnalyzer;
import infinitealloys.item.Items;
import infinitealloys.tile.TileEntityAnalyzer;
import infinitealloys.util.Funcs;
import infinitealloys.util.Consts;
import java.awt.Rectangle;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiAnalyzer extends GuiMachine {

	private TileEntityAnalyzer tea;

	public GuiAnalyzer(InventoryPlayer inventoryPlayer, TileEntityAnalyzer tileEntity) {
		super(216, 166, tileEntity, new ContainerAnalyzer(inventoryPlayer, tileEntity), "analyzer");
		tea = tileEntity;
		progressBar.setLocation(54, 57);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(Items.ingot, 1, i), i * 18 + 27, 8);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		bindTexture("extras");
		if(tea.inventoryStacks[1] != null) {
			int currentAlloy = tea.inventoryStacks[1].getTagCompound().getInteger("alloy");
			WorldData worldData = InfiniteAlloys.instance.worldData;
			for(int i = 0; i < Consts.METAL_COUNT; i++) {
				int currentValue = Funcs.intAtPos(10, Consts.METAL_COUNT, currentAlloy, i);
				int nextValue = Funcs.intAtPos(10, Consts.METAL_COUNT, worldData.getValidAlloys()[worldData.alloysUnlocked], i);
				Rectangle symbol = nextValue > currentValue ? DOWN_ARROW : nextValue < currentValue ? UP_ARROW : CHECK;
				drawTexturedModalRect((Consts.VALID_ALLOY_COUNT - i) * 18 + 45, 26, symbol.x, symbol.y, symbol.width, symbol.height);
			}
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
