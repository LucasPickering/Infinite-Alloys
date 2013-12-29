package infinitealloys.client.gui;

import infinitealloys.block.Blocks;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiInternetWand extends GuiScreen {

	private final RenderItem itemRenderer = new RenderItem();
	private final ResourceLocation background;
	private final int xSize = 196;
	private final int ySize = 240;

	/** If the GUI was opened by clicking on a machine, this button adds the machine that was clicked to the wand */
	private GuiButton addToWand;
	/** If the GUI was opened by clicking on a machine, this button adds the selected machine to the machine that was clicked */
	private GuiButton addSelected;

	private MachineButton[] machineButtons = new MachineButton[Consts.WAND_MAX_COORDS];
	private boolean initialized;

	public GuiInternetWand() {
		background = Funcs.getGuiTexture("wand");
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, width - 20, 0, 20, 20, "?")); // The button to enable/disable help
		buttonList.add(addToWand = new GuiButton(1, width / 2 - 91, height / 2 - 112, 80, 20, Funcs.getLoc("wand.addToWand")));
		buttonList.add(addSelected = new GuiButton(2, width / 2 + 11, height / 2 - 112, 80, 20, Funcs.getLoc("wand.addSelected")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		if(!initialized) {
			initialized = true;
			setButtons();
		}
		Funcs.bindTexture(background);
		drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
		super.drawScreen(mouseX, mouseY, partialTick);
		for(MachineButton button : machineButtons)
			if(button != null)
				button.drawButton();
	}

	private void setButtons() {
		ItemStack wandItem = mc.thePlayer.getHeldItem();
		if(wandItem.hasTagCompound()) {
			for(int i = 0; i < Consts.WAND_MAX_COORDS; i++) {
				if(wandItem.getTagCompound().hasKey("Coords" + i)) {
					int x = wandItem.getTagCompound().getIntArray("Coords" + i)[0];
					int y = wandItem.getTagCompound().getIntArray("Coords" + i)[1];
					int z = wandItem.getTagCompound().getIntArray("Coords" + i)[2];
					if(mc.theWorld.getBlockId(x, y, z) == Blocks.machineID)
						machineButtons[i] = new MachineButton(150, i * 20 + 100, mc.theWorld.getBlockMetadata(x, y, z), x, y, z);
				}
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	/** A button that represents a machine with its texture and coordinates */
	private class MachineButton {

		int xPos;
		int yPos;
		boolean selected;

		int machineID;
		int machineX;
		int machineY;
		int machineZ;

		MachineButton(int xPos, int yPos, int machineID, int machineX, int machineY, int machineZ) {
			this.xPos = xPos;
			this.yPos = yPos;
			this.machineID = machineID;
			this.machineX = machineX;
			this.machineY = machineY;
			this.machineZ = machineZ;
		}

		void drawButton() {
			Funcs.bindTexture(GuiMachine.extras);

			// If this button is selected, draw an overlay to indicate that
			if(selected)
				drawTexturedModalRect(xPos - 1, yPos - 1, GuiMachine.SELECTED_OVERLAY.x, GuiMachine.SELECTED_OVERLAY.y,
						GuiMachine.SELECTED_OVERLAY.width, GuiMachine.SELECTED_OVERLAY.height);

			// Draw the string for the coordinates
			String display = machineX + ", " + machineY + ", " + machineZ;
			mc.fontRenderer.drawStringWithShadow(display, xPos + 90 - (mc.fontRenderer.getStringWidth(display) / 2), yPos + 5, 0xffffff);

			itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(Blocks.machineID, 1, machineID), xPos + 18, yPos);
		}
	}
}
