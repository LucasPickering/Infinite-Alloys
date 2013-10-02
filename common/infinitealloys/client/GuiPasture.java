package infinitealloys.client;

import java.util.Arrays;
import infinitealloys.inventory.ContainerMachine;
import infinitealloys.tile.TileEntityPasture;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiPasture extends GuiMachine {

	public TileEntityPasture tep;

	/** The buttons to enable/disable the animal trapping in the order of chicken, cow, pig, sheep */
	private GuiButton[] animalButtons = new GuiButton[Consts.PASTURE_ANIMALS];
	/** The buttons to enable/disable the monster repulsion in the order of creeper, skeleton, spider, zombie */
	private GuiButton[] monsterButtons = new GuiButton[Consts.PASTURE_MONSTERS];

	public GuiPasture(InventoryPlayer inventoryPlayer, TileEntityPasture tileEntity) {
		super(210, 186, tileEntity, new ContainerMachine(inventoryPlayer, tileEntity, 13, 94, 141, 44), "pasture");
		tep = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();

		// Initialize each animal button with the text "<animal>: <off/on>"
		for(int i = 0; i < Consts.PASTURE_ANIMALS; i++) {
			buttonList.add(animalButtons[i] = new GuiButton(i + 1, width / 2 - 53, height / 2 - 88 + i * 22, 24, 20, Funcs.getLoc("pasture.mode." + tep.animals[i])));
			animalButtons[i].enabled = tep.animals[i] > 0 || tep.hasFreeSpots();
		}

		// Initialize each monster button with the text "<monster>: <off/on>"
		for(int i = 0; i < Consts.PASTURE_MONSTERS; i++) {
			buttonList.add(monsterButtons[i] = new GuiButton(i + Consts.PASTURE_ANIMALS + 1, width / 2 - 23, height / 2 - 88 + i * 22, 24, 20, Funcs.getLoc("pasture.mode." + tep.monsters[i])));
			monsterButtons[i].enabled = tep.monsters[i] > 0 || tep.hasFreeSpots();
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		// If an Animal button is clicked
		if(1 < button.id && button.id <= Consts.PASTURE_ANIMALS)
			// Cycle the value of the field associated with the button
			tep.animals[button.id - 1] = (byte)(++tep.animals[button.id - 1] % Consts.PASTURE_MODES);

		// If a Monster button is clicked
		if(Consts.PASTURE_ANIMALS < button.id && button.id <= Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS)
			// Cycle the value of the field associated with the button
			tep.monsters[button.id - Consts.PASTURE_ANIMALS - 1] = (byte)(++tep.monsters[button.id - Consts.PASTURE_ANIMALS - 1] % Consts.PASTURE_MODES);

		// Update all buttons when any is clicked
		updateButtons();
	}

	/** Called when buttons are initialized or when any button is clicked, updates text, TE values, etc. */
	private void updateButtons() {
		// Enable each animal button if it is set to on or if there is space to turn it on, and if the mode is Animals or the mode is Both
		for(int i = 0; i < Consts.PASTURE_ANIMALS; i++) {
			animalButtons[i].enabled = tep.animals[i] > 0 || tep.hasFreeSpots();
			animalButtons[i].displayString = Funcs.getLoc("pasture.mode." + tep.animals[i]);
		}

		// Enable each monster button if it is set to on or if there is space to turn it on, and if the mode is Animals or the mode is Both
		for(int i = 0; i < Consts.PASTURE_MONSTERS; i++) {
			monsterButtons[i].enabled = tep.monsters[i] > 0 || tep.hasFreeSpots();
			monsterButtons[i].displayString = Funcs.getLoc("pasture.mode." + tep.monsters[i]);
		}
	}
}
