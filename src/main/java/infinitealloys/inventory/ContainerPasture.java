package infinitealloys.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import infinitealloys.tile.TileEntityMachine;

public final class ContainerPasture extends ContainerMachine {

  public ContainerPasture(InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
    super(inventoryPlayer, tileEntity, 13, 94, 141, 44);
  }
}
