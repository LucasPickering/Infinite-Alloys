package infinitealloys.client.gui;

import infinitealloys.network.PacketTEClientToServer;
import infinitealloys.tile.TEHComputer;
import infinitealloys.util.Funcs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiComputer extends GuiMachine {

	public GuiComputer(InventoryPlayer inventoryPlayer, TEHComputer tileEntity) {
		super(176, 166, inventoryPlayer, tileEntity);
	}
}
