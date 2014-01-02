package infinitealloys.client.gui;

import org.lwjgl.opengl.GL11;
import infinitealloys.block.Blocks;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.network.PacketAddToWand;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TEMEnergyStorage;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiInternetWand extends GuiScreen {

	private final RenderItem itemRenderer = new RenderItem();
	private final ResourceLocation background;
	private final int xSize = 178;
	private final int ySize = 236;

	/** If the GUI was opened by clicking on a machine, this button adds the machine that was clicked to the wand */
	private GuiButton addToWand;

	/** If the GUI was opened by clicking on a machine, this button adds the selected machine to the machine that was clicked */
	private GuiButton addSelected;

	/** The array of buttons that applies to each machine. It is a fixed length and buttons that do not exist are null */
	private MachineButton[] machineButtons = new MachineButton[Consts.WAND_MAX_COORDS];

	/** When help is enabled, slots get a colored outline and a mouse-over description */
	private boolean helpEnabled;

	/** Binary integer to represent the buttons that are selected. Right-most digit is the top of the list.
	 * There is one bit for each button. 0 is not-selected. 1 is selected. */
	private int selectedButtons;

	public GuiInternetWand() {
		background = Funcs.getGuiTexture("wand");
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, width - 20, 0, 20, 20, "?")); // The button to enable/disable help
		buttonList.add(addToWand = new GuiButton(1, width / 2 - 83, height / 2 - 112, 70, 20, Funcs.getLoc("wand.addToWand")));
		buttonList.add(addSelected = new GuiButton(2, width / 2 - 10, height / 2 - 112, 70, 20, Funcs.getLoc("wand.addSelected")));
		setButtons();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		Funcs.bindTexture(background);
		drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
		super.drawScreen(mouseX, mouseY, partialTick);
		for(MachineButton button : machineButtons)
			if(button != null)
				button.drawButton();
	}

	private void setButtons() {
		addToWand.enabled = false; // Reset button states
		addSelected.enabled = false;
		ItemStack heldItem = mc.thePlayer.getHeldItem();
		if(heldItem.getItem() instanceof ItemInternetWand && heldItem.hasTagCompound()) {
			NBTTagCompound tagCompound = heldItem.getTagCompound();

			for(int i = 0; i < Consts.WAND_MAX_COORDS; i++) { // For each button in the array
				machineButtons[i] = null; // Reset the button
				if(tagCompound.hasKey("Coords" + i)) { // If there is a machine that corresponds to this button
					int[] a = tagCompound.getIntArray("Coords" + i); // Variables for this machine's data
					machineButtons[i] = new MachineButton(i, width / 2 - 82, height / 2 + i * 20 - 86, a[0], a[1], a[2], a[3]); // Create a button
				}
			}

			if(tagCompound.hasKey("CoordsCurrent")) {
				int[] a = tagCompound.getIntArray("CoordsCurrent");
				addToWand.enabled = ((ItemInternetWand)heldItem.getItem()).isMachineValid(mc.theWorld, heldItem, a[0], a[1], a[2]);

				TileEntity te = mc.theWorld.getBlockTileEntity(a[0], a[1], a[2]);

				addSelected.enabled = selectedButtons != 0;
				// Go over each machine button
				for(MachineButton button : machineButtons)
					if(button != null && (selectedButtons & 1 << button.buttonID) != 0) // If this button is selected
						// If the selected machine is not valid for the block that was clicked
						if(!(te instanceof TEMEnergyStorage && button.isElectric || te instanceof TEMComputer && button.isWireless))
							addSelected.enabled = false; // Set the button to false
			}
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 0) { // If it was a left-click
			for(int i = 0; i < Consts.WAND_MAX_COORDS; i++) {
				MachineButton button = machineButtons[i];
				if(button != null && Funcs.mouseInZone(mouseX, mouseY, button.xPos, button.yPos, button.width, button.height)) { // If this button was clicked
					if(!isCtrlKeyDown() && !isShiftKeyDown()) // If the CTRL or Shift key wasn't held, set all buttons to 0
						selectedButtons = 0;
					selectedButtons ^= 1 << i; // Invert its selected state

					if(isShiftKeyDown()) {
						int firstOne = -1; // The index of the position of the rightmost 1 in the binary integer
						int lastOne = -1; // The index of the position of the leftmost 1 in the binary integer
						for(int j = 0; j < Consts.WAND_MAX_COORDS; j++) { // Go over each bit in the integer
							if((selectedButtons & 1 << j) != 0) { // If the bit is 1
								if(firstOne == -1) // If the leftmost 1 has not been found already
									firstOne = j; // This is the leftmost 1
								lastOne = j; // This is the rightmost 1 (so far)
							}
						}
						if(firstOne != -1) // If there is a 1 in the integer
							// Loop from the first 1 to the last 1
							for(int j = firstOne; j <= lastOne; j++)
								selectedButtons |= 1 << j; // Set each bit to 1
					}
					break;
				}
			}
			setButtons();
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch(button.id) {
			case 0:
				InfiniteAlloys.instance.proxy.initLocalization(); // TODO: Remove this line, it is for debug only!!!!
				helpEnabled = !helpEnabled;
				break;
			case 1:
				ItemStack heldItem = mc.thePlayer.getHeldItem();
				int[] a = heldItem.getTagCompound().getIntArray("CoordsCurrent");
				PacketDispatcher.sendPacketToServer(PacketAddToWand.getPacket(a[0], (short)a[1], a[2]));
				((ItemInternetWand)heldItem.getItem()).addMachineToWand(mc.theWorld, heldItem, a[0], a[1], a[2]);
				break;
			case 2:
				break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	/** A button that represents a machine with its texture and coordinates */
	private class MachineButton {

		final int width = 140;
		final int height = 15;

		int buttonID;
		int xPos;
		int yPos;

		int machineID;
		int machineX;
		int machineY;
		int machineZ;
		boolean isElectric;
		boolean isWireless;

		MachineButton(int buttonID, int xPos, int yPos, int machineID, int machineX, int machineY, int machineZ) {
			super();
			this.buttonID = buttonID;
			this.xPos = xPos;
			this.yPos = yPos;
			this.machineID = machineID;
			this.machineX = machineX;
			this.machineY = machineY;
			this.machineZ = machineZ;
			isElectric = MachineHelper.isElectric(mc.theWorld, machineX, machineY, machineZ);
			isWireless = MachineHelper.isWireless(mc.theWorld, machineX, machineY, machineZ);
		}

		void drawButton() {
			Funcs.bindTexture(GuiMachine.extras);

			if((selectedButtons & 1 << buttonID) != 0) { // If this button is selected, draw a box around it
				drawHorizontalLine(xPos - 1, xPos + width + 1, yPos - 1, 0xff22aa22);
				drawHorizontalLine(xPos - 1, xPos + width + 1, yPos + height + 1, 0xff22aa22);
				drawVerticalLine(xPos - 1, yPos + height + 1, yPos - 1, 0xff22aa22);
				drawVerticalLine(xPos + width + 1, yPos + height + 1, yPos - 1, 0xff22aa22);
				drawRect(xPos, yPos, xPos + width + 1, yPos + height + 1, 0x6022aa22);
			}

			GL11.glColor3f(1F, 1F, 1F); // Reset the color
			// If the machine is electrical
			if(isElectric)
				Funcs.drawTexturedModalRect(GuiInternetWand.this, xPos + 121, yPos + 4, GuiMachine.ENERGY_ICON_2); // Render an electricity icon on the button

			// If the machine is wireless
			if(isWireless)
				Funcs.drawTexturedModalRect(GuiInternetWand.this, xPos + 131, yPos + 4, GuiMachine.WIRELESS_ICON); // Render a wireless icon on the button

			// Draw the string for the coordinates
			String display = "X: " + machineX + "  Y: " + machineY + "  Z: " + machineZ;
			mc.fontRenderer.drawStringWithShadow(display, xPos + 70 - (mc.fontRenderer.getStringWidth(display) / 2), yPos + 4, 0xffffff);

			itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(Blocks.machineID, 1, machineID), xPos, yPos);
		}
	}
}
