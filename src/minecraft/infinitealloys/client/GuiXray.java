package infinitealloys.client;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.handlers.PacketHandler;
import infinitealloys.inventory.ContainerXray;
import infinitealloys.tile.TileEntityXray;
import infinitealloys.util.Point;
import java.util.ArrayList;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiXray extends GuiMachine {

	private TileEntityXray tex;

	private GuiBlockButton[] blockButtons = new GuiBlockButton[0];
	private GuiButton searchButton;

	public GuiXray(InventoryPlayer inventoryPlayer, TileEntityXray tileEntity) {
		super(196, 238, tileEntity, new ContainerXray(inventoryPlayer, tileEntity), "xray");
		tex = tileEntity;
		progressBar.setLocation(54, 5);
	}

	@Override
	public void initGui() {
		super.initGui();
		setButtons();
		buttonList.add(searchButton = new GuiButton(0, width / 2 - 30, height / 2 - 92, 80, 20, "Search"));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		searchButton.enabled = tex.inventoryStacks[0] != null;
		setButtons();
		for(GuiBlockButton button : blockButtons)
			button.drawButton();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 0) {
			for(int i = 0; i < blockButtons.length; i++) {
				if(blockButtons[i].mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
					if(tex.selectedButton >= 0)
						blockButtons[tex.selectedButton].activated = false;
					if(tex.selectedButton != i) {
						tex.selectedButton = i;
						blockButtons[i].activated = true;
						InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
						for(Point block : tex.getDetectedBlocks()) {
							if(block.y == blockButtons[i].getYValue()) {
								block.x += tex.xCoord;
								block.z += tex.zCoord;
								InfiniteAlloys.proxy.gfxHandler.xrayBlocks.add(block);
							}
						}
					}
					else {
						tex.selectedButton = -1;
						InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
					}
				}
			}
		}
	}

	public void setButtons() {
		if(tex.inventoryStacks[0] != null) {
			int[] blockCounts = new int[tem.yCoord];
			ArrayList<Integer> levels = new ArrayList<Integer>();
			for(Point block : (ArrayList<Point>)tex.getDetectedBlocks())
				if(blockCounts[block.y]++ == 0)
					levels.add(block.y);
			blockButtons = new GuiBlockButton[levels.size()];
			for(int i = 0; i < blockButtons.length; i++)
				blockButtons[i] = new GuiBlockButton(mc, itemRenderer, i / 5 * 40 + 7, i % 5 * 20 + 50, tex.inventoryStacks[0].itemID,
						blockCounts[levels.get(i)], tex.inventoryStacks[0].getItemDamage(), levels.get(i));
			if(tex.selectedButton != -1)
				blockButtons[tex.selectedButton].activated = true;
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button.id == 0) {
			tex.selectedButton = -1;
			PacketDispatcher.sendPacketToServer(PacketHandler.getPacketSearch(tex.xCoord, tex.yCoord, tex.zCoord));
			tex.searching = true;
		}
	}
}
