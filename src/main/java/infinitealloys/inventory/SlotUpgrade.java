package infinitealloys.inventory;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import infinitealloys.tile.TileEntityMachine;

public final class SlotUpgrade extends Slot {

  public SlotUpgrade(TileEntityMachine tem, int index, int x, int y) {
    super(tem, index, x, y);
  }

  @Override
  public int getSlotStackLimit() {
    return 1;
  }

  @Override
  public boolean isItemValid(ItemStack itemstack) {
    return ((TileEntityMachine) inventory).isUpgradeValid(itemstack);
  }
}
