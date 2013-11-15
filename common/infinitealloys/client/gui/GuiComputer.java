package infinitealloys.client.gui;

import infinitealloys.handlers.PacketHandler;
import infinitealloys.tile.TEMComputer;
import infinitealloys.util.Funcs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiComputer extends GuiMachine {

	public TEMComputer tec;

	public GuiComputer(InventoryPlayer inventoryPlayer, TEMComputer tileEntity) {
		super(176, 176, inventoryPlayer, tileEntity);
		tec = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		// The button to toggle auto-searching
		buttonList.add(new GuiButton(1, width / 2 + 44, height / 2 - 83, 32, 20, "Auto-Search: " + (tec.autoSearch ? Funcs.getLoc("general.on") : Funcs.getLoc("general.off"))));
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if(button.id == 1) {
			tec.autoSearch = !tec.autoSearch;
			PacketDispatcher.sendPacketToServer(PacketHandler.getTEPacketToServer(tec));
		}
	}
}
