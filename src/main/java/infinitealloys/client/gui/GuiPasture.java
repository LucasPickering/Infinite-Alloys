package infinitealloys.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import infinitealloys.tile.TEEPasture;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;

public class GuiPasture extends GuiElectric {

  public TEEPasture tep;

  /**
   * The buttons to enable/disable the mob trapping/repulsion in the order they're defined in {@link
   * infinitealloys.tile.TEEPasture#mobClasses}
   */
  private final GuiButton[] mobButtons = new GuiButton[TEEPasture.mobClasses.length];

  public GuiPasture(InventoryPlayer inventoryPlayer, TEEPasture tileEntity) {
    super(210, 186, inventoryPlayer, tileEntity);
    tep = tileEntity;
    networkIcon = new java.awt.Point(18, 40);
  }

  @Override
  public void initGui() {
    super.initGui();
    // Initialize each mob button with the text "<off/trap/repel>"
    for (int i = 0; i < mobButtons.length; i++) {
      buttonList.add(
          mobButtons[i] = new GuiButton(i + 1, width / 2 - (i < mobButtons.length / 2 ? 53 : 23),
                                        height / 2 - 88 + i % (mobButtons.length / 2) * 22, 24, 20,
                                        Funcs.getLoc("machine.pasture.mode." + tep.mobActions[i])));
      mobButtons[i].enabled = tep.mobActions[i] > 0 || tep.hasFreeSpots();
    }
  }

  @Override
  public void actionPerformed(GuiButton button) {
    super.actionPerformed(button);

    if (button.id > 0) { // If a mob button is clicked
      tep.mobActions[button.id - 1] =
          (byte) (++tep.mobActions[button.id - 1]
                  % Consts.PASTURE_MODES); // Cycle the value of the field associated with the button
      updateButtons(); // Update all buttons when any is clicked (other than the help button
    }
  }

  /**
   * Called when buttons are initialized or when any button is clicked, updates text, TE values,
   * etc.
   */
  private void updateButtons() {
    // Enable each animal button if it is set to on or if there is space to turn it on, and if the mode is Animals or the mode is Both
    for (int i = 0; i < mobButtons.length; i++) {
      mobButtons[i].enabled = tep.mobActions[i] > 0 || tep.hasFreeSpots();
      mobButtons[i].displayString = Funcs.getLoc("machine.pasture.mode." + tep.mobActions[i]);
    }

    tep.syncToServer();
    ; // Send a packet to the server to sync the settings
  }
}
