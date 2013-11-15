package infinitealloys.client.gui;

import infinitealloys.tile.TEEGenerator;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiGenerator extends GuiElectric {

	public TEEGenerator teg;

	public GuiGenerator(InventoryPlayer inventoryPlayer, TEEGenerator tileEntity) {
		super(214, 176, inventoryPlayer, tileEntity);
		teg = tileEntity;
		progressBar.setLocation(71, 39);
	}
}
