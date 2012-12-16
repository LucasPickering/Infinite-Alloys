package infinitealloys.client;

import infinitealloys.Point;
import infinitealloys.inventory.ContainerXray;
import infinitealloys.tile.TileEntityXray;

import java.util.ArrayList;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GuiXray extends GuiMachine {

	private TileEntityXray tex;

	/** Whether or not each coord set has the correct block to display. y, x, z */
	private boolean[][][] blockLocs;
	private GuiBlockButton[] blockButtons;
	private int selectedButton;

	public GuiXray(InventoryPlayer inventoryPlayer, TileEntityXray tileEntity) {
		super(176, 238, tileEntity, new ContainerXray(inventoryPlayer, tileEntity), "xray");
		tex = tileEntity;
		blockLocs = new boolean[tem.yCoord][tex.range * 2 + 1][tex.range * 2 + 1];
		blockButtons = new GuiBlockButton[tem.yCoord];
	}

	@Override
	public void initGui() {
		super.initGui();
		setButtons();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		for(GuiBlockButton button : blockButtons)
			// TODO: remove this line
			if(button != null)
				button.drawButton();
		blockButtons[0].drawButton();
		for(boolean[][] grid : blockLocs) {
			if(grid != null) {

			}
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 0) {
			for(int i = 0; i < blockButtons.length; i++) {
				// TODO: remove this line
				if(blockButtons[i] == null) return;
				if(blockButtons[i].mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
					blockButtons[selectedButton].activated = false;
					selectedButton = i;
					blockButtons[i].activated = true;
				}
			}
		}
	}

	public void setButtons() {
		ItemStack searchedBlock = tex.inventoryStacks[0];
		if(searchedBlock != null) {
			ArrayList<Point> detectedBlocks = tex.getDetectedBlocks();
			int[] blockCounts = new int[blockLocs.length];
			for(Point block : detectedBlocks)
				blockLocs[block.y][block.x + tex.range][block.z + tex.range] = true;
			for(int i = 0; i < blockLocs.length; i++)
				for(int j = 0; j < blockLocs[i].length; j++)
					for(int k = 0; k < blockLocs[i][j].length; k++)
						if(blockLocs[i][j][k])
							blockCounts[i]++;
			for(int i = 0; i < blockButtons.length; i++)
				blockButtons[i] = new GuiBlockButton(mc, itemRenderer, 7, i * 20 + 27, tex.inventoryStacks[0].itemID, blockCounts[i], tex.inventoryStacks[0].getItemDamage(), i);
		}
		// TODO: remove this line
		blockButtons[0] = new GuiBlockButton(mc, itemRenderer, 7, 27, 45, 10, 0, 42);
	}
}
