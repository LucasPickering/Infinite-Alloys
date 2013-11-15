package infinitealloys.client.gui;

import infinitealloys.tile.TEMEnergyStorage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiEnergyStorage extends GuiMachine {

	public TEMEnergyStorage tees;

	public GuiEnergyStorage(InventoryPlayer inventoryPlayer, TEMEnergyStorage tileEntity) {
		super(176, 176, inventoryPlayer, tileEntity);
		tees = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
	}
}
