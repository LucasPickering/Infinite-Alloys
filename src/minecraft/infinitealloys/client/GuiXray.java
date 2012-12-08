package infinitealloys.client;

import infinitealloys.inventory.ContainerXray;
import infinitealloys.tile.TileEntityXray;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;

public class GuiXray extends GuiMachine {

	private TileEntityXray tex;

	/** The selection for the axis to view; x=0, y=1, z=2 */
	private int axisView;

	private ItemStack[][][] blocks;

	public GuiXray(InventoryPlayer inventoryPlayer, TileEntityXray tileEntity) {
		super(176, 238, tileEntity, new ContainerXray(inventoryPlayer, tileEntity), "xray");
		tex = tileEntity;
		blocks = new ItemStack[tex.yCoord][tex.range * 2 + 1][tex.range * 2 + 1];
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

	}
}
