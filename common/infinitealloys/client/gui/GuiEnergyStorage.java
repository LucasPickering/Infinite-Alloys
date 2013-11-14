package infinitealloys.client.gui;

import infinitealloys.tile.TEUEnergyStorage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiEnergyStorage extends GuiUpgradable {

	public TEUEnergyStorage tees;

	public GuiEnergyStorage(InventoryPlayer inventoryPlayer, TEUEnergyStorage tileEntity) {
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
