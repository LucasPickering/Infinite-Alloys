package infinitealloys.client.gui;

import net.minecraft.inventory.Container;
import infinitealloys.tile.TEMGenerator;

public class GuiGenerator extends GuiMachine {

	public TEMGenerator teg;

	public java.awt.Point fire = new java.awt.Point(30, 75);

	public GuiGenerator(int xSize, int ySize, TEMGenerator tileEntity, Container container, String texture) {
		super(xSize, ySize, tileEntity, container, texture);
		teg = tileEntity;
	}
}
