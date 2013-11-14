package infinitealloys.client.gui;

import infinitealloys.tile.TEMGenerator;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiGenerator extends GuiMachine {

	public TEMGenerator teg;

	public java.awt.Point fire = new java.awt.Point(30, 75);

	public GuiGenerator(InventoryPlayer inventoryPlayer, TEMGenerator tileEntity) {
		super(176, 176,inventoryPlayer, tileEntity);
		teg = tileEntity;
	}
}
