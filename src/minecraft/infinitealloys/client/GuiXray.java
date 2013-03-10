package infinitealloys.client;

import infinitealloys.core.Point;
import infinitealloys.handlers.PacketHandler;
import infinitealloys.inventory.ContainerXray;
import infinitealloys.tile.TileEntityXray;
import java.util.ArrayList;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GuiXray extends GuiMachine {

	private final int BUTTON_LIST_X = 7;
	private final int BUTTON_LIST_Y = 50;

	private TileEntityXray tex;

	/** Whether or not each coord set has the correct block to display. y, x, z */
	private boolean[][][] blockLocs;
	private GuiBlockButton[] blockButtons = new GuiBlockButton[0];
	private GuiButton searchButton;
	private int selectedButton = -1;

	public GuiXray(InventoryPlayer inventoryPlayer, TileEntityXray tileEntity) {
		super(196, 238, tileEntity, new ContainerXray(inventoryPlayer, tileEntity), "xray");
		tex = tileEntity;
		progressBar.setLocation(54, 5);
		blockLocs = new boolean[tem.yCoord][tex.range * 2 + 1][tex.range * 2 + 1];
	}

	@Override
	public void initGui() {
		super.initGui();
		setButtons();
		controlList.add(searchButton = new GuiButton(0, width / 2 - 30, height / 2 - 92, 80, 20, "Search"));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		for(GuiBlockButton button : blockButtons)
			button.drawButton();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 0) {
			for(int i = 0; i < blockButtons.length; i++) {
				if(blockButtons[i].mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
					if(selectedButton >= 0)
						blockButtons[selectedButton].activated = false;
					if(selectedButton != i) {
						selectedButton = i;
						blockButtons[i].activated = true;
					}
					else
						selectedButton = -1;
				}
			}
		}
	}

	public void setButtons() {
		if(tex.inventoryStacks[0] != null) {
			int[] blockCounts = new int[blockLocs.length];
			ArrayList<Integer> levels = new ArrayList<Integer>();
			for(Point block : tex.getDetectedBlocks()) {
				blockLocs[block.y][block.x + tex.range][block.z + tex.range] = true;
				if(blockCounts[block.y]++ == 0)
					levels.add(block.y);
			}
			blockButtons = new GuiBlockButton[levels.size()];
			for(int i = 0; i < blockButtons.length; i++)
				blockButtons[i] = new GuiBlockButton(mc, itemRenderer, i / 5 * 40 + 7, i % 5 * 20 + 50, tex.inventoryStacks[0].itemID,
						blockCounts[levels.get(i)], tex.inventoryStacks[0].getItemDamage(), levels.get(i));
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button.id == 0) {
			PacketDispatcher.sendPacketToServer(PacketHandler.getPacketSearch(tex.xCoord, tex.yCoord, tex.zCoord));
			tex.searching = true;
		}
	}
}
