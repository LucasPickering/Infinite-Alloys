package infinitealloys.client.gui;

import infinitealloys.inventory.ContainerUpgradable;
import infinitealloys.tile.TEUEnergyStorage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiEnergyStorage extends GuiUpgradable {

	public TEUEnergyStorage tees;

	public GuiEnergyStorage(InventoryPlayer inventoryPlayer, TEUEnergyStorage tileEntity) {
		super(176, 176, tileEntity, new ContainerUpgradable(inventoryPlayer, tileEntity, 8, 84, 140, 43), "computer");
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
