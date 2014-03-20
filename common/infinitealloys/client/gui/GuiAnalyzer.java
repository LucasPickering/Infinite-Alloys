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

	public GuiAnalyzer(InventoryPlayer inventoryPlayer, TEEAnalyzer tileEntity) {
		super(176, 166, inventoryPlayer, tileEntity);
		tea = tileEntity;
		progressBar.setLocation(28, 7);
		energyIcon.setLocation(7, 8);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		if(tea.getAlloys() != 0) { // If an alloy is available
			for(int i = 0; i < Consts.METAL_COUNT; i++) { // For each metal
				int amt = Funcs.intAtPos(EnumAlloy.getAlloyForID(selectedRecipe), Consts.ALLOY_RADIX, i); // The amount of this metal the the currently
				if(amt > 0) { // If this metal is in this alloy
					itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(Items.ingot, 1, i), i * 18 + 26, 33); // Draw the metal ingot
					GL11.glDisable(GL11.GL_LIGHTING);
					fontRenderer.drawStringWithShadow(Integer.toString(amt), i * 18 + 37, 42, 0xffffff); // Draw the amount of the metal required
				}
			}

			ItemStack alloy = new ItemStack(Items.alloyIngot, 1, selectedRecipe + 1);
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, alloy, 6, 33); // Draw the alloy that it creates
			fontRenderer.drawStringWithShadow(Funcs.getLoc(alloy.getItem().getUnlocalizedName(alloy)), 28, 42, 0xffffff); // Draw the name of the alloy
		}

		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		// Was the left mouse button clicked and are the buttons being drawn?
		if(tea.getAlloys() != 0) {
			// Was the ingot clicked?
			if(Funcs.mouseInZone(mouseX, mouseY, topLeft.x + 6, topLeft.y + 35, 16, 16)) {
				if(mouseButton == 0) { // Left-click
					// Iterate over each alloy with an index greater than the current one
					for(int i = selectedRecipe + 1; i < Consts.VALID_ALLOY_COUNT; i++)
						if(tea.hasAlloy(i))
							selectedRecipe = i; // If this alloy has been discovered, select it
				}

				else if(mouseButton == 1) { // Right-click
					// Iterate over each alloy with an index less than the current one
					for(int i = selectedRecipe - 1; i >= 0; i--)
						if(tea.hasAlloy(i))
							selectedRecipe = i; // If this alloy has been discovered, select it
				}
			}
		}
	}
}
