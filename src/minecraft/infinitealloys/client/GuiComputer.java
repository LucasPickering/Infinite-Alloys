package infinitealloys.client;

import java.util.ArrayList;
import infinitealloys.ContainerMachine;
import infinitealloys.IAValues;
import infinitealloys.TileEntityComputer;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Vec3;

public class GuiComputer extends GuiMachine {

	private TileEntityComputer tec;
	private ArrayList<GuiMachineButton> machineButtons = new ArrayList<GuiMachineButton>();

	public GuiComputer(InventoryPlayer inventoryPlayer, TileEntityComputer tileEntity) {
		super(tileEntity, new ContainerMachine(inventoryPlayer, tileEntity));
		xSize = 176;
		ySize = 216;
		tec = tileEntity;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		super.drawScreen(mouseX, mouseY, partialTick);
		for(GuiMachineButton button : machineButtons)
			button.drawButton(mc, mouseX, mouseY);
		updateGui();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		int k = mc.renderEngine.getTexture(IAValues.TEXTURE_PATH + "guicomputer.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(k);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}

	private void updateGui() {
		machineButtons.clear();
		for(int i = 0; i < tec.networkCoords.size(); i++) {
			Vec3 coords = tec.networkCoords.get(i);
			machineButtons.add(new GuiMachineButton(width / 2 - 56 + i / 5 * 46, height / 2 - 103 + i % 5 * 24, (int)coords.xCoord, (int)coords.yCoord, (int)coords.zCoord));
		}
	}
}
