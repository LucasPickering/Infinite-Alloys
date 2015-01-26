package infinitealloys.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import infinitealloys.tile.TileEntityMachine;

public class ContainerEnergyStorage extends ContainerMachine {

  public ContainerEnergyStorage(InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
    super(tileEntity, 10);

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        addSlotToContainer(
            new SlotMachine(inventory, inventory.getEnumMachine(), x + y * 3, 13 + x * 18,
                            22 + y * 18));
      }
    }

    initSlots(inventoryPlayer, 27, 94, 185, 22);
  }
}
