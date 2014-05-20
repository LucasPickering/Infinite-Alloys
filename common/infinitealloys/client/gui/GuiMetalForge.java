package infinitealloys.client.gui;

import infinitealloys.item.Items;
import infinitealloys.network.PacketTEClientToServer;
import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.Funcs;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiMetalForge extends GuiElectric {

	private final TEEMetalForge temf;

	public GuiMetalForge(InventoryPlayer inventoryPlayer, TEEMetalForge tileEntity) {
		super(176, 216, inventoryPlayer, tileEntity);
		temf = tileEntity;
		progressBar.setLocation(31, 14);
		networkIcon = new java.awt.Point(9, 15);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			if(Funcs.mouseInZone(mouseX, mouseY, topLeft.x + i % 4 * 18 + 65, topLeft.y + i / 4 * 18 + 42, 18, 18))
				drawTextBox(mouseX, mouseY, new ColoredLine(Funcs.getLoc("metal." + Consts.METAL_NAMES[i] + ".name"), 0xffffff));
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// If there is an alloy available and selected
		if(temf.recipeAlloyID >= 0)
			// Render the item icon for the alloy. The itemstack for a valid alloy is obtained by setting the damage to one more than the alloy's ID.
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(Items.alloyIngot, 1, temf.recipeAlloyID + 1), 40, 52);

		for(int i = 0; i < Consts.METAL_COUNT; i++)
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(Items.ingot, 1, i), i % 4 * 18 + 66, i / 4 * 18 + 43);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			fontRenderer.drawStringWithShadow((temf.recipeAlloyID < 0 ? 0 : EnumAlloy.getMetalAmt(temf.recipeAlloyID, i)) + "", i % 4 * 18 + 77, i / 4 * 18 + 52, 0xffffff);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		// If the preset selection slot was clicked, adjust its value accordingly
		if(temf.getAnalyzerHost() != null && Funcs.mouseInZone(mouseX, mouseY, topLeft.x + 39, topLeft.y + 51, 18, 18)) {
			if(mouseButton == 0) { // Left-click
				// Iterate over each alloy with an index greater than the current one
				for(int i = temf.recipeAlloyID + 1; i < Consts.VALID_ALLOY_COUNT; i++)
					if(((TEEAnalyzer)Funcs.getBlockTileEntity(mc.theWorld, temf.getAnalyzerHost())).hasAlloy(i))
						temf.recipeAlloyID = i; // If this alloy has been discovered, select it
			}

			else if(mouseButton == 1) { // Right-click
				// Iterate over each alloy with an index less than the current one
				for(int i = temf.recipeAlloyID - 1; i >= 0; i--)
					if(((TEEAnalyzer)Funcs.getBlockTileEntity(mc.theWorld, temf.getAnalyzerHost())).hasAlloy(i))
						temf.recipeAlloyID = (byte)i; // If this alloy has been discovered, select it
			}

			PacketDispatcher.sendPacketToServer(PacketTEClientToServer.getPacket(temf)); // Sync the new recipe to the server
		}
	}

	@Override
	protected ColoredLine[] getNetworkStatuses() {
		int color;
		String status;

		if(temf.getAnalyzerHost() == null) {
			color = 0xff0000;
			status = Funcs.getLoc("machine.network.noconnection");
		}
		else {
			color = 0x00ff00;
			status = Funcs.getLoc("machine.network.hostedby") + " " + temf.getAnalyzerHost();
		}

		return ArrayUtils.addAll(super.getNetworkStatuses(), new ColoredLine(Funcs.getLoc("machine.network.analyzer") + ": " + status, color));
	}
}
