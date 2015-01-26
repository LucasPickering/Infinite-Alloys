package infinitealloys.client.gui;

import net.minecraft.entity.player.InventoryPlayer;

import infinitealloys.tile.TEMComputer;

public class GuiComputer extends GuiMachine {

  public GuiComputer(InventoryPlayer inventoryPlayer, TEMComputer tileEntity) {
    super(176, 166, inventoryPlayer, tileEntity);
  }

  @Override
  protected ColoredLine[] getNetworkStatuses() {
    return null;
  }
}
