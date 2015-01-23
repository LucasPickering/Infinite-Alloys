package infinitealloys.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import infinitealloys.tile.TileEntityMachine;

public class ContainerComputer extends ContainerMachine {

  public ContainerComputer(InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
    super(inventoryPlayer, tileEntity, 8, 84, 140, 43);
  }
}
