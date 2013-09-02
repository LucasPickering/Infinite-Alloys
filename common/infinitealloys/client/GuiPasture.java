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

	/** The button to cycle between the different modes */
	private GuiButton modeButton;
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

		// Initialize the mode button with the text "Mode: <mode>", where <mode> is Off, Animals, Monsters, Both
		buttonList.add(modeButton = new GuiButton(1, width / 2 + 23, height / 2 - 88, 52, 20, Funcs.getLoc("pasture.mode." + tep.mode)));

		// Initialize each animal button with the text "<animal>: <off/on>"
		for(int i = 0; i < Consts.PASTURE_ANIMALS; i++) {
			buttonList.add(animalButtons[i] = new GuiButton(i + 2, width / 2 - 45, height / 2 - 88 + i * 22, 24, 20, Funcs.getLoc(tep.animals[i] ? "general.on" : "general.off")));
			animalButtons[i].enabled = (tep.animals[i] || tep.hasFreeSpots()) && (tep.mode == tep.MODE_ANIMALS || tep.mode == tep.MODE_BOTH);
		}

		// Initialize each monster button with the text "<monster>: <off/on>"
		for(int i = 0; i < Consts.PASTURE_MONSTERS; i++) {
			buttonList.add(monsterButtons[i] = new GuiButton(i + Consts.PASTURE_ANIMALS + 2, width / 2 - 15, height / 2 - 88 + i * 22, 24, 20, Funcs.getLoc(tep.monsters[i] ? "general.on"
					: "general.off")));
			monsterButtons[i].enabled = (tep.monsters[i] || tep.hasFreeSpots()) && (tep.mode == tep.MODE_MONSTERS || tep.mode == tep.MODE_BOTH);
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		// If the Mode button is clicked
		if(button.id == 1)
			// Increase mode by one, and set to 0 if it has moved up to the mode amount (cycle 0-MODES)
			tep.mode = (byte)(++tep.mode % Consts.PASTURE_MODES);

		// If an Animal button is clicked
		if(1 < button.id && button.id <= Consts.PASTURE_ANIMALS + 1)
			// Toggle the value of the field associated with the button
			tep.animals[button.id - 2] = !tep.animals[button.id - 2];

		// If a Monster button is clicked
		if(Consts.PASTURE_ANIMALS + 1 < button.id && button.id <= Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS + 1)
			// Toggle the value of the field associated with the button
			tep.monsters[button.id - Consts.PASTURE_ANIMALS - 2] = !tep.monsters[button.id - Consts.PASTURE_ANIMALS - 2];

		// Update all buttons when any is clicked
		updateButtons();
	}

	/** Called when buttons are initialized or when any button is clicked, updates text, TE values, etc. */
	private void updateButtons() {
		// Update the mode button's text with the mode to "<mode>", where <mode> is Off, Animals, Monsters, Both
		modeButton.displayString = Funcs.getLoc("pasture.mode." + tep.mode);

		switch(tep.mode) {
		// If the mode if Off, set all animal and monster values to false
			case TileEntityPasture.MODE_OFF:
				Arrays.fill(tep.animals, false);
				Arrays.fill(tep.monsters, false);
				break;
			// If the mode is Animals, set all monster values to false
			case TileEntityPasture.MODE_ANIMALS:
				Arrays.fill(tep.monsters, false);
				break;
			// If the mode is Monsters, set all animal values to false
			case TileEntityPasture.MODE_MONSTERS:
				Arrays.fill(tep.animals, false);
				break;
		}

		// Enable each animal button if it is set to on or if there is space to turn it on, and if the mode is Animals or the mode is Both
		for(int i = 0; i < Consts.PASTURE_ANIMALS; i++) {
			animalButtons[i].enabled = (tep.animals[i] || tep.hasFreeSpots()) && (tep.mode == tep.MODE_ANIMALS || tep.mode == tep.MODE_BOTH);
			animalButtons[i].displayString = Funcs.getLoc(tep.animals[i] ? "general.on" : "general.off");
		}

		// Enable each monster button if it is set to on or if there is space to turn it on, and if the mode is Animals or the mode is Both
		for(int i = 0; i < Consts.PASTURE_MONSTERS; i++) {
			monsterButtons[i].enabled = (tep.monsters[i] || tep.hasFreeSpots()) && (tep.mode == tep.MODE_MONSTERS || tep.mode == tep.MODE_BOTH);
			monsterButtons[i].displayString = Funcs.getLoc(tep.monsters[i] ? "general.on" : "general.off");
		}
	}
}
