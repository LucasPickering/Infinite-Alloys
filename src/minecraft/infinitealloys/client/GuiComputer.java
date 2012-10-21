package infinitealloys.client;

import infinitealloys.ContainerMachine;
import infinitealloys.TileEntityComputer;
import infinitealloys.handlers.PacketHandler;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.src.GuiButton;
import net.minecraft.src.InventoryPlayer;

public class GuiComputer extends GuiMachine {

	private TileEntityComputer tec;
	private GuiButton[] idButtons;

	public GuiComputer(InventoryPlayer inventoryPlayer, TileEntityComputer tileEntity) {
		super(tileEntity, new ContainerMachine(inventoryPlayer, tileEntity));
		xSize = 176;
		ySize = 216;
		tec = tileEntity;
		idButtons = new GuiButton[tec.maxIdCount];
	}

	@Override
	public void initGui() {
		super.initGui();
		for(int i = 0; i < idButtons.length; i++)
			controlList.add(idButtons[i] = new GuiButtonID(i + 1, width / 2 - 56 + i / 5 * 46, height / 2 - 103 + i % 5 * 24, new Integer(tec.selectedIDs[i]).toString()));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		int k = mc.renderEngine.getTexture("/infinitealloys/gfx/guicomputer.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(k);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for(int i = 0; i < idButtons.length; i++) {
			if(idButtons[i].mousePressed(mc, mouseX, mouseY)) {
				if(mouseButton == 0)
					tec.selectedIDs[i] = (byte)Math.min(tec.selectedIDs[i] + 1, Byte.MAX_VALUE);
				else if(mouseButton == 1) {
					tec.selectedIDs[i] = (byte)Math.max(tec.selectedIDs[i] - 1, 0);
					mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				}
				PacketDispatcher.sendPacketToServer(PacketHandler.getPacketToServer(tec));
				idButtons[i].displayString = new Byte(tec.selectedIDs[i]).toString();
			}
		}
	}
}
