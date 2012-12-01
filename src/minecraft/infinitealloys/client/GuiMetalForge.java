package infinitealloys.client;

import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;
import infinitealloys.ContainerMetalForge;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.TileEntityMetalForge;
import infinitealloys.handlers.PacketHandler;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class GuiMetalForge extends GuiMachine {

	private TileEntityMetalForge temf;

	public GuiMetalForge(InventoryPlayer inventoryPlayer, TileEntityMetalForge tileEntity) {
		super(176, 216, tileEntity, new ContainerMetalForge(inventoryPlayer, tileEntity));
		temf = tileEntity;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for(int i = 0; i < References.metalCount; i++)
			if(mouseInZone(mouseX, mouseY, topLeft.x + i % 4 * 18 + 65, topLeft.y + i / 4 * 18 + 42, 18, 18))
				drawTextBox(InfiniteAlloys.getStringLocalization("metal." + References.metalNames[i] + ".name"), 0xffffff, mouseX, mouseY);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		bindTexture("extras");
		drawTexturedModalRect(31, 14, PROGRESS_BAR.x, PROGRESS_BAR.y, temf.getProcessProgressScaled(PROGRESS_BAR.width), PROGRESS_BAR.height);
		if(temf.inventoryStacks[0] != null && temf.presetSelection > -1) {
			int[] alloys = temf.inventoryStacks[0].getTagCompound().getIntArray("alloys");
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(InfiniteAlloys.alloyIngot, 1, temf.getDamageForAlloy(alloys[temf.presetSelection])), 40, 52);
		}
		for(int i = 0; i < References.metalCount; i++)
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(InfiniteAlloys.ingot, 1, i), i % 4 * 18 + 66, i / 4 * 18 + 43);
		for(int i = 0; i < References.metalCount; i++)
			fontRenderer.drawStringWithShadow(new Byte(temf.recipeAmts[i]).toString(), i % 4 * 18 + 77, i / 4 * 18 + 52, 0xffffff);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		bindTexture("metalforge");
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseInZone(mouseX, mouseY, topLeft.x + 39, topLeft.y + 51, 18, 18)) {
			int[] alloys = new int[0];
			if(temf.inventoryStacks[0] != null)
				alloys = temf.inventoryStacks[0].getTagCompound().getIntArray("alloys");
			if(mouseButton == 0)
				temf.presetSelection = (byte)Math.min(temf.presetSelection + 1, alloys.length - 1);
			else if(mouseButton == 1)
				temf.presetSelection = (byte)Math.max(temf.presetSelection - 1, -1);
			if(temf.presetSelection > -1)
				for(int i = 0; i < temf.recipeAmts.length; i++)
					temf.recipeAmts[i] = (byte)InfiniteAlloys.intAtPos(References.alloyRadix, References.metalCount, alloys[temf.presetSelection], References.metalCount - i - 1);
			PacketDispatcher.sendPacketToServer(PacketHandler.getTEPacketToServer(temf));
		}
		if(temf.presetSelection == -1) {
			for(int i = 0; i < References.metalCount; i++) {
				if(mouseInZone(mouseX, mouseY, topLeft.x + i % 4 * 18 + 65, topLeft.y + i / 4 * 18 + 42, 18, 18)) {
					if(mouseButton == 0)
						temf.recipeAmts[i] = (byte)Math.min(temf.recipeAmts[i] + 1, References.alloyRadix - 1);
					else if(mouseButton == 1)
						temf.recipeAmts[i] = (byte)Math.max(temf.recipeAmts[i] - 1, 0);
					PacketDispatcher.sendPacketToServer(PacketHandler.getTEPacketToServer(temf));
					break;
				}
			}
		}
	}
}
