package infinitealloys.client.gui;

import infinitealloys.handlers.PacketHandler;
import infinitealloys.item.Items;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiMetalForge extends GuiElectric {

	private final TEEMetalForge temf;

	public GuiMetalForge(InventoryPlayer inventoryPlayer, TEEMetalForge tileEntity) {
		super(176, 216, inventoryPlayer, tileEntity);
		temf = tileEntity;
		progressBar.setLocation(31, 14);
		energyIcon.setLocation(83, 123);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			if(mouseInZone(mouseX, mouseY, topLeft.x + i % 4 * 18 + 65, topLeft.y + i / 4 * 18 + 42, 18, 18))
				drawTextBox(mouseX, mouseY, new ColoredLine(Funcs.getLoc("metal." + Consts.METAL_NAMES[i] + ".name"), 0xffffff));
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if(temf.inventoryStacks[0] != null && temf.presetSelection > -1) {
			int[] alloys = temf.inventoryStacks[0].getTagCompound().getIntArray("alloys");
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(Items.alloyIngot, 1, temf.getDamageForAlloy(alloys[temf.presetSelection])), 40, 52);
		}
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(Items.ingot, 1, i), i % 4 * 18 + 66, i / 4 * 18 + 43);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			fontRenderer.drawStringWithShadow(new Byte(temf.recipeAmts[i]).toString(), i % 4 * 18 + 77, i / 4 * 18 + 52, 0xffffff);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		// If the preset selection slot was clicked
		if(mouseInZone(mouseX, mouseY, topLeft.x + 39, topLeft.y + 51, 18, 18)) {
			int[] alloys = new int[0];
			if(temf.inventoryStacks[0] != null && temf.inventoryStacks[0].hasTagCompound())
				alloys = temf.inventoryStacks[0].getTagCompound().getIntArray("alloys");
			if(mouseButton == 0)
				temf.presetSelection = (byte)Math.min(temf.presetSelection + 1, alloys.length - 1);
			else if(mouseButton == 1)
				temf.presetSelection = (byte)Math.max(temf.presetSelection - 1, -1);
			if(temf.presetSelection > -1)
				for(int i = 0; i < temf.recipeAmts.length; i++)
					temf.recipeAmts[i] = (byte)Funcs.intAtPos(alloys[temf.presetSelection], Consts.ALLOY_RADIX, Consts.METAL_COUNT, i);
			PacketDispatcher.sendPacketToServer(PacketHandler.getTEPacketToServer(temf));
		}

		if(temf.presetSelection == -1) {
			for(int i = 0; i < Consts.METAL_COUNT; i++) {
				if(mouseInZone(mouseX, mouseY, topLeft.x + i % 4 * 18 + 65, topLeft.y + i / 4 * 18 + 42, 18, 18)) {
					if(mouseButton == 0)
						temf.recipeAmts[i] = (byte)Math.min(temf.recipeAmts[i] + 1, Consts.ALLOY_RADIX - 1);
					else if(mouseButton == 1)
						temf.recipeAmts[i] = (byte)Math.max(temf.recipeAmts[i] - 1, 0);
					PacketDispatcher.sendPacketToServer(PacketHandler.getTEPacketToServer(temf));
					break;
				}
			}
		}
	}
}
