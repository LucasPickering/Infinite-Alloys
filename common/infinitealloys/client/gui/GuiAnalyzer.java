package infinitealloys.client.gui;

import infinitealloys.item.Items;
import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.Funcs;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiAnalyzer extends GuiElectric {

	private final TEEAnalyzer tea;

	/** The number for the selected recipe, from 0 to {@link infinitealloys.util.Consts#VALID_ALLOY_COUNT Consts.VALID_ALLOY_COUNT} */
	private int selectedRecipe;

	private final boolean requiredMetals[] = new boolean[Consts.METAL_COUNT];

	public GuiAnalyzer(InventoryPlayer inventoryPlayer, TEEAnalyzer tileEntity) {
		super(176, 166, inventoryPlayer, tileEntity);
		tea = tileEntity;
		progressBar.setLocation(28, 7);
		energyIcon.setLocation(7, 8);
		updateRequiredMetals();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		for(int i = 0; i < Consts.METAL_COUNT; i++) {
			if(!requiredMetals[i])
				break;
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(Items.ingot, 1, i), i * 18 + 17, 33);
			if(selectedRecipe > 0)
				fontRenderer.drawStringWithShadow(new Integer(Funcs.intAtPos(Funcs.getValidAlloys()[selectedRecipe - 1], Consts.ALLOY_RADIX, i)).toString(),
						i * 18 + 28, 42, 0xffffff);
		}

		fontRenderer.drawStringWithShadow(new Integer(selectedRecipe).toString(), 7, 37, 0xffffff);

		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		// Was the left mouse button clicked and are the buttons being drawn?
		if(mouseButton == 0 && tea.getUnlockedAlloyCount() > 0) {
			// Was the up button clicked and is the selection not already maxed?
			if(Funcs.mouseInZone(mouseX, mouseY, topLeft.x + 4, topLeft.y + 28, 14, 8) && selectedRecipe < tea.getUnlockedAlloyCount())
				// Increase the selection
				selectedRecipe++;
			// Was the down button clicked and is the selection not already one?
			if(Funcs.mouseInZone(mouseX, mouseY, topLeft.x + 4, topLeft.y + 47, 14, 8) && selectedRecipe > 0)
				// Decrease the selection
				selectedRecipe--;
			updateRequiredMetals();
		}
	}

	private void updateRequiredMetals() {
		for(int i = 0; i < requiredMetals.length; i++) {
			if(selectedRecipe == 0)
				requiredMetals[i] = Funcs.intAtPos(EnumAlloy.values()[tea.getUnlockedAlloyCount()].max, Consts.ALLOY_RADIX, i) > 0;
			else
				requiredMetals[i] = Funcs.intAtPos(EnumAlloy.values()[selectedRecipe - 1].max, Consts.ALLOY_RADIX, i) > 0;
		}
	}
}
