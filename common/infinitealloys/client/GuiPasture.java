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
		super(200, 176, tileEntity, new ContainerMachine(inventoryPlayer, tileEntity), "pasture");
		tep = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();

		// Initialize the mode button with the text "Mode: <mode>", where <mode> is Off, Animals, Monsters, Both
		buttonList.add(modeButton = new GuiButton(0, width / 2 + 44, height / 2 - 83, 32, 20, Funcs.getLoc("general.mode", "/: ", "pasture.mode" + tep.mode)));

		// Initialize each animal button with the text "<animal>: <off/on>"
		for(int i = 0; i < Consts.PASTURE_ANIMALS; i++) {
			buttonList.add(animalButtons[i] = new GuiButton(i + 1, width / 2 + 20, height / 2 - 83 + i * 28, 32, 20, Funcs.getLoc("pasture.animal." + i, "/: ", tep.animals[i] ? "general.on"
					: "general.off")));
			animalButtons[i].enabled = tep.animals[i] || tep.hasFreeSpots();
		}

		// Initialize each monster button with the text "<monster>: <off/on>"
		for(int i = 0; i < Consts.PASTURE_MONSTERS; i++) {
			buttonList.add(monsterButtons[i] = new GuiButton(i + Consts.PASTURE_ANIMALS + 1, width / 2 + 80, height / 2 - 83 + i * 28, 32, 20, Funcs.getLoc("pasture.monster." + i, "/: ",
					tep.monsters[i] ? "general.on" : "general.off")));
			monsterButtons[i].enabled = tep.monsters[i] || tep.hasFreeSpots();
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		// If the Mode button is clicked
		if(button.id == 0)
			// Increase mode by one, and set to 0 if it has moved up to the mode amount (cycle 0-MODES)
			tep.mode = ++tep.mode % Consts.PASTURE_MODES;

		// If an Animal button is clicked
		if(0 < button.id && button.id <= Consts.PASTURE_ANIMALS)
			// Toggle the value of the field associated with the button
			tep.animals[button.id - 1] = !tep.animals[button.id - 1];

		// If a Monster button is clicked
		if(Consts.PASTURE_ANIMALS < button.id && button.id <= Consts.PASTURE_MONSTERS)
			// Toggle the value of the field associated with the button
			tep.animals[button.id - Consts.PASTURE_ANIMALS] = !tep.animals[button.id - Consts.PASTURE_ANIMALS];

		// Update all buttons when any is clicked
		updateButtons();
	}

	/** Called when buttons are initialized or when any button is clicked, updates text, TE values, etc. */
	private void updateButtons() {
		// Update the mode button's text with the mode to "Mode: <mode>", where <mode> is Off, Animals, Monsters, Both
		modeButton.displayString = Funcs.getLoc("general.mode", "/: ", "pasture.mode" + tep.mode);

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
		for(int i = 0; i < Consts.PASTURE_ANIMALS; i++)
			animalButtons[i].enabled = tep.animals[i] || tep.hasFreeSpots() && tep.mode == tep.MODE_ANIMALS || tep.mode == tep.MODE_BOTH;

		// Enable each monster button if it is set to on or if there is space to turn it on, and if the mode is Animals or the mode is Both
		for(int i = 0; i < Consts.PASTURE_MONSTERS; i++)
			monsterButtons[i].enabled = tep.monsters[i] || tep.hasFreeSpots() && tep.mode == tep.MODE_MONSTERS || tep.mode == tep.MODE_BOTH;
	}
}
