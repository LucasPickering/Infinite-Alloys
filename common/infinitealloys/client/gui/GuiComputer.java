package infinitealloys.client.gui;

import infinitealloys.tile.TEMComputer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiComputer extends GuiMachine {

	public GuiComputer(InventoryPlayer inventoryPlayer, TEMComputer tileEntity) {
		super(176, 166, inventoryPlayer, tileEntity);
	}

	@Override
	protected ColoredLine[] getNetworkStatuses() {
		return null;
	}
}
