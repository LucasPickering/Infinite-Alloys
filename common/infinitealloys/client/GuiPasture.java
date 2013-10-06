package infinitealloys.client;

import java.util.Arrays;
import cpw.mods.fml.common.network.PacketDispatcher;
import infinitealloys.handlers.PacketHandler;
import infinitealloys.inventory.ContainerMachine;
import infinitealloys.tile.TileEntityPasture;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiPasture extends GuiMachine {

	public TileEntityPasture tep;

	/** The buttons to enable/disable the mob trapping/repulsion in the order of chicken, cow, pig, sheep, creeper, skeleton, spider, zombie */
	private GuiButton[] mobButtons = new GuiButton[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];

	public GuiPasture(InventoryPlayer inventoryPlayer, TileEntityPasture tileEntity) {
		super(210, 186, tileEntity, new ContainerMachine(inventoryPlayer, tileEntity, 13, 94, 141, 44), "pasture");
		tep = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		// Initialize each mob button with the text "<off/trap/repel>"
		for(int i = 0; i < Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS; i++) {
			buttonList.add(mobButtons[i] = new GuiButton(i + 1, width / 2 - (i < Consts.PASTURE_ANIMALS ? 53 : 23),
					height / 2 - 88 + i % Consts.PASTURE_ANIMALS * 22, 24, 20, Funcs.getLoc("pasture.mode." + tep.mobActions[i])));
			mobButtons[i].enabled = tep.mobActions[i] > 0 || tep.hasFreeSpots();
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		// If a mob button is clicked
		if(0 < button.id)
			// Cycle the value of the field associated with the button
			tep.mobActions[button.id - 1] = (byte)(++tep.mobActions[button.id - 1] % Consts.PASTURE_MODES);

		// Update all buttons when any is clicked
		updateButtons();
	}

	/** Called when buttons are initialized or when any button is clicked, updates text, TE values, etc. */
	private void updateButtons() {
		// Enable each animal button if it is set to on or if there is space to turn it on, and if the mode is Animals or the mode is Both
		for(int i = 0; i < Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS; i++) {
			mobButtons[i].enabled = tep.mobActions[i] > 0 || tep.hasFreeSpots();
			mobButtons[i].displayString = Funcs.getLoc("pasture.mode." + tep.mobActions[i]);
		}

		// Send a packet to the server to sync the settings
		PacketDispatcher.sendPacketToServer(PacketHandler.getTEPacketToServer(tep));
	}
}
